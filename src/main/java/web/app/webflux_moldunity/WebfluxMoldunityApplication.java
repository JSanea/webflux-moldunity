package web.app.webflux_moldunity;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import web.app.webflux_moldunity.util.ImageConverterUtil;
import web.app.webflux_moldunity.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@SpringBootApplication
@EnableScheduling
public class WebfluxMoldunityApplication {
	public static void main(String[] args) {
		String heic = "/home/alx/Downloads/heic.heic";
		String jpg = "/home/alx/Downloads/jpg.jpg";
		String webp = "/home/alx/Downloads/webp.webp";
		Path pathToFile = Path.of(heic);
		File f = pathToFile.toFile();
		System.out.println(f.getAbsolutePath());
		boolean isHEIC = ImageUtil.isHEIC(pathToFile.toFile());
		System.out.println("Is HEIC/HEIF: " + isHEIC);

		try {
			ImageConverterUtil.convertHeicToJpg(heic, jpg, "80");
			ImageConverterUtil.convertToWebp(jpg, webp, 80, 800, 600);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		//SpringApplication.run(WebfluxMoldunityApplication.class, args);
	}
}
