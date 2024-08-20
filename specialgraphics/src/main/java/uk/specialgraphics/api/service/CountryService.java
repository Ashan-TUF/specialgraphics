package uk.specialgraphics.api.service;

import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.payload.response.SuccessResponse;

public interface CountryService {

    AllCountryResponse getAllCountries();
}
