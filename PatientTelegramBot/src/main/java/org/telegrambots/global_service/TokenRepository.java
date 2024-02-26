package org.telegrambots.global_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.global_entity.PermissionToken;

@Repository
public interface TokenRepository extends JpaRepository<PermissionToken, Integer> {
}
