# Reports and PDFs

## Purpose

Reports expose detailed sale data as JSON and PDF. PDF generation uses Thymeleaf HTML templates rendered through OpenHTMLToPDF.

## Main Files

- `SaleReportsController`
- `SaleReportsService`
- `SaleReportsRepository`
- `ReportsController`
- `ReportsService`
- `ReportsRepository`
- `PdfReportController`
- `PdfReportService`
- `SaleReportDTO`
- `CollectiveSaleReportDTO`
- `ContributionDetailDTO`
- `templates/reports/normal-sale-report.html`
- `templates/reports/collective-sale-report.html`

## Normal Sale JSON Report

Endpoint:

- `GET /api/reports/sales/normal/{saleId}`

Access:

- manager or admin

Behavior:

1. Verifies the sale exists.
2. Workers are rejected.
3. Admins pass access validation.
4. Managers must match their own cooperative and the sale must belong to that cooperative.
5. Reads sale, material, buyer, worker, and cooperative data.
6. Maps data into `SaleReportDTO`.

Returned fields include:

- sale ID and status
- material ID/name
- buyer ID/name
- responsible worker ID/name
- cooperative ID/name
- created, expected, sold, cancelled timestamps
- weight
- price per kg
- total revenue

## Collective Sale JSON Report

Endpoint:

- `GET /api/reports/sales/collective/{saleId}`

Access:

- manager or admin

Behavior:

1. Verifies the collective sale exists.
2. Workers are rejected.
3. Admins pass access validation.
4. Managers must match their own cooperative and that cooperative must have a contribution row for the sale.
5. Reads sale data and all contributions.
6. Maps data into `CollectiveSaleReportDTO`.

Returned fields include:

- collective sale ID and status
- material ID/name
- buyer ID/name
- timestamps
- total weight
- price per kg
- total revenue
- total cooperatives
- creator cooperative ID
- contribution details

## PDF Generation

Endpoints:

- `GET /api/reports/pdf/normal-sale/{saleId}`
- `GET /api/reports/pdf/collective-sale/{saleId}`

Behavior:

1. Controller resolves cooperative scope.
2. `PdfReportService` calls the corresponding JSON report service.
3. Thymeleaf renders HTML from `templates/reports`.
4. OpenHTMLToPDF renders PDF bytes.
5. Controller returns `application/pdf` with attachment headers.

Generated filenames:

- `normal-sale-report-{saleId}.pdf`
- `collective-sale-report-{saleId}.pdf`

## Template Data

Normal sale template variables:

- `report`
- `generatedAt`

Collective sale template variables:

- `report`
- `generatedAt`
- `viewerCooperativeName`

## Related Notes

- [[Normal Sales]]
- [[Collective Sales]]
- [[Architecture/Frontend Views|Frontend Views]]
- [[API/API Reference|API Reference]]

