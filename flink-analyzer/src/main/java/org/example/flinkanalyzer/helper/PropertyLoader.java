package org.example.flinkanalyzer.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author irfan.nagoo
 */

@Slf4j
public class PropertyLoader {

    public static PropertyLoader INSTANCE = new PropertyLoader("application.properties");

    private final String propertyFileName;
    private Properties properties;

    public PropertyLoader(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        load();
    }

    private void load() {
        try (InputStream inputStream = PropertyLoader.class.getClassLoader().getResourceAsStream(propertyFileName)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Error occurred while reading file: ", e);
        }
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }


}
