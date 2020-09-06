package com.minetec.backend.repository;

import com.minetec.backend.entity.User;
import com.minetec.backend.repository.projection.UserListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {

    @Query("select u from User u where u.isActive = true ")
    Page<UserListItemProjection> list(Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query("select CONCAT(u.firstName, ' ', u.lastName) as fullName " +
        " from User u where u.isActive = true and u.id = :id ")
    String findFullName(final Long id);
}
