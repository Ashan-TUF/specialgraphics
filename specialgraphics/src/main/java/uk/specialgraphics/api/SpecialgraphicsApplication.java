package uk.specialgraphics.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.model.GenerateKeys;
import uk.specialgraphics.api.payload.response.GeneralUserProfileResponse;
import uk.specialgraphics.api.utils.GenerateKey;

@SpringBootApplication
@RestController
@Slf4j
@EnableScheduling
public class SpecialgraphicsApplication {
	public static GenerateKeys keys;
	public static void main(String[] args) {
		SpringApplication.run(SpecialgraphicsApplication.class, args);
	}
	@Bean
	public GeneralUserProfileResponse  generalUserProfileResponse() {
		return new GeneralUserProfileResponse();
	}
	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		return (args) -> {
			log.info("Generate New Security Keys");
			keys = new GenerateKey().generateKeys();
		};
	}
}
