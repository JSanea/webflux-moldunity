package web.app.webflux_moldunity.util;

import java.io.IOException;

public class ImageConverterUtil {

    public static boolean convertToWebp(String inputPath, String outputPath, Integer quality, Integer height, Integer width) throws IOException, InterruptedException{
        String[] command = {
                "cwebp",
                "-q", String.valueOf(quality),
                "-resize", String.valueOf(height), String.valueOf(width),
                inputPath,
                "-o", outputPath
        };

        return convertImage(command);
    }

    public static boolean convertHeicToJpg(String inputPath, String outputPath, String quality) throws IOException, InterruptedException {
        String[] command = {
                "heif-convert",
                "-q", quality,
                inputPath,
                outputPath
        };

        return convertImage(command);
    }

    public static boolean convertImage(String[] command) throws IOException, InterruptedException {
        String output = command[command.length - 1];
        if (executeCommand(command)){
            System.out.println("Conversion successful: " + output);
            return true;
        }else {
            System.err.println("Conversion failed: " + output);
            return false;
        }
    }

    private static boolean executeCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        return pb.start().waitFor() == 0;
    }
}
