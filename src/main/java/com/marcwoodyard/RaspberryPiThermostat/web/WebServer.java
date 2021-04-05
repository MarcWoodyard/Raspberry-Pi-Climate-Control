package com.marcwoodyard.RaspberryPiThermostat.web;

import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import com.marcwoodyard.RaspberryPiThermostat.web.security.KeyStoreManager;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WebServer {

    private static Boolean serverRunning = false;
    private static ConfigurableApplicationContext ctx;
    private static int port;

    public static boolean startServer(int port) {
        WebServer.port = port;

        new KeyStoreManager();

        System.getProperties().put("server.ssl.key-store-type", "PKCS12");
        System.getProperties().put("server.ssl.key-store", "keystore.pfx");
        System.getProperties().put("server.ssl.key-alias", "WebInterface");
        System.getProperties().put("server.ssl.key-store-password", ProgramSettings.getWebPassword());
        System.getProperties().put("security.require-ssl", true);
        System.getProperties().put("server.port", port);

        serverRunning = true;
        ctx = SpringApplication.run(WebServer.class);
        return serverRunning;
    }

    public static boolean stopServer() {
        serverRunning = false;
        // https://stackoverflow.com/questions/22944144/programmatically-shut-down-spring-boot-application
        ctx.close();
        return serverRunning;
    }

    public static Boolean isServerStarted() {
        return serverRunning;
    }

    // Redirect to SSL port.
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        //tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    public static int getPort() { return port; }
/*
    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
    */

}
