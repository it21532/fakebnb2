package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.property;
import gr.hua.dit.ds.ds_lab_2024.entities.PropertyStatus;
import gr.hua.dit.ds.ds_lab_2024.entities.Tenant;
import gr.hua.dit.ds.ds_lab_2024.entities.Owner;
import gr.hua.dit.ds.ds_lab_2024.service.propertyService;
import gr.hua.dit.ds.ds_lab_2024.service.TenantService;
import gr.hua.dit.ds.ds_lab_2024.service.OwnerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("property")
public class OwnerPropertyController {

    private final TenantService tenantService;
    private final propertyService propertyService;
    private final OwnerService ownerService;

    public OwnerPropertyController(propertyService propertyService, OwnerService ownerService, TenantService tenantService) {
        this.propertyService = propertyService;
        this.ownerService = ownerService;
        this.tenantService = tenantService;
    }

    @GetMapping("/submit")
    public String showSubmitForm(Model model) {
        property prop = new property();
        model.addAttribute("property", prop);
        return "property/submitProperty";
    }

    @PostMapping("/submit")
    public String submitProperty(@Valid @ModelAttribute("property") property prop,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "property/submitProperty";
        }
        if (propertyService.existsByTitle(prop.getTitle())) {
            model.addAttribute("errorMessage", "A property with this title already exists. Please choose another title.");
            return "property/submitProperty";
        }
        Owner currentOwner = ownerService.getOwnerByUsername(principal.getName());
        prop.setOwner(currentOwner);
        prop.setStatus(PropertyStatus.PENDING);
        propertyService.saveproperty(prop);
        return "redirect:/owner/dashboard/" + currentOwner.getUsername();
    }
    @PostMapping("/new")
    public String saveProperty(@Valid @ModelAttribute("property") property property,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("error");
            return "property/property";
        } else {
            propertyService.saveproperty(property);
            model.addAttribute("properties", propertyService.getproperties());
            model.addAttribute("successMessage", "Property added successfully!");
            return "property/properties";
        }
    }

    @RequestMapping()
    @GetMapping("")
    public String showApprovedProperties(Model model) {
        model.addAttribute("properties", propertyService.getApprovedProperties());
        return "property/properties";
    }

    @GetMapping("/{title}")
    public String showproperty(@PathVariable String title, Model model) {
        property property = propertyService.getproperty(title);
        model.addAttribute("properties", property);
        return "property/details";
    }

    @GetMapping("/new")
    public String addproperty(Model model) {
        property property = new property();
        model.addAttribute("property", property);
        return "property/property";
    }

    @GetMapping("/assign/{title}")
    public String showAssignownerToproperty(@PathVariable String title, Model model) {
        property property = propertyService.getproperty(title);
        model.addAttribute("property", property);
        return "property/assignowner";
    }

    // Unassign the owner from a property.
    @GetMapping("/unassign/{title}")
    public String unassignownerToproperty(@PathVariable String title, Model model) {
        propertyService.unassignownerFromproperty(title);
        model.addAttribute("properties", propertyService.getproperties());
        return "property/properties";
    }

    @GetMapping("/Tenantassign/{title}")
    public String showAssignTenantToproperty(@PathVariable String title, Model model) {
        property property = propertyService.getproperty(title);
        model.addAttribute("property", property);
        return "property/assignTenant";
    }
.
    @PostMapping("/assign/{title}")
    public String assignownerToproperty(@PathVariable String title,
                                        @RequestParam(value = "owner", required = true) String ownerUsername,
                                        Model model) {
        System.out.println("Assigning owner: " + ownerUsername);
        Owner owner = ownerService.getOwnerByUsername(ownerUsername);
        property property = propertyService.getproperty(title);
        System.out.println("Property: " + property);
        propertyService.assignownerToproperty(title, owner);
        model.addAttribute("properties", propertyService.getproperties());
        model.addAttribute("successMessage", "Form submitted successfully!");
        return "property/properties";
    }

    @PostMapping("/Tenantassign/{title}")
    public String assignTenantToproperty(@PathVariable String title,
                                         @RequestParam(value = "tenant", required = true) String tenantUsername,
                                         Model model) {
        System.out.println("Assigning tenant: " + tenantUsername);
        Tenant tenant = tenantService.getTenantByUsername(tenantUsername);
        property property = propertyService.getproperty(title);
        System.out.println("Property: " + property);
        propertyService.assignTenantToproperty(title, tenant);
        model.addAttribute("properties", propertyService.getproperties());
        return "property/properties";
    }
    @PostMapping("/delete/{title}")
    public String deleteProperty(@PathVariable String title, Principal principal, Model model) {
        Owner currentOwner = ownerService.getOwnerByUsername(principal.getName());
        property prop = propertyService.getproperty(title);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            propertyService.deleteProperty(title);
            model.addAttribute("successMessage", "Property deleted successfully!");
            return "redirect:/admin/owners";
        } else {
            if (prop != null && prop.getOwner() != null && prop.getOwner().getUsername().equals(currentOwner.getUsername())) {
                propertyService.deleteProperty(title);
                model.addAttribute("successMessage", "Property deleted successfully!");
            } else {
                model.addAttribute("errorMessage", "You are not authorized to delete this property.");
            }
            return "redirect:/user/" + currentOwner.getUsername();
        }
    }
}