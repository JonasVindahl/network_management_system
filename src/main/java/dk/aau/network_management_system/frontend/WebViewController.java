package dk.aau.network_management_system.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving HTML pages (not REST API).
 * Returns view names that Thymeleaf will resolve to templates.
 */
@Controller
public class WebViewController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/frontend";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/frontend")
    public String frontend() {
        return "frontend";
    }

    @GetMapping("/collective-sale")
    public String collectiveSale() {
        return "collective-sale";
    }
}