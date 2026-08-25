package shelfsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShelfSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShelfSyncApplication.class, args);
	}

}
