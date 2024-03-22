package br.com.cesarschool.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetParameters {
    public String getParameter(String parameterName) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("./src/main/resources/properties/configuration.properties")) {
            properties.load(fileInputStream);            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(parameterName);
    }
}
