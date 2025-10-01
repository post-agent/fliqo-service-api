package com.fliqo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;
    private long accessMin;
    private long refreshDay;
    private String secretBase64;

    public JwtProperties(String issuer, long accessMin, long refreshDay, String secretBase64) {
        this.issuer = issuer;
        this.accessMin = accessMin;
        this.refreshDay = refreshDay;
        this.secretBase64 = secretBase64;
    }
}
