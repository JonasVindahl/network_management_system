package dk.aau.network_management_system.noticeboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // GET - get all active notices (non-expired)
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll().stream()
                .filter(n -> !n.isExpired())
                .toList();
    }

    // GET - get all active notices for a specific cooperative
    public List<Notice> getNoticesForCooperative(Long cooperativeId) {
        return noticeRepository.findActiveNoticesForCooperative(cooperativeId, Instant.now());
    }

    // GET - get all active global notices
    public List<Notice> getAllActiveNotices() {
        return noticeRepository.findActiveGlobalNotices(Instant.now());
    }

    // GET - get single notice by ID
    public Optional<Notice> getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId);
    }

    // GET - get notices filtered by priority
    public List<Notice> getNoticesByPriority(PriorityLevel priority) {
        return noticeRepository.findByPriority(priority, Instant.now());
    }

    // POST - create a new notice
    public Notice createNotice(String title, String content, PriorityLevel priority, Long createdBy, Instant expiresAt, Long cooperativeId) {
        Notice notice = new Notice(title, content, priority, createdBy, expiresAt, cooperativeId);
        return noticeRepository.save(notice);
    }

    public Notice createNotice(NoticeDTO dto) {
        return createNotice(dto.getTitle(), dto.getContent(), dto.getPriority(), dto.getCreatedBy(), dto.getExpiresAt(), dto.getCooperativeId());
    }

    // PUT - modify an existing notice
    public Optional<Notice> modifyNotice(Long noticeId, String title, String content) {
        return noticeRepository.findById(noticeId).map(notice -> {
            if (title != null && !title.isBlank()) notice.setTitle(title);
            if (content != null && !content.isBlank()) notice.setContent(content);
            notice.setLastUpdated(Instant.now());
            return noticeRepository.save(notice);
        });
    }

    // DELETE - remove a notice
    public boolean deleteNotice(Long noticeId) {
        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            return true;
        }
        return false;
    }
}
