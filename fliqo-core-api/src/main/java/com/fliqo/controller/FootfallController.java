package com.fliqo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fliqo.controller.dto.response.footfall.FootfallDistributionResponse;
import com.fliqo.controller.dto.response.footfall.FootfallFactorsResponse;
import com.fliqo.controller.dto.response.footfall.FootfallForecastResponse;
import com.fliqo.controller.dto.response.footfall.FootfallSummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/market/footfall")
@Tag(name = "Footfall", description = "유동인구 분석")
public class FootfallController {

    @Operation(summary = "유동인구 요약 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FootfallSummaryResponse.class)))
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootfallSummaryResponse> getSummary(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @RequestParam(name = "store_id") String storeId,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "radius") Integer radius) {
        return ResponseEntity.ok(FootfallSummaryResponse.sample(storeId, from, to));
    }

    @Operation(summary = "요일/시간대 분포 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FootfallDistributionResponse.class)))
    @GetMapping(value = "/distribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootfallDistributionResponse> getDistribution(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @RequestParam(name = "group_by") String groupBy) {
        return ResponseEntity.ok(FootfallDistributionResponse.sample());
    }

    @Operation(summary = "유동 변화 요인 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FootfallFactorsResponse.class)))
    @GetMapping(value = "/factors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootfallFactorsResponse> getFactors(
            @RequestHeader(name = "Authorization", required = true) String authorization) {
        return ResponseEntity.ok(FootfallFactorsResponse.sample());
    }

    @Operation(summary = "유동 예측 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FootfallForecastResponse.class)))
    @GetMapping(value = "/forecast", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootfallForecastResponse> getForecast(
            @RequestHeader(name = "Authorization", required = true) String authorization) {
        return ResponseEntity.ok(FootfallForecastResponse.sample());
    }
}
