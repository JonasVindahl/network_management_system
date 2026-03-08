package dk.aau.network_management_system.materials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialBagStateRepository extends JpaRepository<MaterialBagState, Long> {

    @Query(value = """
        SELECT current_kg FROM public.material_bag_state
        WHERE cooperative_id = :cooperativeId AND material_id = :materialId
        """, nativeQuery = true)
    Double getCurrentKg(
        @Param("cooperativeId") long cooperativeId,
        @Param("materialId")    long materialId
    );

    // Bag is full → reset (upsert: create row if not exists, else reset)
    @Modifying
    @Query(value = """
        INSERT INTO public.material_bag_state
            (cooperative_id, material_id, is_begun, current_kg, last_updated)
        VALUES
            (:cooperativeId, :materialId, :isBegun, :currentKg, now())
        ON CONFLICT (cooperative_id, material_id)
        DO UPDATE SET
            is_begun     = :isBegun,
            current_kg   = :currentKg,
            last_updated = now()
        """, nativeQuery = true)
    void upsertBagState(
        @Param("cooperativeId") long cooperativeId,
        @Param("materialId")    long materialId,
        @Param("isBegun")       boolean isBegun,
        @Param("currentKg")     double currentKg
    );

    // Bag not full → add kg, mark begun
    @Modifying
    @Query(value = """
        INSERT INTO public.material_bag_state
            (cooperative_id, material_id, is_begun, current_kg, last_updated)
        VALUES
            (:cooperativeId, :materialId, true, :amount, now())
        ON CONFLICT (cooperative_id, material_id)
        DO UPDATE SET
            is_begun     = true,
            current_kg   = material_bag_state.current_kg + :amount,
            last_updated = now()
        """, nativeQuery = true)
    void upsertAndAddKg(
        @Param("cooperativeId") long cooperativeId,
        @Param("materialId")    long materialId,
        @Param("amount")        double amount
    );
}