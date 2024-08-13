package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CourseLevel;

public interface CourseLevelRepository  extends JpaRepository<CourseLevel, Integer> {
    CourseLevel getCourseLevelById(int level);
}
