package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CourseKeyword;

public interface CourseKeywordRepository extends JpaRepository<CourseKeyword, Integer> {
}
