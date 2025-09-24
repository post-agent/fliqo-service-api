package com.fliqo.controller.dto.response.footfall;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유동 변화 요인 응답")
public record FootfallFactorsResponse(List<Factor> factors) {
    public static FootfallFactorsResponse sample() {
        return new FootfallFactorsResponse(
                List.of(
                        new Factor("강수량", "negative"),
                        new Factor("기온", "u-shape"),
                        new Factor("프로모션", "positive")));
    }

    public record Factor(String name, String correlation) {}
}
