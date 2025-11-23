package com.fliqo.controller.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record StoreDetailResponse(
        Long id,
        String storeName,
        String rgstPk,
        String baseYm,
        String stdIndTypeNm,
        BigDecimal sumAmt,
        BigDecimal sumCnt,
        BigDecimal meanAmt,
        Long totalWalkPopulation,
        TimeSlotWalkPopulationResponse timeSlotWalkPopulation) {

    @Builder
    public record TimeSlotWalkPopulationResponse(
            BigDecimal tmzn0810Pt, // 08-10시
            BigDecimal tmzn1113Pt, // 11-13시
            BigDecimal tmzn1416Pt, // 14-16시
            BigDecimal tmzn1719Pt, // 17-19시
            BigDecimal tmzn2022Pt) {} // 20-22시

    public static StoreDetailResponse from(
            com.fliqo.service.dto.response.StoreDetailResult result) {
        return StoreDetailResponse.builder()
                .id(result.id())
                .storeName(result.storeName())
                .rgstPk(result.rgstPk())
                .baseYm(result.baseYm())
                .stdIndTypeNm(result.stdIndTypeNm())
                .sumAmt(result.sumAmt())
                .sumCnt(result.sumCnt())
                .meanAmt(result.meanAmt())
                .totalWalkPopulation(result.totalWalkPopulation())
                .timeSlotWalkPopulation(
                        result.timeSlotWalkPopulation() != null
                                ? TimeSlotWalkPopulationResponse.builder()
                                        .tmzn0810Pt(result.timeSlotWalkPopulation().tmzn0810Pt())
                                        .tmzn1113Pt(result.timeSlotWalkPopulation().tmzn1113Pt())
                                        .tmzn1416Pt(result.timeSlotWalkPopulation().tmzn1416Pt())
                                        .tmzn1719Pt(result.timeSlotWalkPopulation().tmzn1719Pt())
                                        .tmzn2022Pt(result.timeSlotWalkPopulation().tmzn2022Pt())
                                        .build()
                                : null)
                .build();
    }
}
