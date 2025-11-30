package com.fliqo.service.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record StoreDetailResult(
        Long id,
        String storeName,
        String rgstPk,
        String baseYm,
        String stdIndTypeNm,
        BigDecimal sumAmt,
        BigDecimal sumCnt,
        BigDecimal meanAmt,
        Long totalWalkPopulation,
        TimeSlotWalkPopulation timeSlotWalkPopulation) {

    @Builder
    public record TimeSlotWalkPopulation(
            BigDecimal tmzn0810Pt, // 08-10시
            BigDecimal tmzn1113Pt, // 11-13시
            BigDecimal tmzn1416Pt, // 14-16시
            BigDecimal tmzn1719Pt, // 17-19시
            BigDecimal tmzn2022Pt) {} // 20-22시
}
