package com.hobbyconnect.controller;

import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;
import com.hobbyconnect.service.HobbyService;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HobbyController {

    private final HobbyService hobbyService;
    private final UserService userService;

    @Autowired
    public HobbyController(HobbyService hobbyService, UserService userService) {
        this.hobbyService = hobbyService;
        this.userService = userService;
    }

    @GetMapping("/hobbies")
    public String showHobbySelection(HttpSession session, Model model) {
        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findByIdWithHobbies(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Set<Long> userHobbyIds = user.getHobbies().stream().map(Hobby::getId).collect(Collectors.toSet());

        model.addAttribute("currentUser", user);
        model.addAttribute("allHobbies", hobbyService.getAllHobbies());
        model.addAttribute("userHobbyIds", userHobbyIds);
        model.addAttribute("isLoggedIn", true);

        return "hobbies";
    }

    @PostMapping("/hobbies")
    public String saveSelectedHobbies(
            @RequestParam(value = "hobbyIds", required = false) List<Long> hobbyIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        if (hobbyIds == null || hobbyIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one hobby to continue!");
            return "redirect:/hobbies";
        }

        userService.updateUserHobbies(userId, hobbyIds);
        redirectAttributes.addFlashAttribute("successMessage",
                "Your hobbies have been updated successfully! Check out your matched hobbyist friends below.");

        return "redirect:/friends";
    }
}
