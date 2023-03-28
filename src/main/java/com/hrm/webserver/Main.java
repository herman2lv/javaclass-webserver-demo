package com.hrm.webserver;

import com.hrm.weblib.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("unchecked")
public class Main {

    public static final String DEFAULT_PORT = "8080";
    public static final String CONFIG_FILE = "/server.properties";
    public static final String CONTROLLER_KEY = "controller";
    public static final String PORT_KEY = "port";

    public static void main(String[] args) throws Exception {
        Props props = readProperties();
        Controller controller = getController(props.controller());
        Server server = new Server(controller);
        server.run(props.port());
    }

    private static Controller getController(String controllerClass) throws Exception {
        Class<Controller> clazz = (Class<Controller>) Class.forName(controllerClass);
        return clazz.getConstructor().newInstance();
    }

    private static Props readProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Main.class.getResourceAsStream(CONFIG_FILE)) {
            properties.load(in);
        }
        String controllerClass = properties.getProperty(CONTROLLER_KEY);
        String portStr = properties.getProperty(PORT_KEY, DEFAULT_PORT);
        int port = Integer.parseInt(portStr);
        return new Props(controllerClass, port);
    }

    record Props (String controller, int port) {}

}
