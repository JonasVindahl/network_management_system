package dk.aau.network_management_system.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PermissionHelper {
    
    private final AuthenticatedUser authenticatedUser;
    
    public PermissionHelper(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
    
    public void requireManagerOrAdmin() {
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot perform this action");
        }
    }
    
    public void requireAdmin() {
        if (!authenticatedUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Only admins can perform this action");
        }
    }
    
    public Long determineTargetCooperative(Long requestedCooperativeId) {
        if (authenticatedUser.isWorker() || authenticatedUser.isManager()) {
            return authenticatedUser.getCooperativeId();
        }
        
        if (requestedCooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Admin must specify cooperativeId parameter");
        }
        
        return requestedCooperativeId;
    }
    
    public Long determineTargetCooperativeForWrite(Long dtoCooperativeId) {
        if (authenticatedUser.isManager()) {
            return authenticatedUser.getCooperativeId();
        }
        return dtoCooperativeId;
    }
    
    public void validateCooperativeOwnership(Long cooperativeId) {
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        if (cooperativeId == null || userCooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid cooperative ID");
        }
        
        if (!cooperativeId.equals(userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }
    }
    
    public Long determineTargetWorker(Long requestedWorkerId) {
        if (authenticatedUser.isWorker()) {
            return authenticatedUser.getWorkerId();
        }
        return requestedWorkerId;
    }
}