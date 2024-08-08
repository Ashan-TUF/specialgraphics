package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.CourseCategory;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer> {
    CourseCategory getCourseCategoryById(int categoryId);
}
