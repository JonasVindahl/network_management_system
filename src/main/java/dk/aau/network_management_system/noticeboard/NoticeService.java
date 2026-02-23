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

    // GET - get notices for a cooperative (includes global notices)
    public List<Notice> getNotice(String title, String message, long dateAdded, String senderId, long timeAlive) {
        return noticeRepository.findAll().stream()
                .filter(n -> !n.isExpired())
                .toList();
    }

    // GET - get all active notices for a specific cooperative
    public List<Notice> getNoticesForCooperative(String cooperativeId) {
        long now = Instant.now().toEpochMilli();
        return noticeRepository.findActiveNoticesForCooperative(cooperativeId, now);
    }

    // GET - get all active global notices
    public List<Notice> getAllActiveNotices() {
        long now = Instant.now().toEpochMilli();
        return noticeRepository.findActiveGlobalNotices(now);
    }

    // GET - get single notice by ID
    public Optional<Notice> getNoticeById(String noticeId) {
        return noticeRepository.findById(noticeId);
    }

    // GET - get notices filtered by priority
    public List<Notice> getNoticesByPriority(PriorityLevel priority) {
        long now = Instant.now().toEpochMilli();
        return noticeRepository.findByPriority(priority, now);
    }

    // POST - create a new notice
    public Notice createNotice(String title, String message, PriorityLevel priority, String senderId, long timeAlive, String cooperativeId) {
        Notice notice = new Notice(title, message, priority, senderId, timeAlive, cooperativeId);
        return noticeRepository.save(notice);
    }

    public Notice createNotice(NoticeDTO dto) {
        return createNotice(dto.getTitle(), dto.getMessage(), dto.getPriority(), dto.getSenderId(), dto.getTimeAlive(), dto.getCooperativeId());
    }

    // PUT - modify an existing notice
    public Optional<Notice> modifyNotice(String noticeId, String title, String message) {
        return noticeRepository.findById(noticeId).map(notice -> {
            if (title != null && !title.isBlank()) notice.setTitle(title);
            if (message != null && !message.isBlank()) notice.setMessage(message);
            notice.setModifiedDate(Instant.now().toEpochMilli());
            return noticeRepository.save(notice);
        });
    }

    // DELETE - remove a notice
    public boolean deleteNotice(String noticeId) {
        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            return true;
        }
        return false;
    }
}
