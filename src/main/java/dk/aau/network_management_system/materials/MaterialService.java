package dk.aau.network_management_system.materials;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaterialService {

    private final MeasurementRepository measurementRepository;
    private final MaterialBagStateRepository bagStateRepository;
    private final StockRepository stockRepository;

    @Autowired
    public MaterialService(MeasurementRepository measurementRepository,
                           MaterialBagStateRepository bagStateRepository,
                           StockRepository stockRepository) {
        this.measurementRepository = measurementRepository;
        this.bagStateRepository = bagStateRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void insertMaterial(long cooperativeId, MaterialRequest request) {

        Double accumulated = bagStateRepository.getCurrentKg(cooperativeId, request.getMaterialId());
        double delta = Math.max(0.0, request.getAmount() - (accumulated != null ? accumulated : 0.0));

        measurementRepository.insertMeasurement(
            delta,
            LocalDateTime.now(),
            request.getWorkerId(),
            request.getMaterialId(),
            request.getDeviceId(),
            request.isBagFull()
        );

        if (request.isBagFull()) {
            bagStateRepository.upsertBagState(
                cooperativeId,
                request.getMaterialId(),
                false,
                0.0
            );
        } else {
            bagStateRepository.upsertBagState(
                cooperativeId,
                request.getMaterialId(),
                true,
                request.getAmount()
            );
        }

        int rows = stockRepository.addToStock(
            cooperativeId,
            request.getMaterialId(),
            delta
        );
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "No stock row found for this cooperative and material");
        }
    }
}