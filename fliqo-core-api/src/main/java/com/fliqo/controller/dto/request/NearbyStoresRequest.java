package com.fliqo.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NearbyStoresRequest(
        @NotBlank(message = "경도는 필수입니다.") String longitude,
        @NotBlank(message = "위도는 필수입니다.") String latitude,
        @NotNull(message = "반경은 필수입니다.") Double radiusMeters) {}
