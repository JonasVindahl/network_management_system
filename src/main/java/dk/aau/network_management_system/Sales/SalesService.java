package dk.aau.network_management_system.Sales;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class SalesService {
    
    private final SalesRepository repository;
    private final AuthenticatedUser authenticatedUser;
    
    @Autowired
    public SalesService(SalesRepository repository, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

    public List<SaleDTO> getSalesHistory(Long cooperativeId, Instant startDate, 
                                         Instant endDate, String type) {
        
        validateCooperativeOwnership(cooperativeId);
        
        List<SaleDTO> allSales = new ArrayList<>();
        
        if ("REGULAR".equalsIgnoreCase(type) || "ALL".equalsIgnoreCase(type)) {
            allSales.addAll(mapRegularSales(
                repository.findRegularSalesHistory(cooperativeId, startDate, endDate)
            ));
        }
        
        if ("COLLECTIVE".equalsIgnoreCase(type) || "ALL".equalsIgnoreCase(type)) {
            allSales.addAll(mapCollectiveSales(
                repository.findCollectiveSalesHistory(cooperativeId, startDate, endDate)
            ));
        }
        
        // sorte for sold_at nyeste føtst
        return allSales.stream()
            .sorted((a, b) -> {
                if (a.getSoldAt() == null && b.getSoldAt() == null) return 0;
                if (a.getSoldAt() == null) return 1;
                if (b.getSoldAt() == null) return -1;
                return b.getSoldAt().compareTo(a.getSoldAt());
            })
            .collect(Collectors.toList());
    }
 
    public List<SaleDTO> getActiveSales(Long cooperativeId, String type) {
        
        validateCooperativeOwnership(cooperativeId);
        
        List<SaleDTO> activeSales = new ArrayList<>();
        
        if ("REGULAR".equalsIgnoreCase(type) || "ALL".equalsIgnoreCase(type)) {
            activeSales.addAll(mapRegularSales(
                repository.findActiveRegularSales(cooperativeId)
            ));
        }
        
        if ("COLLECTIVE".equalsIgnoreCase(type) || "ALL".equalsIgnoreCase(type)) {
            activeSales.addAll(mapCollectiveSales(
                repository.findActiveCollectiveSales(cooperativeId)
            ));
        }
        
        // Sortere efter created_at
        return activeSales.stream()
            .sorted((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            })
            .collect(Collectors.toList());
    }
    
  
    private List<SaleDTO> mapRegularSales(List<Object[]> raw) {
        return raw.stream()
            .map(row -> new SaleDTO(
                ((Number) row[0]).longValue(),                  
                (String) row[1],                                 
                row[2] != null ? ((Timestamp) row[2]).toInstant() : null,  
                row[3] != null ? ((Timestamp) row[3]).toInstant() : null,
                row[4] != null ? ((Timestamp) row[4]).toInstant() : null, 
                (String) row[5],                               
                ((Number) row[6]).doubleValue(),    
                ((Number) row[7]).doubleValue(),              
                (String) row[8] 
            ))
            .collect(Collectors.toList());
    }
    
    private List<SaleDTO> mapCollectiveSales(List<Object[]> raw) {
        return raw.stream()
            .map(row -> new SaleDTO(
                ((Number) row[0]).longValue(),                  
                (String) row[1],                                
                row[2] != null ? ((Timestamp) row[2]).toInstant() : null,  
                row[3] != null ? ((Timestamp) row[3]).toInstant() : null,
                row[4] != null ? ((Timestamp) row[4]).toInstant() : null,
                (String) row[5],            
                ((Number) row[6]).doubleValue(),
                ((Number) row[7]).doubleValue(),      
                (String) row[8],                             
                ((Number) row[9]).intValue()                     
            ))
            .collect(Collectors.toList());
    }
    
  
    private void validateCooperativeOwnership(Long cooperativeId) {
        // admin har adgang til alt
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        // null check
        if (cooperativeId == null || userCooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid cooperative ID");
        }
        
        // kun egenet coop
        if (!Objects.equals(cooperativeId, userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }
    }
}