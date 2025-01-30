package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.CourseSection;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {
    CourseSection getCourseSectionByCourseAndSectionName(Course course, String sectionName);

    CourseSection getCourseSectionBySectionCode(String courseSectionCode);

    List<CourseSection> getCourseSectionByCourse(Course course);

    CourseSection getCourseSectionBySectionName(String sectionName);
}
