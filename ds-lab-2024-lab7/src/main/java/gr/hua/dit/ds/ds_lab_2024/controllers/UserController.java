package gr.hua.dit.ds.ds_lab_2024.controllers;
import gr.hua.dit.ds.ds_lab_2024.entities.Owner;
import gr.hua.dit.ds.ds_lab_2024.entities.Tenant;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.entities.property;
import gr.hua.dit.ds.ds_lab_2024.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import gr.hua.dit.ds.ds_lab_2024.service.propertyService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final propertyService propertyService;

    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder, propertyService propertyService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.propertyService = propertyService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam("accountType") String accountType,
                           Model model) {
        String username = null;
        if ("owner".equalsIgnoreCase(accountType)) {
            Owner owner = new Owner(user.getUsername(), user.getEmail(), user.getPassword());
            owner.setSecurityRole("ROLE_OWNER");
            username = userService.saveUser(owner);
        } else if ("tenant".equalsIgnoreCase(accountType)) {
            Tenant tenant = new Tenant(user.getUsername(), user.getEmail(), user.getPassword());
            tenant.setSecurityRole("ROLE_TENANT");
            username = userService.saveUser(tenant);
        }
            return "redirect:/";
    }

    @GetMapping("/user/{username}")
    public String showUser(@PathVariable String username, Model model) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            if ("ROLE_OWNER".equals(user.getSecurityRole())) {
                List<property> ownerProperties = propertyService.getPropertiesByOwner(username);
                model.addAttribute("ownerProperties", ownerProperties);
            }
            return "auth/user";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/user/edit/{username}")
    public String editUser(@PathVariable String username, Model model) {
        if (!canEditUser(username)) {
            return "redirect:/error";
        }
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "auth/edit-user";
        } else {
            return "redirect:/error";
        }
    }

    @PostMapping("/user/edit/{username}")
    public String updateUser(@PathVariable String username, @ModelAttribute("user") User updatedUser, Model model) {
        if (!canEditUser(username)) {
            return "redirect:/error";
        }
        Optional<User> existingOpt = userService.getUserByUsername(username);
        if (existingOpt.isPresent()) {
            User existingUser = existingOpt.get();
            existingUser.setEmail(updatedUser.getEmail());
            userService.updateUser(existingUser);
            return "redirect:/user/" + username;
        } else {
            return "redirect:/error";
        }
    }


    @PostMapping("/user/delete/{username}")
    public String deleteUser(@PathVariable String username, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("ROLE_OWNER".equals(user.getSecurityRole())) {
                List<property> ownerProps = propertyService.getPropertiesByOwner(username);
                if (ownerProps != null && !ownerProps.isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Make sure the account has no properties before deleting it.");
                    return "redirect:/user/" + username;
                }
            }
            userService.deleteUser(username);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login?logout";
    }
//Βοηθητικο
    private boolean canEditUser(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin || currentUsername.equals(username);
    }
}