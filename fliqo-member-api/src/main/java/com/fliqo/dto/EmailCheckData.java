package com.fliqo.dto;

public record EmailCheckData(boolean exists, String action, String email) {
}
