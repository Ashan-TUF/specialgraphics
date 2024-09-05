package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.StudentHasCourse;

public interface StudentHasCourseRepository extends JpaRepository<StudentHasCourse, Integer> {

    StudentHasCourse getStudentHasCourseByCourseAndGeneralUserProfile(Course course, GeneralUserProfile profile);
}
