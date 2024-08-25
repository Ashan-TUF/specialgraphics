package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course getCourseByCourseTitle(String courseTitle);

    Course getCourseByCode(String code);

    List<Course> getAllByIsActiveEquals(byte status);


    Course findByCode(String courseCode);
}
