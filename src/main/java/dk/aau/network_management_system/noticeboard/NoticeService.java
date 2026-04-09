package dk.aau.network_management_system.noticeboard;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeService.class);

    private final NoticeRepository noticeRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, AuthenticatedUser authenticatedUser) {
        this.noticeRepository = noticeRepository;
        this.authenticatedUser = authenticatedUser;
    }

    public List<Notice> getNotices(Long cooperativeId) {
        try {
            return noticeRepository.findActiveNoticesForCooperative(
                resolveCooperativeId(cooperativeId), Instant.now()
            );
        } catch (DataAccessException e) {
            log.error("Database error while fetching notices for cooperative {}", cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving notices");
        }
    }

    public List<Notice> getAllActiveNotices() {
        requireAdmin();
        try {
            return noticeRepository.findActiveGlobalNotices(Instant.now());
        } catch (DataAccessException e) {
            log.error("Database error while fetching all active global notices", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving global notices");
        }
    }

    public Optional<Notice> getNoticeById(Long noticeId) {
        try {
            Optional<Notice> notice = noticeRepository.findById(noticeId);
            notice.ifPresent(n -> requireReadAccess(n));
            return notice;
        } catch (DataAccessException e) {
            log.error("Database error while fetching notice {}", noticeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving notice");
        }
    }

    public List<Notice> getNoticesByPriority(int priority, Long cooperativeId) {
        try {
            return noticeRepository.findByPriorityAndCooperativeId(
                priority, resolveCooperativeId(cooperativeId), Instant.now()
            );
        } catch (DataAccessException e) {
            log.error("Database error while fetching notices by priority {} for cooperative {}",
                    priority, cooperativeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving notices by priority");
        }
    }

    public Notice createNotice(NoticeDTO dto) {
        requireManagerOrAdmin();
        try {
            Notice notice = new Notice(
                dto.getTitle(),
                dto.getContent(),
                dto.getPriority(),
                authenticatedUser.getWorkerId(),
                dto.getExpiresAt(),
                resolveCooperativeIdForWrite(dto.getCooperativeId())
            );
            return noticeRepository.save(notice);
        } catch (DataAccessException e) {
            log.error("Database error while creating notice", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error creating notice");
        }
    }

    public Optional<Notice> modifyNotice(Long noticeId, NoticeDTO dto) {
        requireManagerOrAdmin();
        try {
            return noticeRepository.findById(noticeId).map(notice -> {
                requireWriteAccess(notice);
                applyUpdates(notice, dto);
                notice.setLastUpdated(Instant.now());
                return noticeRepository.save(notice);
            });
        } catch (DataAccessException e) {
            log.error("Database error while modifying notice {}", noticeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error modifying notice");
        }
    }

    public boolean deleteNotice(Long noticeId) {
        requireManagerOrAdmin();
        try {
            return noticeRepository.findById(noticeId).map(notice -> {
                requireWriteAccess(notice);
                noticeRepository.deleteById(noticeId);
                return true;
            }).orElse(false);
        } catch (DataAccessException e) {
            log.error("Database error while deleting notice {}", noticeId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error deleting notice");
        }
    }

    private void requireManagerOrAdmin() {
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers can't perform this action");
        }
    }

    private void requireAdmin() {
        if (!authenticatedUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Only Admins can perform this action");
        }
    }

    private void requireReadAccess(Notice notice) {
        if (authenticatedUser.isAdmin()) return;
        Long myCoopId = authenticatedUser.getCooperativeId();
        boolean isGlobal = notice.getCooperativeId() == null;
        boolean isOwnCoop = myCoopId.equals(notice.getCooperativeId());
        if (!isGlobal && !isOwnCoop) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You don't have access to this notice - you can only see notices from your own cooperative or global notices");
        }
    }

    private void requireWriteAccess(Notice notice) {
        if (authenticatedUser.isAdmin()) return;
        Long myCoopId = authenticatedUser.getCooperativeId();
        boolean isGlobal = notice.getCooperativeId() == null;
        boolean isOwnCoop = myCoopId.equals(notice.getCooperativeId());
        if (isGlobal || !isOwnCoop) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You don't have access to modify this notice - you can only modify notices from your own cooperative, and you can't modify global notices");
        }
    }

    private Long resolveCooperativeId(Long requestedCooperativeId) {
        if (!authenticatedUser.isAdmin()) {
            return authenticatedUser.getCooperativeId();
        }
        if (requestedCooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Admin has to give a cooperativeId as a parameter");
        }
        return requestedCooperativeId;
    }

    private Long resolveCooperativeIdForWrite(Long dtoCooperativeId) {
        if (authenticatedUser.isManager()) {
            return authenticatedUser.getCooperativeId();
        }
        return dtoCooperativeId;
    }

    private void applyUpdates(Notice notice, NoticeDTO dto) {
        if (dto.getTitle() != null && !dto.getTitle().isBlank())
            notice.setTitle(dto.getTitle());
        if (dto.getContent() != null && !dto.getContent().isBlank())
            notice.setContent(dto.getContent());
        if (dto.getPriority() != null)
            notice.setPriority(dto.getPriority());
        if (dto.getExpiresAt() != null)
            notice.setExpiresAt(dto.getExpiresAt());
        if (authenticatedUser.isAdmin() && dto.getCooperativeId() != null)
            notice.setCooperativeId(dto.getCooperativeId());
    }
}