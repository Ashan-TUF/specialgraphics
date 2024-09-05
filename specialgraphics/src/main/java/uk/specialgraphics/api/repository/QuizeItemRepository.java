package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.QuizItems;
import uk.specialgraphics.api.entity.Quiz;

import java.util.List;

public interface QuizeItemRepository extends JpaRepository<QuizItems, Integer> {
    List<QuizItems> getAllByQuiz(Quiz quiz);
    QuizItems getQuizItemsByCode(String code);
}
