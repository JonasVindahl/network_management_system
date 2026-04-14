package dk.aau.network_management_system.Collective_Sale_Reports;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class SaleReportsService {
    
    private static final Logger log = LoggerFactory.getLogger(SaleReportsService.class);

    private final SaleReportsRepository saleReportsRepository;
    private final AuthenticatedUser authenticatedUser;

    public SaleReportsService(SaleReportsRepository saleReportsRepository,
                             AuthenticatedUser authenticatedUser) {
        this.saleReportsRepository = saleReportsRepository;
        this.authenticatedUser = authenticatedUser;
    }

    public SaleReportDTO getSaleReport(Long saleId, Long requestingCooperativeId) {
        
        try {
            if (!saleReportsRepository.saleExists(saleId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Sale with id " + saleId + " not found");
            }
            
            validateSaleAccess(saleId, requestingCooperativeId);
            
            List<Object[]> rows = saleReportsRepository.findSaleReport(saleId);
            
            if (rows.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Sale data could not be retrieved");
            }

            return new SaleReportDTO(rows.get(0));
            
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
    

    private void validateSaleAccess(Long saleId, Long cooperativeId) {
        
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot access sale reports");
        }
        
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cooperative ID is required");
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        if (!cooperativeId.equals(userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's sale reports");
        }
        
        try {
            boolean isOwner = saleReportsRepository.isSaleOwnedByCooperative(saleId, cooperativeId);
            
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