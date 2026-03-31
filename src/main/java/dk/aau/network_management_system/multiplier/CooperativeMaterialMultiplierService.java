package dk.aau.network_management_system.multiplier;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class CooperativeMaterialMultiplierService {
    
    private static final Logger log = LoggerFactory.getLogger(CooperativeMaterialMultiplierService.class);

    private final CooperativeMaterialMultiplierRepository repository;
    private final AuthenticatedUser authenticatedUser;
    
    @Autowired
    public CooperativeMaterialMultiplierService(
            CooperativeMaterialMultiplierRepository repository,
            AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }
    
    public Optional<CooperativeMaterialMultiplier> getMultiplier(
            Long cooperativeId, Long materialId) {
        
        validateCooperativeOwnership(cooperativeId);
        
        try {
            return repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
        } catch (DataAccessException e) {
            log.error("Database error while fetching multiplier for cooperative {} and material {}",
                    cooperativeId, materialId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving multiplier");
        }
    }
    
    @Transactional
    public CooperativeMaterialMultiplier saveOrUpdateMultiplier(
            Long cooperativeId, Long materialId, Double multiplierValue) {
        
        validateCooperativeOwnership(cooperativeId);
        
        if (materialId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Material ID is required");
        }
        if (multiplierValue == null || multiplierValue <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Multiplier value must be positive");
        }
        
        try {
            Optional<CooperativeMaterialMultiplier> existing = 
                repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
            
            CooperativeMaterialMultiplier entity;
            
            if (existing.isPresent()) {
                entity = existing.get();
                entity.setMultiplierValue(multiplierValue);
                entity.setLastUpdated(Instant.now());
            } else {
                entity = new CooperativeMaterialMultiplier(
                    cooperativeId,
                    materialId,
                    multiplierValue
                );
            }
            
            return repository.save(entity);
            
        } catch (DataAccessException e) {
            log.error("Database error while saving multiplier for cooperative {} and material {}",
                    cooperativeId, materialId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error saving multiplier");
        }
    }
    
    private void validateCooperativeOwnership(Long cooperativeId) {
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        if (cooperativeId == null || userCooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid cooperative ID");
        }
        
        if (!Objects.equals(cooperativeId, userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }
    }
}