package dk.aau.network_management_system.multiplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//https://www.geeksforgeeks.org/springboot/spring-boot-jparepository-with-example/
@Repository
//sætter entity typen for interfacet
public interface CooperativeMaterialMultiplierRepository extends JpaRepository<CooperativeMaterialMultiplier, UUID> {
    
    Optional<CooperativeMaterialMultiplier> findByCooperativeIdAndMaterialId(Long cooperativeId, Long materialId);
    List<CooperativeMaterialMultiplier> findByCooperativeId(Long cooperativeId);

}