import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.io.File;

public class Download {
    public static void main(String[] args) throws Exception {
        new File("lib").mkdirs();
        URL website = new URL("https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar");
        try (InputStream in = website.openStream()) {
            Files.copy(in, Paths.get("lib/mysql-connector-j-8.0.33.jar"), StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Downloaded successfully!");
    }
}
