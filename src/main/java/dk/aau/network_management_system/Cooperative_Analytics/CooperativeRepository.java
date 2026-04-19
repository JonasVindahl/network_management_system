package dk.aau.network_management_system.Cooperative_Analytics;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CooperativeRepository extends JpaRepository<CooperativeEntity, Long> {

    @Query(value = """
        SELECT cooperative_id, cooperative_name
        FROM cooperative
        ORDER BY cooperative_name ASC
        """, nativeQuery = true)
    List<Object[]> findAllIdAndName();
}
