package com.fliqo.controller.dto.response;

public record EmailCheckResponse(boolean exists, String nextAction) {
    public static EmailCheckResponse from(boolean exists) {
        return new EmailCheckResponse(exists, exists ? "LOGIN" : "SIGNUP");
    }
}
