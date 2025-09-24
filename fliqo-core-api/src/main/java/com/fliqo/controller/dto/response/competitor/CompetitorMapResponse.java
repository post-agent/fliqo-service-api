package com.fliqo.controller.dto.response.competitor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경쟁점 분포 응답")
public record CompetitorMapResponse(List<Item> competitors) {
    public static CompetitorMapResponse sample() {
        return new CompetitorMapResponse(
                List.of(
                        new Item("스타벅스 안산점", 37.123, 126.987, 4.3, 150),
                        new Item("투썸플레이스", 37.125, 126.990, 4.1, 230)));
    }

    public record Item(String name, double lat, double lng, double rating, int distance) {}
}
