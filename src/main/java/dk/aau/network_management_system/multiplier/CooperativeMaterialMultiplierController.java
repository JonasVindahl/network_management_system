package dk.aau.network_management_system.multiplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



// Selve REST API delen
@RestController
@RequestMapping("/api/cooperative-material-multipliers")
public class CooperativeMaterialMultiplierController {
    
    @Autowired
    private CooperativeMaterialMultiplierService service;
    
    // bruges til at oprette/opdatere data
    @PostMapping
    public ResponseEntity<CooperativeMaterialMultiplier> saveOrUpdateMultiplier(
            @Valid @RequestBody MultiplierDTO dto) {
        // når der modtages et post requesten konvertes det til dto objekt 
        CooperativeMaterialMultiplier result = service.saveOrUpdateMultiplier(
            dto.getCooperativeId(), 
            dto.getMaterialId(), 
            dto.getMultiplierValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
    
    // her er en get request til at hente data 
    @GetMapping("/cooperative/{cooperativeId}/material/{materialId}")
    public ResponseEntity<CooperativeMaterialMultiplier> getMultiplier(
            @PathVariable Long cooperativeId,
            @PathVariable Long materialId) {
        return service.getMultiplier(cooperativeId, materialId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    
    
        }
}