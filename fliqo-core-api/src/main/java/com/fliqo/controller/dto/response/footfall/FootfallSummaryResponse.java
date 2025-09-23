package com.fliqo.controller.dto.response.footfall;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유동인구 요약 응답")
public record FootfallSummaryResponse(
        Meta meta, int avg_daily, Delta delta, String peak_time, String low_time) {
    public static FootfallSummaryResponse sample(String storeId, String from, String to) {
        return new FootfallSummaryResponse(
                new Meta(storeId, from + " ~ " + to), 3247, new Delta("+12%"), "14–16시", "10–11시");
    }

    public record Meta(String store_id, String period) {}

    public record Delta(String week_over_week) {}
}
