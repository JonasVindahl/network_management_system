# Frontend Views

## Server Routes

`WebViewController` maps browser routes to Thymeleaf templates:

- `/` -> redirects to `/login`
- `/login` -> `login.html`
- `/frontend` -> `frontend.html`
- `/collective-sale` -> `collective-sale.html`
- `/normal-sale` -> `normal-sale.html`

## Main Templates

### `templates/login.html`

Purpose:

- Login screen.
- Posts CPF and password to `/api/auth/login`.
- Saves the returned token client-side.

Observed language:

- The page contains Danish labels such as "Brugernavn (CPF)" and "Adgangskode".

### `templates/frontend.html`

Purpose:

- Main manager dashboard.
- Displays performance, productivity, revenue, material multipliers, notices, and related tables.

Observed API calls include:

- `/api/performance`
- `/api/productivity`
- `/api/revenue`
- `/api/cooperative/materials`
- `/api/cooperative/lastsales`
- `/api/cooperative/lastsales/all`
- `/api/multipliers`
- `/api/notices`

### `templates/normal-sale.html`

Purpose:

- Normal sale workflow page.
- Lists active sales and history.
- Creates, edits, completes, cancels, and downloads normal-sale PDFs.

Observed API calls include:

- `/api/sales`
- `/api/sales/active`
- `/api/sales/{saleId}`
- `/api/sales/{saleId}/complete`
- `/api/sales/{saleId}/cancel`
- `/api/reports/pdf/normal-sale/{saleId}`

### `templates/collective-sale.html`

Purpose:

- Collective sale workflow page.
- Lists active sales, invitations, own sales, and history.
- Creates collective sales, invites cooperatives, joins, updates contribution, changes material/price, leaves, cancels, and downloads PDFs.

Observed API calls include:

- `/api/collective-sale`
- `/api/collective-sale/invitations`
- `/api/collective-sale/{saleId}/invite`
- `/api/collective-sale/{saleId}/join`
- `/api/collective-sale/{saleId}/contribution`
- `/api/collective-sale/{saleId}/material`
- `/api/collective-sale/{saleId}/price`
- `/api/collective-sale/{saleId}/leave`
- `/api/collective-sale/my`
- `/api/reports/pdf/collective-sale/{saleId}`

## Report Templates

### `templates/reports/normal-sale-report.html`

Used by `PdfReportService.generateNormalSaleReport`.

Displays:

- sale ID and status
- material and buyer
- responsible worker
- cooperative
- created, expected, sold, and cancelled timestamps
- weight, price per kg, total revenue

### `templates/reports/collective-sale-report.html`

Used by `PdfReportService.generateCollectiveSaleReport`.

Displays:

- collective sale ID and status
- viewer cooperative when available
- material and buyer
- creator cooperative ID
- total cooperatives
- timestamps
- total weight, price per kg, total revenue
- contribution table

## Static Tester Pages

`src/main/resources/static/test.html`:

- API tester page.
- Logs in through `/api/auth/login`.
- Saves a token and lets the user call arbitrary API paths.

`src/main/resources/static/collective-sale.html`:

- Older collective sales tester page.
- Contains Danish UI text.
- Calls collective sale, report, buyer, cooperative, and stock APIs.

## Related Notes

- [[API/API Reference]]
- [[Domain/Normal Sales]]
- [[Domain/Collective Sales]]
- [[Domain/Reports and PDFs]]

