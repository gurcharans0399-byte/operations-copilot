package com.opcopilot.queryservice.repository;

import com.opcopilot.queryservice.model.ProposedAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProposedActionRepository extends JpaRepository<ProposedAction, UUID> {
}
