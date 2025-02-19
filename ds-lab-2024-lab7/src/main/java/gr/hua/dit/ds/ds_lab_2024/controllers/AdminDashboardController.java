package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.*;
import gr.hua.dit.ds.ds_lab_2024.service.propertyService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import gr.hua.dit.ds.ds_lab_2024.service.TenantService;
import gr.hua.dit.ds.ds_lab_2024.service.OwnerService;
import gr.hua.dit.ds.ds_lab_2024.service.UserService;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
public class AdminDashboardController {

    private final propertyService propertyService;
    private final TenantService TenantService;
    private final OwnerService OwnerService;
    private final UserService UserService;

    public AdminDashboardController(propertyService propertyService,OwnerService OwnerService, TenantService TenantService, UserService userService) {
        this.propertyService = propertyService;
        this.TenantService = TenantService;
        this.OwnerService = OwnerService;
        this.UserService = userService;
    }

    @GetMapping("/dashboard")
    @Transactional
    public String adminDashboard(Model model) {
        List<property> pendingProperties = propertyService.getPropertiesByStatus(PropertyStatus.PENDING);
        model.addAttribute("pendingProperties", pendingProperties);
        return "admin/dashboard";
    }

    @PostMapping("/dashboard/{title}/approve")
    @Transactional
    public String approveProperty(@PathVariable String title) {
        property prop = propertyService.getproperty(title);
        if (prop != null) {
            prop.setStatus(PropertyStatus.APPROVED);
            propertyService.saveproperty(prop);
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/dashboard/{title}/reject")
    @Transactional
    public String rejectProperty(@PathVariable String title) {
        property prop = propertyService.getproperty(title);
        if (prop != null) {
            prop.setStatus(PropertyStatus.REJECTED);
            propertyService.saveproperty(prop);
        }
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/tenants")
    @Transactional
    public String listTenants(Model model) {
        List<Tenant> tenants = TenantService.getAllTenants();
        model.addAttribute("tenants", tenants);
        return "admin/tenants";
    }

    @GetMapping("/owners")
    @Transactional
    public String listOwners(Model model) {
        List<Owner> owners = OwnerService.getAllOwners();
        model.addAttribute("owners", owners);
        return "admin/owners";
    }

    @GetMapping("/admins")
    public String listAdmins(Model model) {
        Iterable<User> allUsers = UserService.getUsers();
        List<User> admins = new ArrayList<>();
        for (User u : allUsers) {
            if ("ROLE_ADMIN".equals(u.getSecurityRole())) {
                admins.add(u);
            }
        }
        model.addAttribute("admins", admins);
        return "admin/admins"; // Template at: templates/admin/admins.html
    }
}