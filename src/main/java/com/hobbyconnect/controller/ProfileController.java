package com.hobbyconnect.controller;

import com.hobbyconnect.model.Community;
import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;
import com.hobbyconnect.service.CommunityService;
import com.hobbyconnect.service.UserService;
import com.hobbyconnect.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CommunityService communityService;

    @Autowired
    public ProfileController(UserService userService, CommunityService communityService) {
        this.userService = userService;
        this.communityService = communityService;
    }

    @GetMapping
    public String viewMyProfile(HttpSession session, Model model) {
        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findByIdWithHobbies(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        List<Community> joinedCommunities = communityService.getCommunitiesForUser(userId);

        model.addAttribute("currentUser", user);
        model.addAttribute("profileUser", user);
        model.addAttribute("joinedCommunities", joinedCommunities);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isLoggedIn", true);

        return "profile";
    }

    @GetMapping("/edit")
    public String showEditProfilePage(HttpSession session, Model model) {
        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.findByIdWithHobbies(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        model.addAttribute("currentUser", user);
        model.addAttribute("isLoggedIn", true);

        return "profile-edit";
    }

    @PostMapping("/edit")
    public String processEditProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "avatar", required = false) String avatar,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full name cannot be blank.");
            return "redirect:/profile/edit";
        }

        User updated = userService.updateProfile(userId, fullName, bio, location, avatar);
        SessionUtils.updateSessionUser(session, updated);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @GetMapping("/{username}")
    public String viewPublicProfile(
            @PathVariable("username") String username,
            HttpSession session,
            Model model) {

        Long currentUserId = SessionUtils.getLoggedInUserId(session);
        User currentUser = null;
        if (currentUserId != null) {
            currentUser = userService.findByIdWithHobbies(currentUserId).orElse(null);
            model.addAttribute("currentUser", currentUser);
        }

        Optional<User> targetUserOpt = userService.findByUsername(username);
        if (targetUserOpt.isEmpty()) {
            return "redirect:/friends";
        }

        User targetUser = targetUserOpt.get();
        boolean isOwnProfile = currentUser != null && currentUser.getId().equals(targetUser.getId());

        List<Hobby> commonHobbies = new ArrayList<>();
        int matchPercentage = 0;
        if (currentUser != null && !isOwnProfile) {
            for (Hobby h : targetUser.getHobbies()) {
                if (currentUser.getHobbies().contains(h)) {
                    commonHobbies.add(h);
                }
            }
            if (!currentUser.getHobbies().isEmpty()) {
                matchPercentage = Math.min(100, Math.round(((float) commonHobbies.size() / currentUser.getHobbies().size()) * 100));
            }
        }

        List<Community> targetCommunities = communityService.getCommunitiesForUser(targetUser.getId());

        model.addAttribute("profileUser", targetUser);
        model.addAttribute("joinedCommunities", targetCommunities);
        model.addAttribute("isOwnProfile", isOwnProfile);
        model.addAttribute("commonHobbies", commonHobbies);
        model.addAttribute("matchPercentage", matchPercentage);
        model.addAttribute("isLoggedIn", currentUserId != null);

        return "profile";
    }
}
