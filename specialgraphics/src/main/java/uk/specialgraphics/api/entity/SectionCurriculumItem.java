package uk.specialgraphics.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "section_curriculum_item")
public class SectionCurriculumItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "title", length = 80)
    private String title;
    @Lob
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_section_id")
    private CourseSection courseSection;
    @Column(name = "is_delete")
    private Byte isDelete;
    @Column(name = "arranged_no")
    private Integer arranged_no;
    @Lob
    @Column(name = "article")
    private String article;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_item_type_id")
    private CurriculumItemType curriculumItemType;
}