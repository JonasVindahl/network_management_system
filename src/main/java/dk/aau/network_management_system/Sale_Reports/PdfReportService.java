package dk.aau.network_management_system.Sale_Reports;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfReportService {

    private final TemplateEngine templateEngine;
    private final SaleReportsService saleReportsService;
    private final ReportsService reportsService;

    @Autowired
    public PdfReportService(TemplateEngine templateEngine,
                            SaleReportsService saleReportsService,
                            ReportsService reportsService) {
        this.templateEngine   = templateEngine;
        this.saleReportsService = saleReportsService;
        this.reportsService     = reportsService;
    }

    public byte[] generateNormalSaleReport(Long saleId, Long targetCooperativeId) {
        SaleReportDTO report = saleReportsService.getSaleReport(saleId, targetCooperativeId);

        Context ctx = new Context();
        ctx.setVariable("report",      report);
        ctx.setVariable("generatedAt", LocalDateTime.now());

        String html = templateEngine.process("reports/normal-sale-report", ctx);
        return renderHtmlToPdf(html);
    }

    public byte[] generateCollectiveSaleReport(Long saleId, Long targetCooperativeId) {
        CollectiveSaleReportDTO report = reportsService.getCollectiveSaleReport(saleId, targetCooperativeId);

        Context ctx = new Context();
        ctx.setVariable("report",                report);
        ctx.setVariable("generatedAt",           LocalDateTime.now());
        ctx.setVariable("viewerCooperativeName", resolveViewerName(report, targetCooperativeId));

        String html = templateEngine.process("reports/collective-sale-report", ctx);
        return renderHtmlToPdf(html);
    }
    
    private byte[] renderHtmlToPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF: " + e.getMessage(), e);
        }
    }

    private String resolveViewerName(CollectiveSaleReportDTO report, Long targetCooperativeId) {
        if (targetCooperativeId == null || report.getContributions() == null) return null;
        return report.getContributions().stream()
                .filter(c -> targetCooperativeId.equals(c.getCooperativeId()))
                .map(c -> c.getCooperativeName())
                .findFirst()
                .orElse(null);
    }
}
