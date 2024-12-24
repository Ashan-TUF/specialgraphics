package uk.specialgraphics.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_zip_files")
public class UserZipFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "url", columnDefinition = "LONGTEXT")
    private String url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_item_zip_file_id")
    private CurriculumItemZipFile curriculumItemZipFile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_has_course_id")
    private StudentHasCourse studentHasCourse;

    @Column(name = "marks")
    private double marks;

    @Column(name = "upload_date")
    private Date uploadDate;

    @Column(name = "status")
    private boolean status;


}