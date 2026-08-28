package com.codeloom.dsa.analytics.repository;

import com.codeloom.dsa.analytics.entity.XpLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface XpLedgerRepository extends JpaRepository<XpLedger, UUID> {
    List<XpLedger> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<XpLedger> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID userId, OffsetDateTime after);
}
