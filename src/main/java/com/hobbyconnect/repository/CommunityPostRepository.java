package com.hobbyconnect.repository;

import com.hobbyconnect.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    @Query("SELECT p FROM CommunityPost p JOIN FETCH p.author WHERE p.community.id = :communityId ORDER BY p.createdAt DESC")
    List<CommunityPost> findByCommunityIdWithAuthorOrderByCreatedAtDesc(@Param("communityId") Long communityId);

    long countByCommunityId(Long communityId);
}
