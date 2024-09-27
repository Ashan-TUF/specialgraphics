package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Answers;
import uk.specialgraphics.api.entity.QuizItems;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answers, Integer> {
    List<Answers> getAllByQuizItems(QuizItems quizItems);

}
