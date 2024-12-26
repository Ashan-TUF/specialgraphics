package uk.specialgraphics.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.specialgraphics.api.entity.Course;
import uk.specialgraphics.api.entity.GeneralUserProfile;
import uk.specialgraphics.api.entity.StudentHasCourse;

import java.util.List;

public interface StudentHasCourseRepository extends JpaRepository<StudentHasCourse, Integer> {

    StudentHasCourse getStudentHasCourseByCourseAndGeneralUserProfile(Course course, GeneralUserProfile profile);
List<StudentHasCourse>  getAllByGeneralUserProfile(GeneralUserProfile profile);

    @Query(value = "SELECT * FROM student_has_course", nativeQuery = true)
    Page<StudentHasCourse> getAllStudentHasCourses(Pageable pageable);

    @Query(value = "SELECT * FROM student_has_course shc WHERE shc.admin_status='0' AND shc.is_complete='1'", nativeQuery = true)
    List<StudentHasCourse> getAllAdminNotMarkedCompletedCourses();
}
