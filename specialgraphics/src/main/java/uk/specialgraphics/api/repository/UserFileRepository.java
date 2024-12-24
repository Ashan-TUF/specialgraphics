package uk.specialgraphics.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.specialgraphics.api.entity.*;

import java.util.List;

public interface UserFileRepository extends JpaRepository<UserZipFile, Integer> {
    UserZipFile getUserZipFileByCurriculumItemZipFile(CurriculumItemZipFile curriculumItemZipFile);
    UserZipFile getUserZipFileByCurriculumItemZipFileAndStudentHasCourse(CurriculumItemZipFile curriculumItemZipFile, StudentHasCourse studentHasCourse);

//    @Query(value = "SELECT * FROM user_zip_files usz INNER JOIN student_has_course shc ON shc.id=usz.student_has_course_id INNER JOIN general_user_profile gup ON gup.id=shc.gup_id", nativeQuery = true)
//    List<UserZipFile> getDataToAdmin();


    @Query(value = "SELECT * FROM user_zip_files usz " +
                    "INNER JOIN student_has_course shc ON shc.id = usz.student_has_course_id " +
                    "INNER JOIN general_user_profile gup ON gup.id = shc.gup_id",
            countQuery = "SELECT COUNT(*) FROM user_zip_files usz " +
                    "INNER JOIN student_has_course shc ON shc.id = usz.student_has_course_id " +
                    "INNER JOIN general_user_profile gup ON gup.id = shc.gup_id",
            nativeQuery = true
    )
    Page<UserZipFile> getDataToAdmin(Pageable pageable);

    UserZipFile getUserZipFileById(int id);

}
