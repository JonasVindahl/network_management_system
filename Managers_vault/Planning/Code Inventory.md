# Code Inventory

This inventory accounts for the observed source, resources, database, and deployment files in the repository.

## Root Application Files

- `NetworkManagementSystemApplication.java`: Spring Boot entry point.
- `MonthlyRandomMultiplier.java`: monthly random cooperative multiplier scheduler.
- `Last5SalesController.java`: direct `JdbcTemplate` endpoint for last five sold normal sales by material.

## Authentication Package

- `AuthController.java`: login endpoint and JWT response creation.
- `AuthRequest.java`: login request body.
- `AuthResponse.java`: login response body.
- `AuthenticatedUser.java`: helper for current JWT principal and role checks.
- `JwtAuthFilter.java`: bearer/query/cookie JWT authentication filter.
- `JwtPrincipal.java`: authenticated principal payload.
- `JwtUtil.java`: JWT signing, parsing, and validation.
- `PermissionHelper.java`: shared authorization and target scoping rules.
- `SecurityConfig.java`: Spring Security filter chain and password encoder.
- `WorkerDetailsService.java`: loads worker credentials and worker metadata from the database.
- `WorkerInfo.java`: worker ID, cooperative ID, and role holder.
- `auth.md`: source-side authentication notes already present in the repository.

## Cooperative Analytics Package

- `AnalyticsController.java`: performance, productivity, revenue, stock, materials, and last-sales endpoints.
- `AnalyticsService.java`: authorization and mapping logic for analytics responses.
- `AnalyticsRepository.java`: native SQL analytics queries.
- `CooperativeController.java`: cooperative lookup endpoint.
- `CooperativeService.java`: cooperative lookup mapping and error handling.
- `CooperativeRepository.java`: native query for cooperative IDs and names.
- `CooperativeEntity.java`: JPA entity for `cooperative`.
- `CooperativeDTO.java`: cooperative ID/name response.
- `CooperativePerformanceDTO.java`: aggregate performance response.
- `WorkerProductivityDTO.java`: worker productivity response.
- `RevenueDTO.java`: revenue response.
- `StockByMaterialDTO.java`: stock summary response.
- `Last5SalesDTO.java`: last sales response.

## Buyers Package

- `BuyerController.java`: buyer lookup endpoint.
- `BuyerService.java`: buyer mapping and error handling.
- `BuyerRepository.java`: native buyer lookup query.
- `BuyerEntity.java`: JPA entity for `buyers`.
- `BuyerDTO.java`: buyer ID/name response.

## Materials Package

- `MaterialController.java`: admin material weighing endpoint.
- `MaterialService.java`: measurement delta, bag state, and stock update logic.
- `MaterialRequest.java`: material weighing request body.
- `Measurement.java`: JPA entity for `measurements`.
- `MeasurementRepository.java`: native insert for measurements.
- `MaterialBagState.java`: JPA entity for `material_bag_state`.
- `MaterialBagStateRepository.java`: bag state lookup and upsert queries.
- `Stock.java`: JPA entity for `stock`.
- `StockController.java`: manual stock add endpoint.
- `StockRepository.java`: stock add, sale record, reservation, release, and lookup queries.
- `AddStockDTO.java`: manual stock add request body.

## Normal Sales Package

- `SalesController.java`: normal sale endpoints plus active/history combined sale endpoints.
- `SalesService.java`: normal sale lifecycle and combined sale mapping.
- `SalesRepository.java`: native SQL for normal and collective sale lists plus normal sale writes.
- `CreateSaleDTO.java`: normal sale create request.
- `UpdateSaleDTO.java`: normal sale update request.
- `SaleDTO.java`: normal/collective sale list response.

## Collective Sale Package

- `CollectiveSaleController.java`: collective sale lifecycle endpoints.
- `CollectiveSaleService.java`: invitations, joining, contribution stock reservation, material/price updates, leave, cancel, and list mapping.
- `CollectiveSaleRepository.java`: native collective sale and contribution queries.
- `CollectiveSaleEntity.java`: JPA entity for `collective_sale`.
- `ActiveCollectiveSaleDTO.java`: active/history collective sale response.
- `CollectiveSaleInvitationDTO.java`: invitation response.
- `CreateCollectiveSaleDTO.java`: create request.
- `InviteCooperativeDTO.java`: invite request.
- `UpdateContributionDTO.java`: contribution weight request.
- `UpdateSaleMaterialDTO.java`: material update request.
- `UpdateSalePriceDTO.java`: price update request.

## Sale Reports Package

- `SaleReportsController.java`: normal sale JSON report endpoint.
- `SaleReportsService.java`: normal report access checks and mapping.
- `SaleReportsRepository.java`: normal sale report SQL.
- `ReportsController.java`: collective sale JSON report endpoint.
- `ReportsService.java`: collective report access checks and mapping.
- `ReportsRepository.java`: collective sale report SQL.
- `PdfReportController.java`: normal and collective PDF download endpoints.
- `PdfReportService.java`: Thymeleaf rendering and OpenHTMLToPDF conversion.
- `SaleReportDTO.java`: normal report response.
- `CollectiveSaleReportDTO.java`: collective report response.
- `ContributionDetailDTO.java`: collective report contribution response.

## Notice Board Package

- `NoticeController.java`: notice CRUD and filters.
- `NoticeService.java`: access rules, sanitization, create/update/delete logic.
- `NoticeRepository.java`: active notice JPQL queries.
- `Notice.java`: JPA entity for `notice_board`.
- `NoticeDTO.java`: notice request body and validation.

## Multiplier Package

- `CooperativeMaterialMultiplierController.java`: multiplier create/update and reads.
- `CooperativeMaterialMultiplierService.java`: multiplier validation, ownership checks, and mapping.
- `CooperativeMaterialMultiplierRepository.java`: multiplier lookup queries.
- `CooperativeMaterialMultiplier.java`: JPA entity for `cooperative_material_multiplier`.
- `MultiplierDTO.java`: multiplier request/response.

## Gamification Achievements Package

- `AchievementController.java`: achievement list, XP override, worker monthly summary, top month, top day.
- `AchievementService.java`: achievement queries, XP override upsert, worker summaries, achievement evaluation.
- `AchievementDTO.java`: achievement response.
- `UpdateAchievementXPDTO.java`: XP override request.
- `WorkerMonthSummaryDTO.java`: worker monthly achievement summary.

## Gamification Levels Package

- `LevelController.java`: level list and worker level endpoints.
- `LevelService.java`: worker level creation, XP summing, level recalculation.
- `LevelDTO.java`: level response.
- `AchievementEvaluationScheduler.java`: daily achievement evaluation and level recalculation scheduler.

## Gamification Leaderboard Package

- `LeaderboardController.java`: current and historical leaderboard endpoints.
- `LeaderboardService.java`: current display selection, snapshot computation, persistence, and reads.
- `LeaderboardDTO.java`: leaderboard response and nested entry DTO.
- `LeaderboardScheduler.java`: weekly and monthly leaderboard snapshot scheduler.

## Frontend and Config Packages

- `frontend/WebViewController.java`: browser route to template mapping.
- `config/OpenApiConfig.java`: bearer JWT OpenAPI security scheme.

## Resource Files

- `src/main/resources/application.properties`: Spring, datasource, JWT, logging, Thymeleaf, and static resource configuration.
- `src/main/resources/templates/login.html`: login page.
- `src/main/resources/templates/frontend.html`: manager dashboard.
- `src/main/resources/templates/normal-sale.html`: normal sales page.
- `src/main/resources/templates/collective-sale.html`: collective sales page.
- `src/main/resources/templates/reports/normal-sale-report.html`: normal sale PDF template.
- `src/main/resources/templates/reports/collective-sale-report.html`: collective sale PDF template.
- `src/main/resources/static/test.html`: generic API tester.
- `src/main/resources/static/collective-sale.html`: older static collective sale tester.

## Test Files

- `NetworkManagementSystemApplicationTests.java`: Spring Boot context load test.

## Database and Deployment Files

- `Database/DMS_db_schema.sql`: PostgreSQL schema and seed data for levels and achievements.
- `pom.xml`: Maven project, Spring Boot parent, dependencies, Java version.
- `Dockerfile`: multi-stage Java 25 build and runtime image.
- `docker-compose.yml`: app service, port mapping, and environment variables.
- `.github/workflows/deploy.yml`: build and deploy workflow on self-hosted runner.
- `.dockerignore`: Docker build ignore rules.
- `.gitignore`: Git ignore rules.
- `.gitattributes`: Git attributes.
- `.mvn/wrapper/maven-wrapper.properties`: Maven wrapper configuration.
- `mvnw`: Unix Maven wrapper.
- `mvnw.cmd`: Windows Maven wrapper.
- `HELP.md`: generated Spring Boot help and reference links.

## Related Notes

- [[Architecture/System Overview|System Overview]]
- [[Models/Java Models and DTOs|Java Models and DTOs]]
- [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]]

