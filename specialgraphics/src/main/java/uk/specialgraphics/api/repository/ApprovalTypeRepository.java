package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.ApprovalType;

public interface ApprovalTypeRepository extends JpaRepository<ApprovalType, Integer> {
    ApprovalType getApprovalTypeById(int i);
}
