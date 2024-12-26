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
@Table(name = "student_has_course")
public class StudentHasCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "item_code")
    private String itemCode;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "buy_date")
    private Date buyDate;
    @Column(name = "progress", columnDefinition = "DOUBLE DEFAULT 0")
    private double progress;
    @Column(name = "complete_section")
    private String completeSection;
    @Column(name = "is_complete", columnDefinition = "TINYINT DEFAULT 0")
    private Byte isComplete;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_id")
    private GeneralUserProfile generalUserProfile;
    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "admin_status", columnDefinition = "TINYINT DEFAULT 0")
    private Byte adminStatus;
}
