package com.opcopilot.queryservice.repository;

import com.opcopilot.queryservice.model.ProposedRefundUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProposedRefundUpdateRepository extends JpaRepository<ProposedRefundUpdate, UUID> {
}
