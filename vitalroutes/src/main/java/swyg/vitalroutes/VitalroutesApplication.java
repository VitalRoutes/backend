package swyg.vitalroutes;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEncryptableProperties
@SpringBootApplication
public class VitalroutesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VitalroutesApplication.class, args);
	}

}
