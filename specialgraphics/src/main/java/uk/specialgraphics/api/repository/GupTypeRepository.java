package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.GupType;

public interface GupTypeRepository extends JpaRepository<GupType, Integer> {

    GupType getGupTypeById(Integer gupType);
}