package com.hobbyconnect.controller;

import com.hobbyconnect.model.User;
import com.hobbyconnect.service.UserService;
import com.hobbyconnect.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session, Model model) {
        if (SessionUtils.isLoggedIn(session)) {
            return "redirect:/friends";
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "avatar", required = false, defaultValue = "avatar-1") String avatar,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            if (fullName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                model.addAttribute("error", "Please fill in all required fields.");
                return "register";
            }
            if (password.length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters long.");
                return "register";
            }

            User user = userService.register(fullName, username, email, password, bio, location, avatar);
            SessionUtils.setLoggedInUser(session, user);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome to HobbyConnect, " + user.getFullName() + "! Please select your favorite hobbies to start connecting.");
            return "redirect:/hobbies";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("bio", bio);
            model.addAttribute("location", location);
            model.addAttribute("selectedAvatar", avatar);
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpSession session,
            Model model) {

        if (SessionUtils.isLoggedIn(session)) {
            return "redirect:/friends";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid username/email or password.");
        }
        if (logout != null) {
            model.addAttribute("info", "You have been successfully logged out.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("usernameOrEmail") String usernameOrEmail,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Optional<User> userOpt = userService.authenticate(usernameOrEmail, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            SessionUtils.setLoggedInUser(session, user);

            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + "!");

            // If user hasn't selected any hobbies yet, guide them to hobby selection first
            Optional<User> userWithHobbies = userService.findByIdWithHobbies(user.getId());
            if (userWithHobbies.isPresent() && userWithHobbies.get().getHobbies().isEmpty()) {
                return "redirect:/hobbies";
            }

            return "redirect:/friends";
        } else {
            model.addAttribute("error", "Invalid username or password. Please try again.");
            model.addAttribute("usernameOrEmail", usernameOrEmail);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String processLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUtils.clearSession(session);
        redirectAttributes.addFlashAttribute("info", "You have been logged out successfully.");
        return "redirect:/login";
    }
}
