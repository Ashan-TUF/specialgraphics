package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.entity.Coupon;
import uk.specialgraphics.api.entity.Course;

public interface CouponRepository extends JpaRepository<Coupon,Integer> {
Coupon getCouponByUidAndAndCourse(String uid, Course course);
Coupon getCouponByUid(String uid);
}
