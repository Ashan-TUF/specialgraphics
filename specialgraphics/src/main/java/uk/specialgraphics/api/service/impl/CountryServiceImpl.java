package uk.specialgraphics.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.specialgraphics.api.entity.Country;
import uk.specialgraphics.api.exception.ErrorException;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.repository.CountryRepository;
import uk.specialgraphics.api.service.CountryService;
import uk.specialgraphics.api.utils.VarList;

import java.util.List;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {

    @Autowired
    CountryRepository countryRepository;

    @Override
    public AllCountryResponse getAllCountries() {

        List<Country> countryList = countryRepository.findAll();

        if(countryList.isEmpty()){
            log.warn("password incorrect.");
            throw new ErrorException("Empty Countries", VarList.RSP_NO_DATA_FOUND);
        }

        AllCountryResponse countryResponse = new AllCountryResponse();
        countryResponse.setCountries(countryList);
        countryResponse.setVariable(VarList.RSP_SUCCESS);

        return countryResponse;
    }
}
