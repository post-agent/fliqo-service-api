package com.fliqo.service;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FakeSmsSender implements SmsSender {
    @Override
    public void send(String phoneNumber, String message) {
        log.info("[FAKE-SMS] to={} body={}", phoneNumber, message);
    }
}
