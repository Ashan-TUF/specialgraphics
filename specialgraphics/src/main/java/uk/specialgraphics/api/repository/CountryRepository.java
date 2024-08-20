package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.Country;

public interface CountryRepository extends JpaRepository<Country,Integer> {

    Country getCountryById(Integer countryId);
}
