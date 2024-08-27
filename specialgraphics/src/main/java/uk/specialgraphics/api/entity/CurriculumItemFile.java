package uk.specialgraphics.api.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "curriculum_item_files")
public class CurriculumItemFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_item_file_types_id")
    private CurriculumItemFileType curriculumItemFileTypes;
    @Column(name = "url", columnDefinition = "LONGTEXT")
    private String url;
    @Column(name = "title", columnDefinition = "LONGTEXT")
    private String title;
    @Column(name = "video_length")
    private Double videoLength;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_curriculum_item_id")
    private SectionCurriculumItem sectionCurriculumItem;
}