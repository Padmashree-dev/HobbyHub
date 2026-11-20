package com.hobbyconnect.controller;

import com.hobbyconnect.model.Community;
import com.hobbyconnect.model.CommunityPost;
import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;
import com.hobbyconnect.service.CommunityService;
import com.hobbyconnect.service.HobbyService;
import com.hobbyconnect.service.UserService;
import com.hobbyconnect.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/communities")
public class CommunityController {

    private final CommunityService communityService;
    private final UserService userService;
    private final HobbyService hobbyService;

    @Autowired
    public CommunityController(CommunityService communityService, UserService userService, HobbyService hobbyService) {
        this.communityService = communityService;
        this.userService = userService;
        this.hobbyService = hobbyService;
    }

    @GetMapping
    public String listCommunities(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            HttpSession session,
            Model model) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId != null) {
            userService.findByIdWithHobbies(userId).ifPresent(u -> model.addAttribute("currentUser", u));
        }

        List<Community> communities = communityService.searchCommunities(keyword, category);
        List<String> categories = hobbyService.getAllCategories();
        List<Hobby> allHobbies = hobbyService.getAllHobbies();

        Set<Long> joinedCommunityIds = Set.of();
        if (userId != null) {
            joinedCommunityIds = communityService.getCommunitiesForUser(userId).stream()
                    .map(Community::getId)
                    .collect(Collectors.toSet());
        }

        model.addAttribute("communities", communities);
        model.addAttribute("categories", categories);
        model.addAttribute("allHobbies", allHobbies);
        model.addAttribute("joinedCommunityIds", joinedCommunityIds);
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("isLoggedIn", userId != null);

        return "communities";
    }

    @PostMapping("/create")
    public String createCommunity(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam(value = "icon", required = false, defaultValue = "bi-people") String icon,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Community name is required.");
            return "redirect:/communities";
        }

        Community created = communityService.createCommunity(name, description, category, icon, userId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Community '" + created.getName() + "' created successfully! Welcome to your new hobby hub.");

        return "redirect:/communities/" + created.getId();
    }

    @GetMapping("/{id}")
    public String viewCommunity(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId != null) {
            userService.findByIdWithHobbies(userId).ifPresent(u -> model.addAttribute("currentUser", u));
        }

        Optional<Community> commOpt = communityService.getCommunityById(id);
        if (commOpt.isEmpty()) {
            return "redirect:/communities";
        }

        Community community = commOpt.get();
        List<CommunityPost> posts = communityService.getPostsForCommunity(id);
        boolean isMember = userId != null && communityService.isMember(id, userId);

        model.addAttribute("community", community);
        model.addAttribute("posts", posts);
        model.addAttribute("isMember", isMember);
        model.addAttribute("isLoggedIn", userId != null);

        return "community-detail";
    }

    @PostMapping("/{id}/join")
    public String joinCommunity(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        communityService.joinCommunity(id, userId);
        redirectAttributes.addFlashAttribute("successMessage", "You have joined the community!");
        return "redirect:/communities/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leaveCommunity(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        communityService.leaveCommunity(id, userId);
        redirectAttributes.addFlashAttribute("infoMessage", "You have left the community.");
        return "redirect:/communities/" + id;
    }

    @PostMapping("/{id}/post")
    public String addPost(
            @PathVariable("id") Long id,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Post message cannot be empty.");
            return "redirect:/communities/" + id;
        }

        communityService.addPost(id, userId, content);
        redirectAttributes.addFlashAttribute("successMessage", "Your post has been published!");
        return "redirect:/communities/" + id;
    }
}
