package com.fliqo.controller.dto.response.footfall;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유동 예측 응답")
public record FootfallForecastResponse(List<Item> forecast, String model) {
    public static FootfallForecastResponse sample() {
        return new FootfallForecastResponse(
                List.of(new Item("2025-09-18", 3100), new Item("2025-09-19", 3250)),
                "Prophet/LSTM");
    }

    public record Item(String date, int expected) {}
}
