package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Language getLanguageById(int language);
}
