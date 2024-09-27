package uk.specialgraphics.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_answer")
public class UserAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quize_item_id", nullable = false)
    private QuizItems quizItem;

    @ManyToOne
    @JoinColumn(name = "user_answer_id", nullable = false)
    private Answers userAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_quiz_id")
    private UserQuiz userQuiz;

}
