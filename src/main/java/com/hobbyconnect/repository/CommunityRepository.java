package com.hobbyconnect.repository;

import com.hobbyconnect.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    List<Community> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT c FROM Community c LEFT JOIN FETCH c.members LEFT JOIN FETCH c.creator WHERE c.id = :id")
    Optional<Community> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Community c LEFT JOIN FETCH c.members LEFT JOIN FETCH c.creator ORDER BY c.createdAt DESC")
    List<Community> findAllWithDetails();

    @Query("SELECT c FROM Community c JOIN c.members m WHERE m.id = :userId")
    List<Community> findCommunitiesByMemberId(@Param("userId") Long userId);

    List<Community> findByCategoryIgnoreCase(String category);

    List<Community> findByNameContainingIgnoreCase(String keyword);
}
