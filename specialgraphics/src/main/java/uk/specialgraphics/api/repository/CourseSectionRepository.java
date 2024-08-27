package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.CourseSection;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {
    CourseSection getCourseSectionByCourseAndSectionName(Course course, String sectionName);

    CourseSection getCourseSectionBySectionCode(String courseSectionCode);
}
