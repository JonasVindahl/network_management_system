# Executive Summary

Audience: project owners, supervisors, and anyone who needs the state of the system without reading the code.
Scope: the `network_management_system` repository as observed on branch `main`, last commit 21 May 2026.

## What the system is

A cooperative waste management platform built as a single Spring Boot application. It lets several waste picker cooperatives register material weighings, track stock, sell materials individually or jointly with other cooperatives, generate sale reports as PDFs, publish notices, and motivate workers through a gamification layer with achievements, levels, and leaderboards.

The application serves both a JSON REST API and server-rendered HTML pages from the same deployment, backed by one PostgreSQL database.

## Current state

The system is feature complete for its core flows and is deployed automatically to a self-hosted server on every push to `main`. Scale in numbers:

- 96 Java files, roughly 7,500 lines of application code
- 14 functional packages covering auth, analytics, buyers, materials, sales, collective sales, reports, notices, multipliers, three gamification modules, page routing, and configuration
- 23 database tables plus seed data for levels and achievements
- around 60 HTTP endpoints across REST and page routes, documented through Swagger UI
- 4 Thymeleaf screens (login, dashboard, normal sale, collective sale) and 2 PDF report templates
- 3 scheduled jobs (monthly random multiplier, daily achievement and level evaluation, weekly and monthly leaderboard snapshots)
- 309 commits between February and May 2026

## What works

Authentication and role separation. Login issues a JWT carrying role, cooperative, and worker identity. Three roles exist: admin, manager, and worker. A shared `PermissionHelper` enforces that managers and workers act only within their own cooperative, while admins must name the cooperative they act on.

Material intake and stock. Weighings are recorded as deltas against a per-bag state, so a re-weighed bag does not double count. The computed delta updates cooperative stock in the same flow.

Normal sales. Managers create, edit, complete, or cancel sales. Completion stamps `sold_at` and subtracts stock. History and active sales are served through a combined endpoint that merges normal and collective sales.

Collective sales. A cooperative creates a sale, invites others, and participants join with a contribution weight. Contribution changes reserve or release stock atomically. Cancelling returns all reserved stock.

Reporting. Both sale types produce JSON reports and downloadable PDFs rendered from Thymeleaf templates.

Gamification. Achievements, XP, levels, and leaderboard snapshots are computed by scheduled jobs, with cooperative and material multipliers affecting XP.

Delivery. GitHub Actions builds and deploys through Docker Compose on a self-hosted Proxmox runner.

## Main risks

Three items are business relevant rather than merely technical.

Secrets are committed to the repository. `src/main/resources/application.properties` contains a database host, username, password, and the JWT signing secret in plain text. Environment variables in Docker Compose override them at runtime, but the values are in git history and must be treated as compromised. Rotating the database password and the JWT secret, then removing the values from the file, is the highest priority action. A leaked JWT secret allows an attacker to forge tokens for any role, including admin.

Collective sale completion deducts stock twice. When a cooperative sets its contribution weight, `CollectiveSaleService.updateContribution` already reserves the stock by subtracting it from `current_stock_kg`. When the creator later confirms the sale, `confirmCollectiveSale` calls `StockRepository.recordSale`, which subtracts the same weight from `current_stock_kg` a second time. Two outcomes are possible, both wrong: if the cooperative holds enough remaining stock, its balance is reduced by twice the sold amount; if it does not, the update matches zero rows, that return value is not checked, and the sale is marked sold with `total_sold_kg` never updated while revenue shares are still written. Stock balances feed analytics, sale eligibility, and the dashboard, so this corrupts numbers the cooperatives act on. The fix is to move the reserved amount into `total_sold_kg` at completion without subtracting `current_stock_kg` again, and to check the affected row count.

There is effectively no automated test coverage. One Spring Boot context test exists, and the Docker build runs `package -DskipTests`, so the CI pipeline never executes tests before deploying. Stock accounting, authorization rules, and sale lifecycles are currently verified only by hand.

## Secondary findings

- JWTs are accepted through a `token` query parameter, which leaks into browser history, proxy logs, and referrer headers.
- `server.error.include-message=always` and `security=DEBUG` logging are production-unsafe defaults.
- `GET /api/performance` accepts a date range that the underlying query ignores, so filtered results are silently wrong.
- Normal sales do not reserve stock at creation, only check it at completion, so two open sales can promise the same stock.
- Many services map SQL results by column index, which breaks silently when a query changes.
- The UI mixes Danish and English text, and two competing collective sale pages exist (a Thymeleaf template and an older static tester).
- `Planning/Known Gaps and Follow-ups` predates the collective sale completion feature and the analytics refactor, so parts of it no longer match the code. The findings above were re-verified against the current source.

## Recommended sequence

1. Rotate the database password and JWT secret, move both to environment variables, and purge them from the properties file.
2. Correct the stock accounting in collective sale completion so reserved weight is not subtracted a second time.
3. Make CI run `./mvnw test` as a separate step before the image build, then add tests for stock accounting and authorization first, since those carry the money and access risk.
4. Harden configuration: remove query parameter tokens, disable error message exposure, and lower the security log level for production.
5. Fix the remaining defects listed above, starting with the ignored date filter on `/api/performance`.
6. Consolidate the frontend: one collective sale page, one UI language.

Items 1 to 3 are the ones worth escalating. The rest are normal cleanup.

## Related Notes

- [[Architecture/System Overview|System Overview]]
- [[Architecture/Runtime and Security|Runtime and Security]]
- [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]]
- [[Planning/Code Inventory|Code Inventory]]
- [[Operations/Build Test Deploy|Build, Test, and Deploy]]
