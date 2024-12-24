package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CurriculumItemFile;
import uk.specialgraphics.api.entity.CurriculumItemZipFile;
import uk.specialgraphics.api.entity.SectionCurriculumItem;

import java.util.List;

public interface CurriculumItemZipFileRepository extends JpaRepository<CurriculumItemZipFile, Integer> {
    List<CurriculumItemZipFile> getCurriculumItemFileBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);

    CurriculumItemZipFile getCurriculumItemZipFileById(int id);
}
