package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.CourseCategory;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course getCourseByCourseTitle(String courseTitle);
}
