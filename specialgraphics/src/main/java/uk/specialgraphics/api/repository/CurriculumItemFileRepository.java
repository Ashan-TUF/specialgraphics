package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CurriculumItemFile;

public interface CurriculumItemFileRepository extends JpaRepository<CurriculumItemFile, Integer> {
}
