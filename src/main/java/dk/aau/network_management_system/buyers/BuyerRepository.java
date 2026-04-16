package dk.aau.network_management_system.buyers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<BuyerEntity, Long> {

    @Query(value = """
        SELECT buyer_id, buyer_name
        FROM buyers
        ORDER BY buyer_name ASC
        """, nativeQuery = true)
    List<Object[]> findAllIdAndName();
}
