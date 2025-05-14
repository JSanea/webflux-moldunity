package web.app.webflux_moldunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebfluxMoldunityApplication {
	public static void main(String[] args) {
//		String heicPath = "/home/alx/Downloads/heic.heic";
//		String heifPath = "/home/alx/Downloads/heif.heif";
//		String jpgPath  = "/home/alx/Downloads/jpg.jpg";
//		File heic = new File(heicPath);
//		File heif = new File(heifPath);
//		File jpg = new File(jpgPath);
//
//		List<File> files = new ArrayList<>(Arrays.asList(heic, heif, jpg));
//
//		ImageService.convertImages2(files).subscribe();

		SpringApplication.run(WebfluxMoldunityApplication.class, args);
//
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
	}
}
