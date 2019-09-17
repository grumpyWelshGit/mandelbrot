package uk.org.landeg.mandel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MandelbrotDriver {
  @Autowired
  MandelbrotService service;

  @Bean
  CommandLineRunner runIt() {
    return args -> {
      service.initialise();
    };
  }

}
