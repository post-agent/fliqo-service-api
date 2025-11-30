package com.fliqo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fliqo.controller.dto.response.NearbyStoreResponse;
import com.fliqo.controller.dto.response.StoreDetailResponse;
import com.fliqo.dto.CommonResponse;
import com.fliqo.exception.ErrorCode;
import com.fliqo.service.MarketService;
import com.fliqo.service.dto.request.FindNearbyStoresCommand;
import com.fliqo.service.dto.request.FindStoreDetailCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/market")
@Tag(name = "Market", description = "상권 분석 API")
public class MarketController {
    private final MarketService marketService;

    /**
     * 반경 200m 내 가게 목록 조회
     *
     * @param longitude 경도
     * @param latitude 위도
     * @param radiusMeters 반경 (미터, 기본값 200)
     * @return 반경 내 가게 목록
     */
    @Operation(summary = "반경 내 가게 목록 조회", description = "사용자 가게의 좌표를 기준으로 반경 내의 다른 가게 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NearbyStoreResponse.class)))
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<NearbyStoreResponse>>> getNearbyStores(
            @Parameter(description = "경도", required = true) @RequestParam @NotBlank
                    String longitude,
            @Parameter(description = "위도", required = true) @RequestParam @NotBlank String latitude,
            @Parameter(description = "반경 (미터)", required = false)
                    @RequestParam(required = false, defaultValue = "200.0")
                    Double radiusMeters) {

        FindNearbyStoresCommand command =
                FindNearbyStoresCommand.builder()
                        .longitude(longitude)
                        .latitude(latitude)
                        .radiusMeters(radiusMeters != null ? radiusMeters : 200.0)
                        .build();

        List<NearbyStoreResponse> responses =
                marketService.findNearbyStores(command).stream()
                        .map(NearbyStoreResponse::from)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(CommonResponse.success(responses));
    }

    /**
     * 가게 상세 정보 조회
     *
     * @param storeName 가게명
     * @param rgstPk 사업자등록번호
     * @return 가게 상세 정보 (매출액, 유동인구 수, 시간대별 보행인구 수)
     */
    @Operation(summary = "가게 상세 정보 조회", description = "가게명과 사업자등록번호로 가게의 상세 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StoreDetailResponse.class)))
    @GetMapping("/{storeName}/{rgstPk}")
    public ResponseEntity<CommonResponse<StoreDetailResponse>> getStoreDetail(
            @Parameter(description = "가게명", required = true) @PathVariable @NotBlank
                    String storeName,
            @Parameter(description = "사업자등록번호", required = true) @PathVariable @NotBlank
                    String rgstPk) {

        FindStoreDetailCommand command =
                FindStoreDetailCommand.builder().storeName(storeName).rgstPk(rgstPk).build();

        return marketService
                .findStoreDetail(command)
                .map(
                        result ->
                                ResponseEntity.ok(
                                        CommonResponse.success(StoreDetailResponse.from(result))))
                .orElseGet(
                        () ->
                                ResponseEntity.ok(
                                        CommonResponse.error(ErrorCode.RESOURCE_NOT_FOUND)));
    }
}
