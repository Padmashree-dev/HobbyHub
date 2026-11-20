package com.hobbyconnect.service;

import com.hobbyconnect.dto.UserMatchDto;
import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;
import com.hobbyconnect.repository.HobbyRepository;
import com.hobbyconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HobbyRepository hobbyRepository;

    @Autowired
    public UserService(UserRepository userRepository, HobbyRepository hobbyRepository) {
        this.userRepository = userRepository;
        this.hobbyRepository = hobbyRepository;
    }

    public User register(String fullName, String username, String email, String password, String bio, String location, String avatar) {
        String cleanUsername = username != null ? username.trim().toLowerCase() : "";
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";

        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' is already taken.");
        }
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email '" + cleanEmail + "' is already registered.");
        }

        if (avatar == null || avatar.isBlank()) {
            avatar = "avatar-1";
        }

        User user = new User(fullName, cleanUsername, cleanEmail, password, bio, location, avatar);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return Optional.empty();
        }
        String cleanInput = usernameOrEmail.trim().toLowerCase();

        Optional<User> userOpt = userRepository.findByUsername(cleanInput);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(cleanInput);
        }

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByIdWithHobbies(Long id) {
        return userRepository.findByIdWithHobbies(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByIdFullDetails(Long id) {
        return userRepository.findByIdFullDetails(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameWithHobbies(username != null ? username.trim().toLowerCase() : "");
    }

    public User updateProfile(Long userId, String fullName, String bio, String location, String avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        user.setBio(bio != null ? bio.trim() : "");
        user.setLocation(location != null ? location.trim() : "");
        if (avatar != null && !avatar.isBlank()) {
            user.setAvatar(avatar.trim());
        }

        return userRepository.save(user);
    }

    public User updateUserHobbies(Long userId, List<Long> hobbyIds) {
        User user = userRepository.findByIdWithHobbies(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Set<Hobby> selectedHobbies = new HashSet<>();
        if (hobbyIds != null && !hobbyIds.isEmpty()) {
            selectedHobbies.addAll(hobbyRepository.findByIdIn(hobbyIds));
        }

        user.setHobbies(selectedHobbies);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserMatchDto> findMatchesForUser(Long userId, Long filterHobbyId) {
        User currentUser = userRepository.findByIdWithHobbies(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Set<Hobby> myHobbies = currentUser.getHobbies();
        Set<Long> myHobbyIds = myHobbies.stream().map(Hobby::getId).collect(Collectors.toSet());

        List<User> candidateUsers;
        if (filterHobbyId != null && filterHobbyId > 0) {
            candidateUsers = userRepository.findUsersByHobbyId(filterHobbyId, userId);
        } else if (!myHobbyIds.isEmpty()) {
            candidateUsers = userRepository.findUsersWithMatchingHobbies(myHobbyIds, userId);
        } else {
            // If user has not chosen hobbies yet, show other registered users to explore
            candidateUsers = userRepository.findAllWithHobbies().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .collect(Collectors.toList());
        }

        List<UserMatchDto> matches = new ArrayList<>();
        for (User candidate : candidateUsers) {
            List<Hobby> common = new ArrayList<>();
            for (Hobby candidateHobby : candidate.getHobbies()) {
                if (myHobbyIds.contains(candidateHobby.getId())) {
                    common.add(candidateHobby);
                }
            }

            int matchPercent = 0;
            if (!myHobbies.isEmpty()) {
                matchPercent = Math.min(100, Math.round(((float) common.size() / myHobbies.size()) * 100));
            }

            matches.add(new UserMatchDto(candidate, common, matchPercent));
        }

        // Sort by common hobbies count desc, then match percentage desc, then username asc
        matches.sort((a, b) -> {
            int cmp = Integer.compare(b.getCommonCount(), a.getCommonCount());
            if (cmp != 0) return cmp;
            int pctCmp = Integer.compare(b.getMatchPercentage(), a.getMatchPercentage());
            if (pctCmp != 0) return pctCmp;
            return a.getUser().getFullName().compareToIgnoreCase(b.getUser().getFullName());
        });

        return matches;
    }

    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllWithHobbies();
    }
}
