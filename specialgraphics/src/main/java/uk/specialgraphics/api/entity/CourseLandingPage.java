package uk.specialgraphics.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "course_landing_page")
public class CourseLandingPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String subTitle;
    private String description;
    private String promotionalVideoUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id")
    private Language language;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_levels_id")
    private CourseLevel courseLevel;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
}