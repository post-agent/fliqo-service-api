package com.fliqo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fliqo.controller.dto.response.market.MarketOverviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/market")
@Tag(name = "Market Overview", description = "상권 개요/KPI")
public class MarketOverviewController {

    @Operation(summary = "상권 KPI 조회", description = "매장 반경 기준 상권 KPI 요약")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarketOverviewResponse.class)))
    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketOverviewResponse> getOverview(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @RequestParam(name = "store_id") String storeId,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "radius") Integer radius) {
        MarketOverviewResponse response = MarketOverviewResponse.sample(storeId, from, to, radius);
        return ResponseEntity.ok(response);
    }
}
