package dk.aau.network_management_system.noticeboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // GET all active global notices
    @GetMapping("/all")
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
    @GetMapping("/filter/{priority}")
    public ResponseEntity<List<Notice>> getNoticesByPriority(@PathVariable int priority) {
        List<Notice> notices = noticeService.getNoticesByPriority(priority);
        return ResponseEntity.ok(notices);
    } 
/* 
    // GET notices filtered by priority (e.g. /api/notices/filter?priority=High)
   @GetMapping("/filter")
public ResponseEntity<List<Notice>> getNoticesByPriority(@RequestParam int priority) {
    PriorityLevel priorityLevel = PriorityLevel.fromValue(priority);
    List<Notice> notices = noticeService.getNoticesByPriority(priorityLevel);
    return ResponseEntity.ok(notices);
}
*/
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
        // return noticeService.modifyNotice(noticeId, dto.getTitle(), dto.getContent())
        return noticeService.modifyNotice(noticeId, dto)
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
