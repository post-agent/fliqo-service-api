package com.fliqo.controller.dto.response.competitor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "브랜드 점유율 응답")
public record CompetitorBrandsResponse(List<Brand> brands) {
    public static CompetitorBrandsResponse sample() {
        return new CompetitorBrandsResponse(
                List.of(
                        new Brand("스타벅스", 0.33),
                        new Brand("투썸플레이스", 0.22),
                        new Brand("이디야", 0.15)));
    }

    public record Brand(String name, double share) {}
}
