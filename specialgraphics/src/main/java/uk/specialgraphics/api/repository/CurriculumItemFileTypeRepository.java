package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CurriculumItemFileType;

public interface CurriculumItemFileTypeRepository extends JpaRepository<CurriculumItemFileType, Integer> {
    CurriculumItemFileType getCurriculumItemFileTypeById(int i);
}
