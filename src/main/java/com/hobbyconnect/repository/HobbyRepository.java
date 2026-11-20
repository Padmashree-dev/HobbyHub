package com.hobbyconnect.repository;

import com.hobbyconnect.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

    Optional<Hobby> findByName(String name);

    List<Hobby> findAllByOrderByNameAsc();

    List<Hobby> findByCategory(String category);

    List<Hobby> findByIdIn(Collection<Long> ids);

    @Query("SELECT DISTINCT h.category FROM Hobby h WHERE h.category IS NOT NULL ORDER BY h.category ASC")
    List<String> findDistinctCategories();
}
