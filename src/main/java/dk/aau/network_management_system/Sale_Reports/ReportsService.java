package dk.aau.network_management_system.Sale_Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
public class ReportsService {

    private static final Logger log = LoggerFactory.getLogger(ReportsService.class);
    
    private final ReportsRepository repository;
    private final AuthenticatedUser authenticatedUser;
    
    @Autowired
    public ReportsService(ReportsRepository repository, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }


    public CollectiveSaleReportDTO getCollectiveSaleReport(Long saleId, Long cooperativeId) {
        try {
            if(!repository.collectiveSaleExists((saleId))){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collective sale not found");
            }

            validateReportAccess(saleId, cooperativeId);

            List<Object[]> rawData = repository.findCollectiveSaleReport(saleId);

            if (rawData.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Contributions for this sale");
            }

        return mapToReportDTO(rawData);

            
        } catch (ResponseStatusException e) {
            throw e;

        } catch (DataAccessException e) {
            log.error("Database error while fetching report for collective sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error retrieving collective sale report");

        } catch (Exception e) {
            log.error("Unexpected error while generating report for collective sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error processing collective sale report");
        }
    

    }

private CollectiveSaleReportDTO mapToReportDTO(List<Object[]> rawData) {
        
        if (rawData.isEmpty()) {
            throw new IllegalArgumentException("Cannot map - There is missing data");
        }
        
        // First row contains sale info (same for all rows)
        Object[] firstRow = rawData.get(0);
        
        Long collectiveSaleId = ((Number) firstRow[0]).longValue();
        Long materialId = ((Number) firstRow[1]).longValue();
        String materialName = (String) firstRow[2];
        Long buyerId = ((Number) firstRow[3]).longValue();
        String buyerName = (String) firstRow[4];
        Instant createdAt = firstRow[5] != null ? ((Timestamp) firstRow[5]).toInstant() : null;
        Instant soldAt = firstRow[6] != null ? ((Timestamp) firstRow[6]).toInstant() : null;
        Instant expectedSaleDate = firstRow[7] != null ? ((Timestamp) firstRow[7]).toInstant() : null;
        Double totalWeight = firstRow[8] != null ? ((Number) firstRow[8]).doubleValue() : 0.0;
        Double pricePerKg = firstRow[9] != null ? ((Number) firstRow[9]).doubleValue() : 0.0;
        Long creatorCooperativeId = firstRow[14] != null ? ((Number) firstRow[14]).longValue() : null;

        List<ContributionDetailDTO> contributions = new ArrayList<>();

        for (Object[] row : rawData) {
            Long coopId = ((Number) row[10]).longValue();
            String coopName = (String) row[11];
            Double contributedWeight = row[12] != null ? ((Number) row[12]).doubleValue() : 0.0;
            Double revenueShare = row[13] != null ? ((Number) row[13]).doubleValue() : null;

            if (revenueShare == null) {
                revenueShare = contributedWeight * pricePerKg;
            }
            
            ContributionDetailDTO contribution = new ContributionDetailDTO(
                coopId, 
                coopName, 
                contributedWeight, 
                totalWeight,
                revenueShare
            );
            
            contributions.add(contribution);
        }
        
        return new CollectiveSaleReportDTO(
            collectiveSaleId,
            materialId,
            materialName,
            buyerId,
            buyerName,
            createdAt,
            soldAt,
            expectedSaleDate,
            totalWeight,
            pricePerKg,
            creatorCooperativeId,
            contributions
        );
 
}


    private void validateReportAccess(Long saleId, Long cooperativeId){

        if (authenticatedUser.isWorker()){
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Workers cannot acesss collective sale reports");
        }

        if (authenticatedUser.isAdmin()){
            return;
        }

        Long userCooperativeId = authenticatedUser.getCooperativeId();

        if (cooperativeId == null){
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,"Missing cooperative ID");
        }
        
        if (!Objects.equals(cooperativeId, userCooperativeId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,"You can only access your own cooperative report");
        }
        
        try {
            boolean participated = repository.isCooperativeParticipant(saleId, cooperativeId);
            
            if (!participated) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Your cooperative did not participate in this sale");
            }
        } catch (DataAccessException e) {
            log.error("Database error while checking for sale and cooperative", 
                     saleId, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error verifying access permissions");
        }
    

    }


}
