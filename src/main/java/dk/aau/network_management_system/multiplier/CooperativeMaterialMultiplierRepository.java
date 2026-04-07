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
        SELECT cmm.material_id, mat.material_name, cmm.multiplier_value, cmm.cooperative_id
        FROM cooperative_material_multiplier cmm
        JOIN materials mat ON cmm.material_id = mat.material_id
        WHERE cmm.cooperative_id = :cooperativeId
        ORDER BY mat.material_name
        """, nativeQuery = true)
    List<Object[]> findMultipliersWithMaterialName(
        @Param("cooperativeId") Long cooperativeId
    );


    Optional<CooperativeMaterialMultiplier> findByCooperativeIdAndMaterialId(Long cooperativeId, Long materialId);
    List<CooperativeMaterialMultiplier> findByCooperativeId(Long cooperativeId);

}