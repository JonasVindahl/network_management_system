package dk.aau.network_management_system.materials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import dk.aau.network_management_system.auth.AuthenticatedUser;

// OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Materials", description = "Endpoints for recording material weighings. Admin access only.")
public class MaterialController {

    private final MaterialService service;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public MaterialController(MaterialService service, AuthenticatedUser authenticatedUser) {
        this.service = service;
        this.authenticatedUser = authenticatedUser;
    }

    @Operation(
        summary     = "Insert a material weighing",
        description = """
            Records a new material measurement for a worker.
            Updates bag state and cooperative stock accordingly.
            If `bagFull` is true, the bag state resets (current_kg → 0, is_begun → false).
            **Requires Admin role.**
            """,
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material inserted successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "\"Material inserted successfully\""))),
        @ApiResponse(responseCode = "400", description = "Missing or invalid required fields",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    { "status": 400, "error": "Bad Request", "message": "materialId must be specified" }
                    """))),
        @ApiResponse(responseCode = "403", description = "Caller does not have Admin role",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    { "status": 403, "error": "Forbidden", "message": "Only admins can insert material" }
                    """)))
    })
    @PostMapping("/insertMaterial")
    public ResponseEntity<String> insertMaterial(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Weighing details to record",
                required    = true,
                content     = @Content(
                    schema   = @Schema(implementation = MaterialRequest.class),
                    examples = @ExampleObject(name = "Example weighing", value = """
                        {
                          "materialId": 3,
                          "workerId":   7,
                          "amount":     12.50,
                          "bagFull":    false,
                          "deviceId":   1
                        }
                        """)
                )
            )
            @RequestBody MaterialRequest request) {

        if (!authenticatedUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Only admins can insert material");
        }

        if (request.getMaterialId() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "materialId must be specified");
        if (request.getWorkerId() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workerId must be specified");
        if (request.getAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than 0");
        if (request.getDeviceId() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deviceId must be specified");

        service.insertMaterial(authenticatedUser.getCooperativeId(), request);

        return ResponseEntity.ok("Material inserted successfully");
    }
}