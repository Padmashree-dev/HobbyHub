package com.hobbyconnect.controller;

import com.hobbyconnect.model.Message;
import com.hobbyconnect.model.User;
import com.hobbyconnect.service.MessageService;
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

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public String showMessages(
            @RequestParam(value = "to", required = false) Long toUserId,
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
        List<User> chatPartners = messageService.getChatPartners(currentUserId);

        User activePartner = null;
        List<Message> conversation = List.of();

        if (toUserId != null && !toUserId.equals(currentUserId)) {
            Optional<User> targetOpt = userService.findById(toUserId);
            if (targetOpt.isPresent()) {
                activePartner = targetOpt.get();
                // If partner is not already in partner list, add them to front for display
                if (!chatPartners.contains(activePartner)) {
                    chatPartners.add(0, activePartner);
                }
                conversation = messageService.getConversation(currentUserId, toUserId);
                messageService.markMessagesAsRead(currentUserId, toUserId);
            }
        } else if (!chatPartners.isEmpty()) {
            activePartner = chatPartners.get(0);
            conversation = messageService.getConversation(currentUserId, activePartner.getId());
            messageService.markMessagesAsRead(currentUserId, activePartner.getId());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chatPartners", chatPartners);
        model.addAttribute("activePartner", activePartner);
        model.addAttribute("conversation", conversation);
        model.addAttribute("isLoggedIn", true);

        return "messages";
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam("toUserId") Long toUserId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long currentUserId = SessionUtils.getLoggedInUserId(session);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        try {
            messageService.sendMessage(currentUserId, toUserId, content);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/messages?to=" + toUserId;
    }
}
