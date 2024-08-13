package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.CourseComplete;

public interface CourseCompleteRepository extends JpaRepository<CourseComplete, Integer> {
    CourseComplete getCourseCompleteByCourse(Course course);
}
