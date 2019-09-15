package uk.org.landeg.mandel;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MandelbrotApplication {

	public static void main(String[] args) {
	  SpringApplicationBuilder builder = new SpringApplicationBuilder(MandelbrotApplication.class);
	  builder.headless(false).run(args);
	}
}
