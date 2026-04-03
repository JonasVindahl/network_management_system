package dk.aau.network_management_system.frontend;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.aau.network_management_system.Sales.SaleDTO;
import dk.aau.network_management_system.Sales.SalesService;
import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;


@Controller
@RequestMapping("/web/sales")
public class SalesWebController {
    
    private static final Logger log = LoggerFactory.getLogger(SalesWebController.class);
    
    private final SalesService salesService;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser;
    
    @Autowired
    public SalesWebController(SalesService salesService, 
                             PermissionHelper permissionHelper,
                             AuthenticatedUser authenticatedUser) {
        this.salesService = salesService;
        this.permissionHelper = permissionHelper;
        this.authenticatedUser = authenticatedUser;
    }
    
    @GetMapping("/list")
public String salesList(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String status,
        Model model) {
    
    try {
        // Get user's cooperative
        Long cooperativeId = authenticatedUser.getCooperativeId();
        
        // ====== DEBUG LOGGING ======
        log.info("=== SALES LIST DEBUG ===");
        log.info("cooperativeId: {}", cooperativeId);
        log.info("type: {}", type);
        log.info("status: {}", status);
        log.info("isAdmin: {}", authenticatedUser.isAdmin());
        log.info("workerId: {}", authenticatedUser.getWorkerId());
        // ============================
        
        if (cooperativeId == null) {
            log.error("User has no cooperative ID");
            model.addAttribute("errorMessage", "User has no cooperative assigned");
            model.addAttribute("sales", new ArrayList<>());
            return "sales/list";
        }
        
        // Default values
        if (type == null || type.isEmpty()) type = "ALL";
        if (status == null || status.isEmpty()) status = "ALL";
        
        // Fetch sales based on status
        List<SaleDTO> sales = new ArrayList<>();
        
        if ("ACTIVE".equalsIgnoreCase(status)) {
            log.info("Calling salesService.getActiveSales({}, {})", cooperativeId, type);
            sales = salesService.getActiveSales(cooperativeId, type);
            log.info("getActiveSales returned: {} sales", sales != null ? sales.size() : "null");
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            Instant endDate = Instant.now();
            Instant startDate = endDate.minus(30, ChronoUnit.DAYS);
            log.info("Calling salesService.getSalesHistory({}, {}, {}, {})", 
                    cooperativeId, startDate, endDate, type);
            sales = salesService.getSalesHistory(cooperativeId, startDate, endDate, type);
            log.info("getSalesHistory returned: {} sales", sales != null ? sales.size() : "null");
        } else {
            // Get both active and recent history
            log.info("Fetching BOTH active and history");
            List<SaleDTO> active = salesService.getActiveSales(cooperativeId, type);
            log.info("Active sales: {}", active != null ? active.size() : "null");
            
            Instant endDate = Instant.now();
            Instant startDate = endDate.minus(30, ChronoUnit.DAYS);
            List<SaleDTO> history = salesService.getSalesHistory(cooperativeId, startDate, endDate, type);
            log.info("History sales: {}", history != null ? history.size() : "null");
            
            if (active != null) {
                sales.addAll(active);
            }
            if (history != null) {
                sales.addAll(history);
            }
        }
        
        // Ensure sales is never null
        if (sales == null) {
            sales = new ArrayList<>();
        }
        
        log.info("FINAL: Returning {} sales to template", sales.size());
        
        // Add data to model
        model.addAttribute("sales", sales);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("userName", "User #" + cooperativeId);
        model.addAttribute("isAdmin", authenticatedUser.isAdmin());
        
        return "sales/list";
        
    } catch (Exception e) {
        log.error("ERROR in salesList controller", e);
        model.addAttribute("errorMessage", "Error loading sales: " + e.getMessage());
        model.addAttribute("sales", new ArrayList<>());
        return "sales/list";
    }
}
    
    @GetMapping("/create")
    public String createSaleForm(Model model) {
        try {
            permissionHelper.requireManagerOrAdmin();
            
            model.addAttribute("userName", "User #" + authenticatedUser.getCooperativeId());
            return "sales/create";
            
        } catch (Exception e) {
            log.error("Error loading create sale form", e);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}