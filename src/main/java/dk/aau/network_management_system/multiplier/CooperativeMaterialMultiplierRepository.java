package dk.aau.network_management_system.multiplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


//https://www.geeksforgeeks.org/springboot/spring-boot-jparepository-with-example/
@Repository
public interface CooperativeMaterialMultiplierRepository extends JpaRepository<CooperativeMaterialMultiplier, UUID> {
    
   @Query(value = """
    SELECT 
        mat.material_id,
        mat.material_name,
        COALESCE(cmm.multiplier_value, 1.0) as multiplier_value,
        :cooperativeId as cooperative_id
    FROM materials mat
    LEFT JOIN cooperative_material_multiplier cmm 
        ON mat.material_id = cmm.material_id 
        AND cmm.cooperative_id = :cooperativeId
    ORDER BY mat.material_name
    """, nativeQuery = true)
List<Object[]> findMultipliersWithMaterialName(
    @Param("cooperativeId") Long cooperativeId
);


    Optional<CooperativeMaterialMultiplier> findByCooperativeIdAndMaterialId(Long cooperativeId, Long materialId);
    List<CooperativeMaterialMultiplier> findByCooperativeId(Long cooperativeId);

}