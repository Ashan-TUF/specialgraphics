package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Answers;
import uk.specialgraphics.api.entity.QuizItems;
import uk.specialgraphics.api.entity.SectionCurriculumItem;
import uk.specialgraphics.api.entity.UserQuiz;

import java.util.List;

public interface UserQuizeRepository extends JpaRepository<UserQuiz, Integer> {

    UserQuiz getUserQuizByQuizSectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
