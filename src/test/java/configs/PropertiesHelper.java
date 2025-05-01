package configs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {

    private static final Properties properties = new Properties();

    public PropertiesHelper() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/config.properties");
        properties.load(fileInputStream);
    }


    public static String getProperty(String name) throws IOException {
        return properties.getProperty(name);
    }
}
