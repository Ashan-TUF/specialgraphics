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
    @Column(name = "course_title")
    private String courseTitle;
    @Column(name = "img")
    private String img;
    @Column(name = "promotional_video")
    private String promotionalVideo;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "buy_count")
    private Integer buyCount;
    @Column(name = "description")
    private String description;
    @Column(name = "points")
    private String points;
    @Column(name = "price")
    private Double price;
    @Column(name = "is_active")
    private byte isActive;

}