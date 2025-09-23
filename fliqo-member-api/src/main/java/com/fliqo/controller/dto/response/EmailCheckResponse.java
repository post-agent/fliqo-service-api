package com.fliqo.controller.dto.response;

public record EmailCheckResponse(boolean exists, NextAction nextAction) {
    public static EmailCheckResponse of(boolean exists) {
        return new EmailCheckResponse(exists, exists ? NextAction.LOGIN : NextAction.SIGNUP);
    }
}
