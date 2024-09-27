package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.QuizItems;
import uk.specialgraphics.api.entity.UserAnswers;
import uk.specialgraphics.api.entity.UserQuiz;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswers, Integer> {

   UserAnswers getUserAnswersByQuizItem(QuizItems item);
}
