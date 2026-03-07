package dk.aau.network_management_system.noticeboard;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, AuthenticatedUser authenticatedUser) {
        this.noticeRepository = noticeRepository;
        this.authenticatedUser = authenticatedUser;
    }

    // Public API

    // GET - get all active notices (non-expired)
    public List<Notice> getNotices(Long cooperativeId) {
        return noticeRepository.findActiveNoticesForCooperative(
            resolveCooperativeId(cooperativeId), Instant.now()
        );
    }

    // GET - get all active notices for a specific cooperative
    // public List<Notice> getNoticesForCooperative(Long cooperativeId) { - fjernet, da det kan håndteres i getNotices() ved at sende cooperativeId som parameter
        // return noticeRepository.findActiveNoticesForCooperative(cooperativeId, Instant.now());
    // }

    // GET - get all active global notices + noticed for users own cooperative
    public List<Notice> getAllActiveNotices() {
        requireAdmin();
        return noticeRepository.findActiveGlobalNotices(Instant.now());
    }

    // GET - get single notice by ID - this can be used by both Workers and Managers, but they can only access notices from their own cooperative or global notices
    public Optional<Notice> getNoticeById(Long noticeId) {
        Optional<Notice> notice = noticeRepository.findById(noticeId);
        notice.ifPresent(n -> requireReadAccess(n));
        return notice;
    }

    // GET - get notices filtered by priority
    public List<Notice> getNoticesByPriority(int priority, Long cooperativeId) {
        return noticeRepository.findByPriorityAndCooperativeId(priority, resolveCooperativeId(cooperativeId), Instant.now());
    }

    // GET updates
    // - getNotices() replaces getNoticesForCooperative() - the cooperativeId resolves via JWT instead of request param
    // - getAllActiveNotices() has gotten requireAdmin() check, only Admins can see global notices
    // - getNoticeById() has gotten requireReadAccess() - worker manager kan se egen cooperative eller globale notices, admin kan se alle notices
    // - getNoticesByPriority() uses findByPriorityAndCooperativeId() which is cooperatve-scoped, instead of the oldfindByPriority() which returned notices from all cooperatives, which was a security risk.


     // Helper methods
    // POST - create a new notice. Only Managers and Admins.

    public Notice createNotice(NoticeDTO dto) {
        requireManagerOrAdmin(); //Workers kan nu ikke oprette notices
            Notice notice = new Notice(
                dto.getTitle(),
                dto.getContent(),
                dto.getPriority(),
                authenticatedUser.getWorkerId(), // Hent createdBy fra JWT - i stedet for request body - forhindrer bruger u at udgive sig for at være en anden bruger
                dto.getExpiresAt(),
                resolveCooperativeIdForWrite(dto.getCooperativeId()) // Manager låses automatisk til egen cooperative fra JWT, Admin kan selv sætte cooperativId eller sende null for global notice
            );
            return noticeRepository.save(notice);
    }


    /* // PUT - modify an existing notice. Only Manager (for own cooperative) and Admin (for all notices)

    public Optional<Notice> modifyNotice(Long noticeId, String title, String content) {
        return noticeRepository.findById(noticeId).map(notice -> {
            if (title != null && !title.isBlank()) notice.setTitle(title);
            if (content != null && !content.isBlank()) notice.setContent(content);
            notice.setLastUpdated(Instant.now());
            return noticeRepository.save(notice);
        });
    } */

      public Optional<Notice> modifyNotice(Long noticeId, NoticeDTO dto) {
        requireManagerOrAdmin();
        return noticeRepository.findById(noticeId).map(notice -> {
           requireWriteAccess(notice);
           applyUpdates(notice, dto);
           notice.setLastUpdated(Instant.now());
           return noticeRepository.save(notice);
        });
} 

    // DELETE - remove a notice
    public boolean deleteNotice(Long noticeId) {
        requireManagerOrAdmin();
        return noticeRepository.findById(noticeId).map(notice -> {
            requireWriteAccess(notice);
            noticeRepository.deleteById(noticeId);
            return true;
        }).orElse(false);
    }





// ---------------------------------------------------------------------------------------
// Private helpers: permission checks
// ---------------------------------------------------------------------------------------

// Throws FORBIDDEN if the user is a Worker
private void requireManagerOrAdmin(){
    if (authenticatedUser.isWorker()){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Workers can't perform this action");
        
    }
}

// Throws FORBIDDEN if the user is not an Admin
private void requireAdmin(){
    if (!authenticatedUser.isAdmin()){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Only Admins can perform this action");
    }
}

// Throws FORBIDDEN if the Worker/Manager tries to read a notice out of their own cooperative (but they can read global notices)
private void requireReadAccess(Notice notice){
    if(authenticatedUser.isAdmin()) return;
    Long myCoopId = authenticatedUser.getCooperativeId();
    boolean isGlobal = notice.getCooperativeId() == null;
    boolean isOwnCoop = myCoopId.equals(notice.getCooperativeId());
    if(!isGlobal && !isOwnCoop){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "You don't have access to this notice - you can only see notices from your own cooperative or global notices");
    }
}



// Throws FORBIDDEN if the manager tries to modify a notice out of their own cooperative, Admins can modify all notices
// Managers may not change global notices (cooperativeId = null) as this would affect all cooperatives, only Admins can create and modify global notices
private void requireWriteAccess(Notice notice){
    if (authenticatedUser.isAdmin()) return;
    Long myCoopId = authenticatedUser.getCooperativeId();
    boolean isGlobal = notice.getCooperativeId() == null;
    boolean isOwnCoop = myCoopId.equals(notice.getCooperativeId());
    if(isGlobal || !isOwnCoop){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "You don't have access to modify this notice - you can only modify notices from your own cooperative, and you can't modify global notices");
    }
}


// ----------------------------------------------------------------------------------
// Private helper
// ----------------------------------------------------------------------------------

// Used for GET: Worker/Managers come to JWT-cooperative, Admin can give parameter cooperativeId or leave it out to get global notices
private Long resolveCooperativeId(Long requestedCooperativeId){
    if (!authenticatedUser.isAdmin()){
        return authenticatedUser.getCooperativeId();
    }
    if (requestedCooperativeId == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Admin has to give a cooperativeId as a parameter");
    }
    return requestedCooperativeId;
}

// Used for POST/PUT : Managers come to JWT-cooperative, Admin uses DTO-value (null means global notice)
private Long resolveCooperativeIdForWrite(Long dtoCooperativeId){
    if (authenticatedUser.isManager()){
        return authenticatedUser.getCooperativeId();
    }
    return dtoCooperativeId; // Admin can set cooperativeId or leave it null for global notice
}

// ---------------------------------------------------------------------------------------
// Private helpers: field updates
// ---------------------------------------------------------------------------------------

//Update only the fields that are present in the DTO (non-null)
// Managers can not change cooperativeId - only Admin
private void applyUpdates(Notice notice, NoticeDTO dto){
    if (dto.getTitle() != null && !dto.getTitle().isBlank())
        notice.setTitle(dto.getTitle());
    if (dto.getContent() != null && !dto.getContent().isBlank())
        notice.setContent(dto.getContent());
    if (dto.getPriority() != null)
        notice.setPriority(dto.getPriority());
    if (dto.getExpiresAt() != null)
        notice.setExpiresAt(dto.getExpiresAt());
    if (authenticatedUser.isAdmin() && dto.getCooperativeId() != null) // Only Admin can change cooperativeId, and it can be set to null for global notice
        notice.setCooperativeId(dto.getCooperativeId());

    }
    
}


// NoticeService changes Dwaj:
// - Added AuthenticatedUser injection to enable permission checks
// - getNotices() replaces getNoticesForCooperative() - cooperativeId now resolved via JWT instead of request param
// - getAllActiveNotices() restricted to Admin only via requireAdmin()
// - getNoticeById() now checks read access via requireReadAccess() - Workers/Managers can only see own cooperative or global notices
// - getNoticesByPriority() now uses cooperative-scoped query findByPriorityAndCooperativeId() instead of old findByPriority() which had no scope
// - createNotice() restricted to Manager/Admin via requireManagerOrAdmin(), createdBy now fetched from JWT instead of request body
// - modifyNotice() restricted to Manager/Admin, Managers can only modify own cooperative notices via requireWriteAccess()
// - deleteNotice() restricted to Manager/Admin, Managers can only delete own cooperative notices via requireWriteAccess()
// - Added private helpers: requireManagerOrAdmin(), requireAdmin(), requireReadAccess(), requireWriteAccess()
// - Added private helpers: resolveCooperativeId(), resolveCooperativeIdForWrite(), applyUpdates()
