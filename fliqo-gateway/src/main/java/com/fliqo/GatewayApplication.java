package com.fliqo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.fliqo.jwt.JwtProperties;

@ConfigurationPropertiesScan(basePackageClasses = JwtProperties.class)
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
