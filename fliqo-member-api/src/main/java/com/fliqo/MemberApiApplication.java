package com.fliqo;

import com.fliqo.config.AuthProperties;
import com.fliqo.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan(basePackageClasses = JwtProperties.class)
@EnableConfigurationProperties(AuthProperties.class)
@SpringBootApplication
public class MemberApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApiApplication.class, args);
    }
}
