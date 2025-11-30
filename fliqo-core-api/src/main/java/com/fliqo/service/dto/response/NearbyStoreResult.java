package com.fliqo.service.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record NearbyStoreResult(
        Long id,
        String storeName,
        String rgstPk,
        String baseYm,
        String stdIndTypeNm,
        BigDecimal sumAmt,
        double distanceMeters) {}
