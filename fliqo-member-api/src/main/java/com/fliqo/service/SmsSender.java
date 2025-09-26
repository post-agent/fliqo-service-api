package com.fliqo.service;

public interface SmsSender {
    void send(String phoneNumber, String message);
}
