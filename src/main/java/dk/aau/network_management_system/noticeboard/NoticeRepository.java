package dk.aau.network_management_system.noticeboard;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // Get all active (non-expired) notices for a specific cooperative OR global notices
    @Query("SELECT n FROM Notice n WHERE (n.cooperativeId = :cooperativeId OR n.cooperativeId IS NULL) AND n.expiresAt > :now")
    List<Notice> findActiveNoticesForCooperative(@Param("cooperativeId") Long cooperativeId, @Param("now") Instant now);

    // Get all active global notices (no specific cooperative)
    @Query("SELECT n FROM Notice n WHERE n.cooperativeId IS NULL AND n.expiresAt > :now")
    List<Notice> findActiveGlobalNotices(@Param("now") Instant now);

    // Get notices by priority
    // Get notices by priority
    // @Query(value = "SELECT * FROM notice_board WHERE priority = :priority AND expires_at > NOW()", nativeQuery = true)
    // List<Notice> findByPriority(@Param("priority") int priority);

    @Query("SELECT n FROM Notice n WHERE n.priority = :priority AND (n.cooperativeId = :cooperativeId OR n.cooperativeId IS NULL) AND n.expiresAt > :now")
    List<Notice> findByPriorityAndCooperativeId(@Param("priority") int priority, @Param("cooperativeId") Long cooperativeId, @Param("now") Instant now);
    // Get all notices by creator
    // List<Notice> findByCreatedBy(Long createdBy);
}


// Update: Deleted the old findByPriority() that didn't have the cooperativeId - which means that all cooperatives could see notices filtered by priority.
// Therefore we updated it to findByPriorityAndCooperativeId() which returns notices from users own cooperatives + globabl notices.
// findbyCreatedBy() is deleted as it is not needed, as it could be a security risk.
