package com.fliqo.controller.dto.response.competitor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경쟁점 요약 응답")
public record CompetitorSummaryResponse(Summary summary) {
    public static CompetitorSummaryResponse sample() {
        return new CompetitorSummaryResponse(new Summary(12, 320, new TopBrandShare("스타벅스", 0.33)));
    }

    public record Summary(int total, int avg_distance, TopBrandShare top_brand_share) {}

    public record TopBrandShare(String brand, double share) {}
}
