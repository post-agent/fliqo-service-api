package com.fliqo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fliqo.controller.dto.response.competitor.CompetitorBrandsResponse;
import com.fliqo.controller.dto.response.competitor.CompetitorListResponse;
import com.fliqo.controller.dto.response.competitor.CompetitorMapResponse;
import com.fliqo.controller.dto.response.competitor.CompetitorPriceIndexResponse;
import com.fliqo.controller.dto.response.competitor.CompetitorSummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/market/competitors")
@Tag(name = "Competitors", description = "경쟁 분석")
public class CompetitorController {

    @Operation(summary = "경쟁점 요약 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorSummaryResponse.class)))
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompetitorSummaryResponse> getSummary(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @RequestParam(name = "radius") Integer radius,
            @RequestParam(name = "period") String period) {
        return ResponseEntity.ok(CompetitorSummaryResponse.sample());
    }

    @Operation(summary = "브랜드 점유율 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorBrandsResponse.class)))
    @GetMapping(value = "/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompetitorBrandsResponse> getBrands(
//            @RequestHeader(name = "Authorization", required = true) String authorization
    ) {
        return ResponseEntity.ok(CompetitorBrandsResponse.sample());
    }

    @Operation(summary = "가격 지수 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorPriceIndexResponse.class)))
    @GetMapping(value = "/price-index", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompetitorPriceIndexResponse> getPriceIndex(
            @RequestHeader(name = "Authorization", required = true) String authorization) {
        return ResponseEntity.ok(CompetitorPriceIndexResponse.sample());
    }

    @Operation(summary = "경쟁점 분포 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorMapResponse.class)))
    @GetMapping(value = "/map", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompetitorMapResponse> getMap(
            @RequestHeader(name = "Authorization", required = true) String authorization) {
        return ResponseEntity.ok(CompetitorMapResponse.sample());
    }

    @Operation(summary = "경쟁점 상세 리스트 조회")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompetitorListResponse.class)))
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompetitorListResponse> getList(
            @RequestHeader(name = "Authorization", required = true) String authorization) {
        return ResponseEntity.ok(CompetitorListResponse.sample());
    }
}
