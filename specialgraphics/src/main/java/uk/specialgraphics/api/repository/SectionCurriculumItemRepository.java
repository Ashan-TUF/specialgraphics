package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CourseSection;
import uk.specialgraphics.api.entity.SectionCurriculumItem;

public interface SectionCurriculumItemRepository extends JpaRepository<SectionCurriculumItem, Integer> {
    SectionCurriculumItem getSectionCurriculumItemByCourseSectionAndTitle(CourseSection courseSection, String title);
}
