package dk.aau.network_management_system.noticeboard;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // GET all active global notices
    @GetMapping
    public ResponseEntity<List<Notice>> getAllActiveNotices() {
        List<Notice> notices = noticeService.getAllActiveNotices();
        return ResponseEntity.ok(notices);
    }

    // GET notices for a specific cooperative (includes global notices)
    @GetMapping("/cooperative/{cooperativeId}")
    public ResponseEntity<List<Notice>> getNoticesForCooperative(@PathVariable Long cooperativeId) {
        List<Notice> notices = noticeService.getNoticesForCooperative(cooperativeId);
        return ResponseEntity.ok(notices);
    }

    // GET single notice by ID
    @GetMapping("/{noticeId}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Long noticeId) {
        return noticeService.getNoticeById(noticeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET notices filtered by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Notice>> getNoticesByPriority(@PathVariable PriorityLevel priority) {
        List<Notice> notices = noticeService.getNoticesByPriority(priority);
        return ResponseEntity.ok(notices);
    }

    // POST - create a new notice (Manager only)
    @PostMapping
    public ResponseEntity<Notice> createNotice(@Valid @RequestBody NoticeDTO dto) {
        Notice created = noticeService.createNotice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT - update a notice (Manager only)
    @PutMapping("/{noticeId}")
    public ResponseEntity<Notice> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeDTO dto) {
        return noticeService.modifyNotice(noticeId, dto.getTitle(), dto.getContent())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - remove a notice (Manager only)
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        boolean deleted = noticeService.deleteNotice(noticeId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
