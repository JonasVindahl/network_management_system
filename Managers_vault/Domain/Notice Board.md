# Notice Board

## Purpose

The notice board stores global and cooperative-specific notices. Global notices have `cooperative_id = null`.

## Main Files

- `NoticeController`
- `NoticeService`
- `NoticeRepository`
- `Notice`
- `NoticeDTO`

## Data Model

Entity:

- `Notice`

Table:

- `notice_board`

Fields:

- `noticeId`
- `title`
- `content`
- `createdAt`
- `lastUpdated`
- `priority`
- `createdBy`
- `expiresAt`
- `cooperativeId`

## Sanitization

`NoticeService` sanitizes title and content using OWASP Java HTML Sanitizer:

- `Sanitizers.FORMATTING`
- `Sanitizers.BLOCKS`

Sanitization is applied on create and update.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/notices` | Active notices for a cooperative plus global notices. |
| GET | `/api/notices/global` | Active global notices only. Admin only. |
| GET | `/api/notices/{noticeId}` | Notice by ID with access check. |
| GET | `/api/notices/filter` | Active notices filtered by priority. |
| POST | `/api/notices` | Create notice. Manager/admin only. |
| PUT | `/api/notices/{noticeId}` | Update notice. Manager/admin only. |
| DELETE | `/api/notices/{noticeId}` | Delete notice. Manager/admin only. |

## Access Rules

Read access:

- Admin can read all notices.
- Managers and workers can read global notices and notices for their own cooperative.

Global list:

- `GET /api/notices/global` requires admin.

Write access:

- Workers are rejected.
- Admin can write global or cooperative notices.
- Manager writes are scoped to the manager's cooperative.
- Manager cannot modify global notices.
- Manager cannot modify another cooperative's notices.

## Validation

`NoticeDTO` requires:

- nonblank title
- nonblank content
- priority between 1 and 3
- future expiry timestamp

## Related Notes

- [[API/API Reference|API Reference]]
- [[Models/Database Schema|Database Schema]]

