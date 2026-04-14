    package dk.aau.network_management_system.Sales;

    import java.sql.Timestamp;
    import java.time.Instant;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    import java.util.stream.Collectors;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.dao.DataAccessException;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;
    import org.springframework.transaction.annotation.Transactional;
    import dk.aau.network_management_system.materials.StockRepository;

    import dk.aau.network_management_system.auth.AuthenticatedUser;

    @Service
    public class SalesService {

        private static final Logger log = LoggerFactory.getLogger(SalesService.class);
        private final SalesRepository repository;
        private final AuthenticatedUser authenticatedUser;
        private final StockRepository stockRepository;

        @Autowired
        public SalesService(SalesRepository repository, AuthenticatedUser authenticatedUser,
                            StockRepository stockRepository) {
            this.repository = repository;
            this.authenticatedUser = authenticatedUser;
            this.stockRepository = stockRepository;
        }

        public List<SaleDTO> getSalesHistory(Long cooperativeId, Instant startDate,
                                             Instant endDate, String type) {

            validateCooperativeOwnership(cooperativeId);

        try{
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
            } catch (DataAccessException e){
                log.error("Database error while fetching notices for cooperative {}", cooperativeId, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving notices");
            }catch (ClassCastException | NullPointerException e) {
                log.error("Data mapping error in worker productivity for cooperative {}",
                         cooperativeId, e);
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error processing productivity data"
                );
            }
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

        @Transactional
        public void createSale(Long cooperativeId, Long workerId, CreateSaleDTO dto) {
            try {
                repository.insertSale(
                        dto.getMaterialId(),
                        dto.getWeight(),
                        dto.getPriceKg(),
                        dto.getBuyerId(),
                        workerId,
                        cooperativeId,
                        dto.getExpectedSaleDate()
                );
            } catch (DataAccessException e) {
                log.error("Database error while creating sale for cooperative {}", cooperativeId, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error creating sale");
            }
        }

        @Transactional
        public void updateSale(Long saleId, Long cooperativeId, UpdateSaleDTO dto) {
            try {
                int rows = repository.updateSale(
                        saleId,
                        cooperativeId,
                        dto.getMaterialId(),
                        dto.getWeight(),
                        dto.getPriceKg(),
                        dto.getBuyerId(),
                        dto.getExpectedSaleDate()
                );
                if (rows == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Sale not found, already completed, cancelled, or does not belong to your cooperative");
                }
            } catch (DataAccessException e) {
                log.error("Database error while updating sale {} for cooperative {}", saleId, cooperativeId, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error updating sale");
            }
        }

        @Transactional
        public void completeSale(Long saleId, Long cooperativeId) {
            List<Object[]> row = repository.findMaterialAndWeightBySaleId(saleId, cooperativeId);
            if (row == null || row.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sale not found or does not belong to your cooperative");
            }

            long materialId = ((Number) row.get(0)[0]).longValue();
            double weight   = ((Number) row.get(0)[1]).doubleValue();

            try {
                int rows = repository.completeSale(saleId, cooperativeId);
                if (rows == 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Sale is already completed or cancelled");
                }

                int stockRows = stockRepository.recordSale(cooperativeId, materialId, weight);
                if (stockRows == 0) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "No stock row found for this cooperative and material");
                }
            } catch (DataAccessException e) {
                log.error("Database error while completing sale {} for cooperative {}", saleId, cooperativeId, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error completing sale");
            }
        }

        @Transactional
        public void cancelSale(Long saleId, Long cooperativeId) {
            try {
                int rows = repository.cancelSale(saleId, cooperativeId);
                if (rows == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Sale not found, already completed, cancelled, or does not belong to your cooperative");
                }
            } catch (DataAccessException e) {
                log.error("Database error while cancelling sale {} for cooperative {}", saleId, cooperativeId, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error cancelling sale");
            }
        }
    }