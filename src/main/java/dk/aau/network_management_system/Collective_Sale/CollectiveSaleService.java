package dk.aau.network_management_system.Collective_Sale;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.materials.StockRepository;

@Service
public class CollectiveSaleService {

    private static final Logger log = LoggerFactory.getLogger(CollectiveSaleService.class);

    private final CollectiveSaleRepository repository;
    private final StockRepository stockRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public CollectiveSaleService(CollectiveSaleRepository repository,
                                 StockRepository stockRepository,
                                 AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.stockRepository = stockRepository;
        this.authenticatedUser = authenticatedUser;
    }

    public List<CollectiveSaleInvitationDTO> getPendingInvitations() {
        Long cooperativeId = requireAuthenticatedCooperativeId();

        try {
            return repository.findPendingInvitations(cooperativeId).stream()
                .map(row -> new CollectiveSaleInvitationDTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    (String) row[2],
                    (BigDecimal) row[3],
                    row[4] != null ? ((Timestamp) row[4]).toInstant() : null,
                    row[5] != null ? ((Timestamp) row[5]).toInstant() : null,
                    ((Number) row[6]).longValue()
                ))
                .collect(Collectors.toList());

        } catch (DataAccessException e) {
            log.error("Database error while fetching invitations for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving invitations");
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error while fetching invitations for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing invitations");
        }
    }

    @Transactional
    public Long createCollectiveSale(CreateCollectiveSaleDTO dto) {
        Long cooperativeId = requireAuthenticatedCooperativeId();

        try {
            validateExpectedSaleDate(dto.getExpectedSaleDate());

            CollectiveSaleEntity sale = new CollectiveSaleEntity(
                dto.getMaterialId(),
                dto.getBuyerId(),
                dto.getPricePerKg(),
                dto.getExpectedSaleDate(),
                null,
                cooperativeId
            );

            CollectiveSaleEntity saved = repository.save(sale);
            repository.addContribution(saved.getCollectiveSaleId(), cooperativeId, "ACCEPTED");

            return saved.getCollectiveSaleId();

        } catch (DataAccessException e) {
            log.error("Database error while creating collective sale for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error creating collective sale");
        }
    }

    @Transactional
    public void inviteCooperative(Long saleId, InviteCooperativeDTO dto) {
        Long callerCooperativeId = requireAuthenticatedCooperativeId();

        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            if (!authenticatedUser.isAdmin() && !creatorId.equals(callerCooperativeId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the creator cooperative can invite others");
            }

            if (dto.getCooperativeId().equals(callerCooperativeId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot invite your own cooperative");
            }

            if (repository.findContributionStatus(saleId, dto.getCooperativeId()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cooperative has already been invited or is already participating");
            }

            repository.addContribution(saleId, dto.getCooperativeId(), "INVITED");

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while inviting cooperative {} to sale {}", dto.getCooperativeId(), saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error sending invitation");
        }
    }

    @Transactional
    public void joinCollectiveSale(Long saleId) {
        Long cooperativeId = requireAuthenticatedCooperativeId();

        try {
            repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            String status = repository.findContributionStatus(saleId, cooperativeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You have not been invited to this collective sale"));

            if ("ACCEPTED".equals(status)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already joined this collective sale");
            }
            if ("LEFT".equals(status)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already left this collective sale and cannot rejoin");
            }

            int updated = repository.updateContributionStatus(saleId, cooperativeId, "ACCEPTED");
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Collective sale invitation could not be accepted");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while joining sale {} for cooperative {}", saleId, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error joining collective sale");
        }
    }

    @Transactional
    public void leaveCollectiveSale(Long saleId) {
        Long cooperativeId = requireAuthenticatedCooperativeId();

        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            if (creatorId.equals(cooperativeId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The creator cooperative cannot leave the sale");
            }

            String status = repository.findContributionStatus(saleId, cooperativeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "You are not part of this collective sale"));

            if ("LEFT".equals(status)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already left this collective sale");
            }

            // Return any reserved stock before leaving
            List<Object[]> rows = repository.findSaleMaterialAndContribution(saleId, cooperativeId);
            if (!rows.isEmpty()) {
                Object[] row = rows.get(0);
                BigDecimal reserved = row[1] != null ? toBigDecimal(row[1]) : BigDecimal.ZERO;
                if (reserved.signum() > 0) {
                    Long materialId = ((Number) row[0]).longValue();
                    stockRepository.adjustStock(cooperativeId, materialId, reserved.negate());
                }
            }

            int updated = repository.updateContributionStatus(saleId, cooperativeId, "LEFT");
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Collective sale participation could not be updated");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while leaving sale {} for cooperative {}", saleId, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error leaving collective sale");
        }
    }

    @Transactional
    public void updateContribution(Long saleId, UpdateContributionDTO dto) {
        Long cooperativeId = requireAuthenticatedCooperativeId();

        try {
            List<Object[]> rows = repository.findSaleMaterialAndContribution(saleId, cooperativeId);
            if (rows.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not an accepted participant in this collective sale, or the sale is completed");
            }
            Object[] row = rows.get(0);

            Long materialId = ((Number) row[0]).longValue();
            BigDecimal oldWeight = row[1] != null ? toBigDecimal(row[1]) : BigDecimal.ZERO;
            BigDecimal newWeight = dto.getWeight();
            BigDecimal delta = newWeight.subtract(oldWeight);

            if (delta.signum() != 0) {
                int stockRows = stockRepository.adjustStock(cooperativeId, materialId, delta);
                if (stockRows == 0) {
                    BigDecimal available = stockRepository.findCurrentStock(cooperativeId, materialId)
                        .orElse(BigDecimal.ZERO);
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Not enough stock: you have " + available + " kg available, but need "
                        + delta + " kg more to reach " + newWeight + " kg");
                }
            }

            int updated = repository.updateContributionWeight(saleId, cooperativeId, newWeight);
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Contribution could not be updated");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating contribution for sale {} cooperative {}", saleId, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error updating contribution");
        }
    }

    @Transactional
    public void updateSalePrice(Long saleId, UpdateSalePriceDTO dto) {
        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            if (!authenticatedUser.isAdmin() && !Objects.equals(creatorId, requireAuthenticatedCooperativeId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the creator cooperative can update the sale price");
            }

            int updated = repository.updateSalePrice(saleId, dto.getPricePerKg());
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Collective sale price could not be updated");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating price for sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error updating sale price");
        }
    }

    @Transactional
    public void cancelCollectiveSale(Long saleId) {
        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            if (!authenticatedUser.isAdmin() && !Objects.equals(creatorId, requireAuthenticatedCooperativeId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the creator cooperative can delete the sale");
            }

            // Return all reserved stock to each participant before cancelling
            List<Object[]> reservations = repository.findAcceptedContributionsWithWeight(saleId);
            for (Object[] r : reservations) {
                Long coopId = ((Number) r[0]).longValue();
                BigDecimal weight = toBigDecimal(r[1]);
                Long materialId = ((Number) r[2]).longValue();
                stockRepository.adjustStock(coopId, materialId, weight.negate());
            }

            int updated = repository.cancelSale(saleId);
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Collective sale could not be deleted");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while cancelling sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error deleting sale");
        }
    }

    @Transactional
    public void updateSaleMaterial(Long saleId, UpdateSaleMaterialDTO dto) {
        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collective sale not found or already completed"));

            if (!authenticatedUser.isAdmin() && !Objects.equals(creatorId, requireAuthenticatedCooperativeId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the creator cooperative can update the sale material");
            }

            if (repository.countContributionsWithWeight(saleId) > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot change material while participants have reserved stock. Ask them to reset their contribution to 0 first.");
            }

            int updated = repository.updateSaleMaterial(saleId, dto.getMaterialId());
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Collective sale material could not be updated");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating material for sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error updating sale material");
        }
    }

    public List<ActiveCollectiveSaleDTO> getActiveSales() {
        try {
            if (authenticatedUser.isAdmin()) {
                return repository.findAllActiveSales().stream()
                    .map(row -> new ActiveCollectiveSaleDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (BigDecimal) row[3],
                        row[4] != null ? ((java.sql.Timestamp) row[4]).toInstant() : null,
                        row[5] != null ? ((java.sql.Timestamp) row[5]).toInstant() : null,
                        ((Number) row[6]).longValue(),
                        null
                    ))
                    .collect(Collectors.toList());
            } else {
                Long cooperativeId = requireAuthenticatedCooperativeId();
                return repository.findActiveSalesForCooperative(cooperativeId).stream()
                    .map(row -> new ActiveCollectiveSaleDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (BigDecimal) row[3],
                        row[4] != null ? ((java.sql.Timestamp) row[4]).toInstant() : null,
                        row[5] != null ? ((java.sql.Timestamp) row[5]).toInstant() : null,
                        ((Number) row[6]).longValue(),
                        (String) row[7]
                    ))
                    .collect(Collectors.toList());
            }
        } catch (DataAccessException e) {
            log.error("Database error while fetching active collective sales", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving active collective sales");
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error while fetching active collective sales", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error processing active collective sales");
        }
    }


    private Long requireAuthenticatedCooperativeId() {
        Long cooperativeId = authenticatedUser.getCooperativeId();
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Authenticated user is not associated with a cooperative");
        }
        return cooperativeId;
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal bd) return bd;
        if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(v.toString());
    }

    private void validateExpectedSaleDate(java.time.Instant expectedSaleDate) {
        if (expectedSaleDate != null && !expectedSaleDate.isAfter(java.time.Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Expected sale date must be in the future");
        }
    }

    public List<ActiveCollectiveSaleDTO> getMyCollectiveSales(Long cooperativeId) {
        try {
            return repository.findActiveCollectiveSalesByCooperative(cooperativeId).stream()
                    .map(row -> {
                        ActiveCollectiveSaleDTO dto = new ActiveCollectiveSaleDTO(
                                ((Number) row[0]).longValue(),
                                (String) row[2],
                                (String) row[5],
                                (BigDecimal) row[4],
                                row[6] != null ? ((java.sql.Timestamp) row[6]).toInstant() : null,
                                row[7] != null ? ((java.sql.Timestamp) row[7]).toInstant() : null,
                                null,
                                (String) row[11]
                        );
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error fetching active collective sales for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving collective sales");
        }
    }

    public List<ActiveCollectiveSaleDTO> getMyCollectiveSalesHistory(Long cooperativeId) {
        try {
            return repository.findCollectiveSalesHistoryByCooperative(cooperativeId).stream()
                    .map(row -> {
                        ActiveCollectiveSaleDTO dto = new ActiveCollectiveSaleDTO(
                                ((Number) row[0]).longValue(),
                                (String) row[2],
                                (String) row[5],
                                (BigDecimal) row[4],
                                row[6] != null ? ((java.sql.Timestamp) row[6]).toInstant() : null,
                                row[7] != null ? ((java.sql.Timestamp) row[7]).toInstant() : null,
                                null,
                                (String) row[11]
                        );
                        dto.setSoldAt(row[8] != null ? ((java.sql.Timestamp) row[8]).toInstant() : null);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error fetching collective sales history for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving collective sales history");
        }
    }

    @Transactional
    public void confirmCollectiveSale(Long saleId) {
        try {
            Long creatorId = repository.findActiveSaleCreator(saleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Collective sale not found or already cancelled"));

            if (!authenticatedUser.isAdmin() && !Objects.equals(creatorId, requireAuthenticatedCooperativeId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only the creator cooperative can confirm the sale");
            }

            BigDecimal totalWeight = BigDecimal.ZERO;

            List<Object[]> reservations = repository.findAcceptedContributionsWithWeight(saleId);
            for (Object[] r : reservations) {
                Long coopId = ((Number) r[0]).longValue();
                BigDecimal weight = toBigDecimal(r[1]);
                Long materialId = ((Number) r[2]).longValue();
                BigDecimal pricePerKg = toBigDecimal(r[3]);
                stockRepository.recordSale(coopId, materialId, weight.doubleValue());
                totalWeight = totalWeight.add(weight);
                BigDecimal revenueShare = weight.multiply(pricePerKg);
                repository.updateRevenueShare(saleId, coopId, revenueShare);

            }

            int rows = repository.confirmSale(saleId, totalWeight);
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Collective sale could not be completed");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while cancelling sale {}", saleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting sale");
        }
    }
}
