package dk.aau.network_management_system.multiplier;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CooperativeMaterialMultiplierService {
    
    @Autowired
    private CooperativeMaterialMultiplierRepository repository;
    
    public CooperativeMaterialMultiplier saveOrUpdateMultiplier(
            Long cooperativeId, Long materialId, Double multiplierValue) {
        
        // tjekker om multiplieren allerde eksistere
        Optional<CooperativeMaterialMultiplier> existing = 
            repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
        
        // hvis den eksistere skal den ændres
        if (existing.isPresent()) {
            CooperativeMaterialMultiplier multiplier = existing.get();
            multiplier.setMultiplierValue(multiplierValue);
            return repository.save(multiplier);

        //hvis den ikke eksistere skal der oprettes en ny multiplier
        } else {
            CooperativeMaterialMultiplier multiplier = new CooperativeMaterialMultiplier();
            multiplier.setCooperativeId(cooperativeId);
            multiplier.setMaterialId(materialId);
            multiplier.setMultiplierValue(multiplierValue);
            return repository.save(multiplier);
        }
    }
    
    // hvis man gerne vil hente en multiplier
    public Optional<CooperativeMaterialMultiplier> getMultiplier(
            Long cooperativeId, Long materialId) {
        return repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
    }
}