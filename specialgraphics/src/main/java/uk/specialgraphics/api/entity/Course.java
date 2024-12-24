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
@Table(name="course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "sub_course_title")
    private String subCourseTitle;
    @Column(name = "sub_course_title_color")
    private String subCourseTitleColorCode;

    @Column(name = "course_title")
    private String courseTitle;

    @Column(name = "course_title_color")
    private String courseTitleColorCode;
    @Column(name = "img")
    private String img;

    @Column(name = "key_highlights",columnDefinition = "TEXT")
    private String keyHighlights;

    @Column(name = "program_outcome",columnDefinition = "TEXT")
    private String programOutcomes;

    @Column(name = "specific_program_outcome",columnDefinition = "TEXT")
    private String specificprogramOutcomes;

    @Column(name = "career",columnDefinition = "TEXT")
    private String careersSection;

    @Column(name = "promotional_video")
    private String promotionalVideo;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "buy_count")
    private Integer buyCount;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;
    @Column(name = "points",columnDefinition = "TEXT")
    private String points;
    @Column(name = "price")
    private Double price;
    @Column(name = "is_active")
    private byte isActive;

}