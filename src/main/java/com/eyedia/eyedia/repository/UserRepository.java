package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(String id);

    boolean existsById(String id);

    User getUserByUsersId(Long userId);

    Boolean existsByNameAndUsersIdNot(String nickname, Long currentUserId);

    Boolean existsByNameAndUsersId(String nickname, Long currentUserId);

    Boolean existsByIdAndUsersIdNot(String id, Long currentUserId);

    Boolean existsByIdAndUsersId(String id, Long currentUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.id = :loginId where u.usersId = :usersId")
    int updateLoginId(@Param("usersId") Long usersId, @Param("loginId") String loginId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.name = :nickname where u.usersId = :usersId")
    int updateNickname(@Param("usersId") Long usersId, @Param("nickname") String nickname);
}
