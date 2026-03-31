package dk.aau.network_management_system.frontend;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

/**
 * Web Controller for Sales pages.
 * Serves HTML pages with Thymeleaf templates.
 */
@Controller
@RequestMapping("/web/sales")
public class SalesWebController {
    
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
        
        // Get user's cooperative
        Long cooperativeId = authenticatedUser.getCooperativeId();
        
        // Default values
        if (type == null) type = "ALL";
        if (status == null) status = "ALL";
        
        // Fetch sales based on status
        List<SaleDTO> sales;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            sales = salesService.getActiveSales(cooperativeId, type);
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            Instant endDate = Instant.now();
            Instant startDate = endDate.minus(30, ChronoUnit.DAYS);
            sales = salesService.getSalesHistory(cooperativeId, startDate, endDate, type);
        } else {
            // Get both active and recent history
            List<SaleDTO> active = salesService.getActiveSales(cooperativeId, type);
            Instant endDate = Instant.now();
            Instant startDate = endDate.minus(30, ChronoUnit.DAYS);
            List<SaleDTO> history = salesService.getSalesHistory(cooperativeId, startDate, endDate, type);
            active.addAll(history);
            sales = active;
        }
        
        // Add data to model
        model.addAttribute("sales", sales);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("userName", authenticatedUser.getWorkerId());
        model.addAttribute("isAdmin", authenticatedUser.isAdmin());
        
        return "sales/list";
    }
    
    /**
     * Display create sale form
     */
    @GetMapping("/create")
    public String createSaleForm(Model model) {
        permissionHelper.requireManagerOrAdmin();
        
        model.addAttribute("userName", authenticatedUser.getWorkerId());
        return "sales/create";
    }
}