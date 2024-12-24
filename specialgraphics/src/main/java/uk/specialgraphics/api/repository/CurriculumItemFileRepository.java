package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CurriculumItemFile;
import uk.specialgraphics.api.entity.SectionCurriculumItem;

import java.util.List;

public interface CurriculumItemFileRepository extends JpaRepository<CurriculumItemFile, Integer> {
    List<CurriculumItemFile> getCurriculumItemFileBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);


}
