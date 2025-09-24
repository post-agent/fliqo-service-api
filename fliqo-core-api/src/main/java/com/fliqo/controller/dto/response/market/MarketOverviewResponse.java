package com.fliqo.controller.dto.response.market;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상권 KPI 개요 응답")
public record MarketOverviewResponse(
        Meta meta, Kpis kpis, List<Competitor> competitors, List<Insight> insights) {

    public static MarketOverviewResponse sample(
            String storeId, String from, String to, Integer radius) {
        return new MarketOverviewResponse(
                new Meta(storeId, from, to, radius),
                new Kpis(3247, "+12%", 87, "+5", List.of(new Audience("20대 여성", 0.38))),
                List.of(
                        new Competitor("스타벅스 안산점", 150),
                        new Competitor("투썸플레이스", 230),
                        new Competitor("이디야커피", 380)),
                List.of(
                        new Insight("opportunity", "14–16시 유동인구 25% 증가 → 디저트 프로모션 고려"),
                        new Insight("risk", "인근 브랜드 신메뉴 출시로 20대 고객 이탈 가능성")));
    }

    public record Meta(String store_id, String from, String to, Integer radius) {}

    public record Kpis(
            int avg_footfall,
            String footfall_change,
            int market_index,
            String market_index_change,
            List<Audience> top_audience) {}

    public record Audience(String segment, double ratio) {}

    public record Competitor(String name, int distance) {}

    public record Insight(String type, String message) {}
}
