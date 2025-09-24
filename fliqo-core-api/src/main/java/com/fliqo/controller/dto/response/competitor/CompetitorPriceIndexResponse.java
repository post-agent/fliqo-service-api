package com.fliqo.controller.dto.response.competitor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가격 지수 응답")
public record CompetitorPriceIndexResponse(Index index) {
    public static CompetitorPriceIndexResponse sample() {
        return new CompetitorPriceIndexResponse(new Index("아메리카노", 4100, 4000, 1.03));
    }

    public record Index(String base_item, int avg_price, int ref_price, double price_index) {}
}
