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
        
        Optional<CooperativeMaterialMultiplier> existing = 
            repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
        
        if (existing.isPresent()) {
            CooperativeMaterialMultiplier multiplier = existing.get();
            multiplier.setMultiplierValue(multiplierValue);
            return repository.save(multiplier);

        } else {
            CooperativeMaterialMultiplier multiplier = new CooperativeMaterialMultiplier();
            multiplier.setCooperativeId(cooperativeId);
            multiplier.setMaterialId(materialId);
            multiplier.setMultiplierValue(multiplierValue);
            return repository.save(multiplier);
        }
    }
    
    public Optional<CooperativeMaterialMultiplier> getMultiplier(
            Long cooperativeId, Long materialId) {
        return repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
    }
}