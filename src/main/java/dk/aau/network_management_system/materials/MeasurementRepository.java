package dk.aau.network_management_system.materials;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO public.measurements
            (weight_kg, time_stamp, wastepicker, material, device, bag_filled)
        VALUES
            (:weightKg, :timeStamp, :workerId, :materialId, :deviceId, :bagFilled)
        """, nativeQuery = true)
    void insertMeasurement(
        @Param("weightKg")   double weightKg,
        @Param("timeStamp")  LocalDateTime timeStamp,
        @Param("workerId")   long workerId,
        @Param("materialId") long materialId,
        @Param("deviceId")   long deviceId,
        @Param("bagFilled")  boolean bagFilled
    );
}