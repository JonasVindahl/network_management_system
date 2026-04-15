package dk.aau.network_management_system.Sale_Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class SaleReportsService {
    
    private static final Logger log = LoggerFactory.getLogger(SaleReportsService.class);

    private final SaleReportsRepository repository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public SaleReportsService(SaleReportsRepository repository,
                             AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

    public SaleReportDTO getSaleReport(Long saleId, Long requestingCooperativeId) {
        
        try {
            if (!repository.saleExists(saleId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Sale not found");
            }
            
            validateSaleAccess(saleId, requestingCooperativeId);
            
            List<Object[]> rawData = repository.findSaleReport(saleId);
            
            if (rawData.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Sale data not found");
            }

            return mapToSaleReportDTO(rawData.get(0));
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while fetching sale report for sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving sale report");
        } catch (Exception e) {
            log.error("Unexpected error while generating sale report for sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing sale report");
        }
    }
    
   
    private SaleReportDTO mapToSaleReportDTO(Object[] row) {
        
        Long saleId = ((Number) row[0]).longValue();
        Instant createdAt = row[1] != null ? ((Timestamp) row[1]).toInstant() : null;
        Instant soldAt = row[2] != null ? ((Timestamp) row[2]).toInstant() : null;
        Instant cancelledAt = row[3] != null ? ((Timestamp) row[3]).toInstant() : null;
        Instant expectedSaleDate = row[4] != null ? ((Timestamp) row[4]).toInstant() : null;
        Double weight = ((Number) row[5]).doubleValue();
        Double pricePerKg = ((Number) row[6]).doubleValue();
        // row[7] er i DTO
        Long materialId = ((Number) row[8]).longValue();
        String materialName = (String) row[9];
        Long buyerId = ((Number) row[10]).longValue();
        String buyerName = (String) row[11];
        Long responsibleWorkerId = ((Number) row[12]).longValue();
        String responsibleWorkerName = (String) row[13];
        Long cooperativeId = ((Number) row[14]).longValue();
        String cooperativeName = (String) row[15];
        
        log.info("Mapped sale report - saleId: {}, soldAt: {}, cancelledAt: {}", 
                saleId, soldAt, cancelledAt);
        
        return new SaleReportDTO(
            saleId,
            materialId,
            materialName,
            buyerId,
            buyerName,
            responsibleWorkerId,
            responsibleWorkerName,
            cooperativeId,
            cooperativeName,
            createdAt,
            soldAt,
            cancelledAt,
            expectedSaleDate,
            weight,
            pricePerKg
        );
    }
    

    private void validateSaleAccess(Long saleId, Long cooperativeId) {
        
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot access sale reports");
        }
        
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cooperative ID is required");
        }
        
        if (!Objects.equals(cooperativeId, userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's sale reports");
        }
        
        try {
            boolean isOwner = repository.isSaleOwnedByCooperative(saleId, cooperativeId);
            
            if (!isOwner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This sale does not belong to your cooperative");
            }
        } catch (DataAccessException e) {
            log.error("Database error while checking sale ownership for sale {} and cooperative {}", 
                     saleId, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error verifying access permissions");
        }
    }
}