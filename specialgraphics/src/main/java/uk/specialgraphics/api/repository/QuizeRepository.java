package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Quiz;
import uk.specialgraphics.api.entity.SectionCurriculumItem;

public interface QuizeRepository extends JpaRepository<Quiz,Integer> {
Quiz getQuizBySectionCurriculumItemId(int id);
Quiz getQuizBySectionCurriculumItem(SectionCurriculumItem item);
}
