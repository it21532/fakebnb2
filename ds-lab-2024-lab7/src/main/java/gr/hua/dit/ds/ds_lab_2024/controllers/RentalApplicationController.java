package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.ApplicationStatus;
import gr.hua.dit.ds.ds_lab_2024.entities.RentalApplication;
import gr.hua.dit.ds.ds_lab_2024.entities.Tenant;
import gr.hua.dit.ds.ds_lab_2024.entities.property;
import gr.hua.dit.ds.ds_lab_2024.service.RentalApplicationService;
import gr.hua.dit.ds.ds_lab_2024.service.TenantService;
import gr.hua.dit.ds.ds_lab_2024.service.propertyService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@Secured("ROLE_TENANT")
@RequestMapping("/rental")
public class RentalApplicationController {

    private final RentalApplicationService rentalApplicationService;
    private final TenantService tenantService;
    private final propertyService propertyService;

    public RentalApplicationController(RentalApplicationService rentalApplicationService,
                                       TenantService tenantService,
                                       propertyService propertyService) {
        this.rentalApplicationService = rentalApplicationService;
        this.tenantService = tenantService;
        this.propertyService = propertyService;
    }

    @GetMapping("/apply/{propertyTitle}")
    @Transactional
    public String showApplicationForm(@PathVariable("propertyTitle") String propertyTitle, Model model, Principal principal) {
        property property = propertyService.getproperty(propertyTitle);
        model.addAttribute("property", property);
        RentalApplication application = new RentalApplication();
        model.addAttribute("application", application);
        return "rental/apply";
    }

    @PostMapping("/apply/{propertyTitle}")
    public String submitApplication(@PathVariable("propertyTitle") String propertyTitle,
                                    @ModelAttribute("application") RentalApplication application,
                                    Principal principal,
                                    Model model) {
        String tenantUsername = principal.getName();
        Tenant tenant = tenantService.getTenantByUsername(tenantUsername);
        property property = propertyService.getproperty(propertyTitle);
        rentalApplicationService.submitApplication(tenant, property);
        model.addAttribute("successMessage", "Rental application submitted successfully!");
        return "redirect:/property";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/pending")
    public String viewPendingApplications(Model model) {
        List<RentalApplication> pendingApplications = rentalApplicationService.getApplicationsByStatus(ApplicationStatus.SUBMITTED);
        model.addAttribute("pendingApplications", pendingApplications);
        return "admin/pendingRentalApplications";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/approve")
    public String approveApplication(@PathVariable int id) {
        rentalApplicationService.updateApplicationStatus(id, ApplicationStatus.APPROVED);
        return "redirect:/rental/pending";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/reject")
    public String rejectApplication(@PathVariable int id) {
        rentalApplicationService.updateApplicationStatus(id, ApplicationStatus.REJECTED);
        return "redirect:/rental/pending";
    }
}