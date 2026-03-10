package com.renukiran.repository;

import com.renukiran.entity.BulkUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<BulkUsers, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
