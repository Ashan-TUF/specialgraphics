package uk.specialgraphics.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.response.AllCountryResponse;
import uk.specialgraphics.api.service.CountryService;

@RestController
@RequestMapping("/country")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CountryController {

    @Autowired
    CountryService countryService;

    @PostMapping("/getAllCountries")
    public AllCountryResponse getAllCountries() {
        return countryService.getAllCountries();
    }
}
