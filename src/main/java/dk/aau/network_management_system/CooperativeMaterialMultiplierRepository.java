package dk.aau.network_management_system;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// dette kode gør det nemmere at integragere med systemet grundet man ikke behøver at skrive sql
//https://www.geeksforgeeks.org/springboot/spring-boot-jparepository-with-example/
@Repository
//sætter entity typen for interfacet
public interface CooperativeMaterialMultiplierRepository extends JpaRepository<CooperativeMaterialMultiplier, UUID> {
    
    // container der indeholder værdi eller er tom
    Optional<CooperativeMaterialMultiplier> findByCooperativeIdAndMaterialId(Long cooperativeId, Long materialId);
    // list der indholder coop id 
    List<CooperativeMaterialMultiplier> findByCooperativeId(Long cooperativeId);
}