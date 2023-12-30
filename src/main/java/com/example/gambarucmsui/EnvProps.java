package com.example.gambarucmsui;

import java.io.IOException;
import java.util.Properties;

// Exposes environment properties as a service
// Reads file src/main/resources/application.properties
public class EnvProps {
    private static EnvProps envProps = new EnvProps();
    public static EnvProps getInstance() {
        return envProps;
    }

    private final Properties props;
    private EnvProps() {
        props = new Properties();
        try {
            props.load(GambaruSwitchController.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getProps() {
        return props;
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getJdbcUrl() {
        return props.getProperty("jakarta.persistence.jdbc.url");
    }
    public String getJdbcDriver() {
        return props.getProperty("jakarta.persistence.jdbc.driver");
    }
    public String getJdbcUsername() {
        return props.getProperty("jakarta.persistence.jdbc.user");
    }
    public String getJdbcPassword() {
        return props.getProperty("jakarta.persistence.jdbc.password");
    }
}
