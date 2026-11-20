package com.hobbyconnect.repository;

import com.hobbyconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.hobbies WHERE u.id = :id")
    Optional<User> findByIdWithHobbies(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.hobbies WHERE u.username = :username")
    Optional<User> findByUsernameWithHobbies(@Param("username") String username);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.hobbies LEFT JOIN FETCH u.joinedCommunities WHERE u.id = :id")
    Optional<User> findByIdFullDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.hobbies")
    List<User> findAllWithHobbies();

    @Query("SELECT DISTINCT u FROM User u JOIN u.hobbies h WHERE h.id IN :hobbyIds AND u.id <> :excludeUserId")
    List<User> findUsersWithMatchingHobbies(@Param("hobbyIds") Collection<Long> hobbyIds, @Param("excludeUserId") Long excludeUserId);

    @Query("SELECT DISTINCT u FROM User u JOIN u.hobbies h WHERE h.id = :hobbyId AND u.id <> :excludeUserId")
    List<User> findUsersByHobbyId(@Param("hobbyId") Long hobbyId, @Param("excludeUserId") Long excludeUserId);
}
