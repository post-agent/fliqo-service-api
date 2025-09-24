package com.fliqo.controller.dto.response.competitor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경쟁점 리스트 응답")
public record CompetitorListResponse(List<Item> list) {
    public static CompetitorListResponse sample() {
        return new CompetitorListResponse(
                List.of(
                        new Item("스타벅스 안산점", 4.3, 150),
                        new Item("투썸플레이스", 4.1, 230),
                        new Item("이디야커피", 4.2, 380)));
    }

    public record Item(String name, double rating, int distance) {}
}
