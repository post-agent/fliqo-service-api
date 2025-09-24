package com.fliqo.controller.dto.response.footfall;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유동인구 분포 응답")
public record FootfallDistributionResponse(List<Weekday> weekday, List<Hour> hour) {
    public static FootfallDistributionResponse sample() {
        return new FootfallDistributionResponse(
                List.of(new Weekday("Mon", 3100), new Weekday("Sat", 4100)),
                List.of(new Hour("14", 1200), new Hour("15", 1300)));
    }

    public record Weekday(String day, int avg) {}

    public record Hour(String hour, int avg) {}
}
