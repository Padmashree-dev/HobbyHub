package com.hobbyconnect.controller;

import com.hobbyconnect.service.CommunityService;
import com.hobbyconnect.service.HobbyService;
import com.hobbyconnect.service.UserService;
import com.hobbyconnect.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HobbyService hobbyService;
    private final UserService userService;
    private final CommunityService communityService;

    @Autowired
    public HomeController(HobbyService hobbyService, UserService userService, CommunityService communityService) {
        this.hobbyService = hobbyService;
        this.userService = userService;
        this.communityService = communityService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Long userId = SessionUtils.getLoggedInUserId(session);
        if (userId != null) {
            userService.findByIdWithHobbies(userId).ifPresent(user -> model.addAttribute("currentUser", user));
        }

        model.addAttribute("isLoggedIn", userId != null);
        model.addAttribute("hobbies", hobbyService.getAllHobbies());
        model.addAttribute("totalUsers", userService.countTotalUsers());
        model.addAttribute("totalCommunities", communityService.countTotalCommunities());
        model.addAttribute("totalHobbies", hobbyService.getAllHobbies().size());
        model.addAttribute("popularCommunities", communityService.getAllCommunities().stream().limit(3).toList());

        return "index";
    }
}
