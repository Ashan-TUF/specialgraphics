package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.QuizItems;
import uk.specialgraphics.api.entity.Quize;

import java.util.List;

public interface QuizeItemRepository extends JpaRepository<QuizItems, Integer> {
    List<QuizItems> getAllByQuize(Quize quize);
    QuizItems getQuizItemsByCode(String code);
}
