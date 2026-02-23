package dk.aau.network_management_system.noticeboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, String> {

    // Get all active (non-expired) notices for a specific cooperative OR global notices
    @Query("SELECT n FROM Notice n WHERE (n.cooperativeId = :cooperativeId OR n.cooperativeId IS NULL) AND (n.dateAdded + n.timeAlive) > :now")
    List<Notice> findActiveNoticesForCooperative(@Param("cooperativeId") String cooperativeId, @Param("now") long now);

    // Get all active global notices (no specific cooperative)
    @Query("SELECT n FROM Notice n WHERE n.cooperativeId IS NULL AND (n.dateAdded + n.timeAlive) > :now")
    List<Notice> findActiveGlobalNotices(@Param("now") long now);

    // Get notices by priority
    @Query("SELECT n FROM Notice n WHERE n.priority = :priority AND (n.dateAdded + n.timeAlive) > :now")
    List<Notice> findByPriority(@Param("priority") PriorityLevel priority, @Param("now") long now);

    // Get all notices by sender
    List<Notice> findBySenderId(String senderId);
}
