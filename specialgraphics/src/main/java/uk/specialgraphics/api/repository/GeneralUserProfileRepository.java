package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.GeneralUserProfile;

import java.util.List;

public interface GeneralUserProfileRepository extends JpaRepository<GeneralUserProfile, Integer> {

    GeneralUserProfile getGeneralUserProfileByEmail(String username);
}

