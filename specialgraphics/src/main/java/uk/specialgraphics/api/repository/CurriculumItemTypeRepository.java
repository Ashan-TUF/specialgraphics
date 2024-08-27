package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CurriculumItemType;

public interface CurriculumItemTypeRepository extends JpaRepository<CurriculumItemType, Integer> {
}
