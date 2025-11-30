package com.fliqo.controller.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record NearbyStoreResponse(
        Long id,
        String storeName,
        String rgstPk,
        String baseYm,
        String stdIndTypeNm,
        BigDecimal sumAmt,
        double distanceMeters) {
    public static NearbyStoreResponse from(
            com.fliqo.service.dto.response.NearbyStoreResult result) {
        return NearbyStoreResponse.builder()
                .id(result.id())
                .storeName(result.storeName())
                .rgstPk(result.rgstPk())
                .baseYm(result.baseYm())
                .stdIndTypeNm(result.stdIndTypeNm())
                .sumAmt(result.sumAmt())
                .distanceMeters(result.distanceMeters())
                .build();
    }
}
