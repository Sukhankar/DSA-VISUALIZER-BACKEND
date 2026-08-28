package com.codeloom.dsa.analytics.repository;

import com.codeloom.dsa.analytics.entity.UserXpTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserXpTransactionRepository extends JpaRepository<UserXpTransaction, UUID> {
    List<UserXpTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Page<UserXpTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    boolean existsByUserIdAndReasonAndReferenceTypeAndReferenceId(UUID userId, String reason, String referenceType, String referenceId);
}
