package com.hobbyconnect.controller;

import com.hobbyconnect.dto.UserMatchDto;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class FriendController {

    private final UserService userService;
    private final HobbyService hobbyService;

    @Autowired
    public FriendController(UserService userService, HobbyService hobbyService) {
        this.userService = userService;
        this.hobbyService = hobbyService;
    }

    @GetMapping("/friends")
    public String findFriends(
            @RequestParam(value = "hobbyId", required = false) Long filterHobbyId,
            HttpSession session,
            Model model) {

        Long currentUserId = SessionUtils.getLoggedInUserId(session);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        Optional<User> currentUserOpt = userService.findByIdWithHobbies(currentUserId);
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User currentUser = currentUserOpt.get();
        List<UserMatchDto> matches = userService.findMatchesForUser(currentUserId, filterHobbyId);
        List<Hobby> allHobbies = hobbyService.getAllHobbies();

        Hobby selectedFilterHobby = null;
        if (filterHobbyId != null && filterHobbyId > 0) {
            selectedFilterHobby = hobbyService.getHobbyById(filterHobbyId).orElse(null);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("matches", matches);
        model.addAttribute("allHobbies", allHobbies);
        model.addAttribute("selectedFilterHobby", selectedFilterHobby);
        model.addAttribute("selectedHobbyId", filterHobbyId);
        model.addAttribute("isLoggedIn", true);

        return "friends";
    }
}
