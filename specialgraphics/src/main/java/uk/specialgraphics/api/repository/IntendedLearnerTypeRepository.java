package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.IntendedLearnerType;

public interface IntendedLearnerTypeRepository extends JpaRepository<IntendedLearnerType, Integer> {
    IntendedLearnerType getIntendedLearnerTypeById(int i);
}
