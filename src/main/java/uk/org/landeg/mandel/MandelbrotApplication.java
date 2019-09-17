package uk.org.landeg.mandel;

import java.awt.Rectangle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MandelbrotApplication {

	public static void main(String[] args) {
	  SpringApplicationBuilder builder = new SpringApplicationBuilder(MandelbrotApplication.class);
	  builder.headless(false).run(args);
	}

	@Bean
	public BlockingQueue<Rectangle> cellQueue () {
	  return new ArrayBlockingQueue<>(1000000);
	}
}
