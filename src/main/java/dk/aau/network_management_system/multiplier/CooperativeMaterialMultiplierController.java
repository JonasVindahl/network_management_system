package dk.aau.network_management_system.multiplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;



// Selve REST API delen
@RestController
@RequestMapping("/api")
public class CooperativeMaterialMultiplierController {
    
    @Autowired
    private CooperativeMaterialMultiplierService service;
    

    @PostMapping("/multipliers")
    public ResponseEntity<CooperativeMaterialMultiplier> saveOrUpdateMultiplier(
            @Valid @RequestBody MultiplierDTO dto) {
        CooperativeMaterialMultiplier result = service.saveOrUpdateMultiplier(
            dto.getCooperativeId(), 
            dto.getMaterialId(), 
            dto.getMultiplierValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
    
    // her er en get request til at hente data 
    @GetMapping("/multipliers")
    public ResponseEntity<CooperativeMaterialMultiplier> getMultiplier(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam Long  materialId) {
        return service.getMultiplier(cooperativeId, materialId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    
    
        }
}