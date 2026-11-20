package com.hobbyconnect.service;

import com.hobbyconnect.model.Community;
import com.hobbyconnect.model.CommunityPost;
import com.hobbyconnect.model.User;
import com.hobbyconnect.repository.CommunityPostRepository;
import com.hobbyconnect.repository.CommunityRepository;
import com.hobbyconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository,
                            CommunityPostRepository communityPostRepository,
                            UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.communityPostRepository = communityPostRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Community> getAllCommunities() {
        return communityRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Optional<Community> getCommunityById(Long id) {
        return communityRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<Community> getCommunitiesForUser(Long userId) {
        return communityRepository.findCommunitiesByMemberId(userId);
    }

    public Community createCommunity(String name, String description, String category, String icon, Long creatorUserId) {
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found with id: " + creatorUserId));

        if (icon == null || icon.isBlank()) {
            icon = "bi-people";
        }

        Community community = new Community(name.trim(), description != null ? description.trim() : "",
                category != null ? category.trim() : "General", icon.trim(), creator);

        Community saved = communityRepository.save(community);
        // Creator automatically joins their created community
        joinCommunity(saved.getId(), creatorUserId);
        return saved;
    }

    public void joinCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findByIdWithDetails(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + communityId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        community.getMembers().add(user);
        user.getJoinedCommunities().add(community);
        communityRepository.save(community);
        userRepository.save(user);
    }

    public void leaveCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findByIdWithDetails(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + communityId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        community.getMembers().remove(user);
        user.getJoinedCommunities().remove(community);
        communityRepository.save(community);
        userRepository.save(user);
    }

    public CommunityPost addPost(Long communityId, Long authorUserId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty.");
        }

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + communityId));
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + authorUserId));

        CommunityPost post = new CommunityPost(content.trim(), community, author);
        return communityPostRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> getPostsForCommunity(Long communityId) {
        return communityPostRepository.findByCommunityIdWithAuthorOrderByCreatedAtDesc(communityId);
    }

    @Transactional(readOnly = true)
    public boolean isMember(Long communityId, Long userId) {
        if (communityId == null || userId == null) return false;
        Optional<Community> commOpt = communityRepository.findByIdWithDetails(communityId);
        if (commOpt.isEmpty()) return false;
        return commOpt.get().getMembers().stream().anyMatch(m -> m.getId().equals(userId));
    }

    @Transactional(readOnly = true)
    public List<Community> searchCommunities(String keyword, String category) {
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            return communityRepository.findByCategoryIgnoreCase(category.trim());
        }
        if (keyword != null && !keyword.isBlank()) {
            return communityRepository.findByNameContainingIgnoreCase(keyword.trim());
        }
        return getAllCommunities();
    }

    @Transactional(readOnly = true)
    public long countTotalCommunities() {
        return communityRepository.count();
    }
}
