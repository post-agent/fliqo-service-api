package com.fliqo.service.dto.request;

import lombok.Builder;

@Builder
public record FindNearbyStoresCommand(String longitude, String latitude, double radiusMeters) {}
