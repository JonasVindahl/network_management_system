package dk.aau.network_management_system.noticeboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // Get all active (non-expired) notices for a specific cooperative OR global notices
    @Query("SELECT n FROM Notice n WHERE (n.cooperativeId = :cooperativeId OR n.cooperativeId IS NULL) AND n.expiresAt > :now")
    List<Notice> findActiveNoticesForCooperative(@Param("cooperativeId") Long cooperativeId, @Param("now") Instant now);

    // Get all active global notices (no specific cooperative)
    @Query("SELECT n FROM Notice n WHERE n.cooperativeId IS NULL AND n.expiresAt > :now")
    List<Notice> findActiveGlobalNotices(@Param("now") Instant now);

    // Get notices by priority
    @Query("SELECT n FROM Notice n WHERE n.priority = :priority AND n.expiresAt > :now")
    List<Notice> findByPriority(@Param("priority") PriorityLevel priority, @Param("now") Instant now);

    // Get all notices by sender
    List<Notice> findByCreatedBy(Long createdBy);
}
