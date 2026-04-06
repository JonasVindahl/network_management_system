package dk.aau.network_management_system.multiplier;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class CooperativeMaterialMultiplierService {

    private final CooperativeMaterialMultiplierRepository repository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public CooperativeMaterialMultiplierService(
            CooperativeMaterialMultiplierRepository repository,
            AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

    public List<CooperativeMaterialMultiplier> getAllMultipliers(Long cooperativeId) {
        validateCooperativeOwnership(cooperativeId);
        return repository.findByCooperativeId(cooperativeId);
    }

    public Optional<CooperativeMaterialMultiplier> getMultiplier(
            Long cooperativeId, Long materialId) {

        validateCooperativeOwnership(cooperativeId);

        return repository.findByCooperativeIdAndMaterialId(cooperativeId, materialId);
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