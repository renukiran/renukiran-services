package com.renukiran.repository;

import com.renukiran.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
	Optional<Trainer> findByUserId(Long userId);

	Optional<Trainer> findFirstByNameIgnoreCase(String name);
}
