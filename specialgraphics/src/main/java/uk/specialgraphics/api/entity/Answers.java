package uk.specialgraphics.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "answer")
public class Answers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "answer")
    private String answer;

    @ManyToOne
    @JoinColumn(name = "quiz_items_id", nullable = false)
    private QuizItems quizItems;

    @Column(name="isCorrect",nullable = false)
    private boolean isCorrect;

}
