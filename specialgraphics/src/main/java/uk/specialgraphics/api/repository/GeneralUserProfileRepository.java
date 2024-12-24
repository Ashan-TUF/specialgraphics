package uk.specialgraphics.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.specialgraphics.api.entity.GeneralUserProfile;

import java.util.List;

public interface GeneralUserProfileRepository extends JpaRepository<GeneralUserProfile, Integer> {

    GeneralUserProfile getGeneralUserProfileByEmail(String username);

//    @Query(value = "SELECT * FROM general_user_profile gup WHERE gup.gup_type_id='2'", nativeQuery = true)
//    List<GeneralUserProfile> getAllStudentsDetails();

    @Query(value = "SELECT * FROM general_user_profile gup WHERE gup.gup_type_id='2'", nativeQuery = true)
    Page<GeneralUserProfile> getAllStudentsDetails(Pageable pageable);
}

