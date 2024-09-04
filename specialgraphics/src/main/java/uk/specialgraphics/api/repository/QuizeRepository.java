package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.Quize;

public interface QuizeRepository extends JpaRepository<Quize,Integer> {
Quize getQuizeBySectionCurriculumItemId(int id);
}
