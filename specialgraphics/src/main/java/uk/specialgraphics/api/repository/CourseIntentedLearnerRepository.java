package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CourseIntentedLearner;

import java.util.List;

public interface CourseIntentedLearnerRepository extends JpaRepository<CourseIntentedLearner, Integer> {
    List<CourseIntentedLearner> getCourseIntentedLearnerByCourseId(int id);
}
