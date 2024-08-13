package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.CourseLandingPage;

public interface CourseLandingPageRepository  extends JpaRepository<CourseLandingPage, Integer> {
    CourseLandingPage findByCourseId(int id);

    CourseLandingPage getCourseLandingPageByCourse(Course course);
}
