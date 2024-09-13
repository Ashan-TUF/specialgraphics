package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course getCourseByCourseTitle(String courseTitle);

    Course getCourseByCode(String code);

    Course findByCode(String courseCode);
    Course findById(int id);
}
