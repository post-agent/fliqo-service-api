package com.fliqo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fliqo.domain.entity.CardSales;
import com.fliqo.domain.repository.CardSalesRepository;
import com.fliqo.domain.repository.WalkMasterRepository;
import com.fliqo.service.dto.request.FindNearbyStoresCommand;
import com.fliqo.service.dto.request.FindStoreDetailCommand;
import com.fliqo.service.dto.response.NearbyStoreResult;
import com.fliqo.service.dto.response.StoreDetailResult;
import com.fliqo.service.dto.response.StoreDetailResult.TimeSlotWalkPopulation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final CardSalesRepository cardSalesRepository;
    private final WalkMasterRepository walkMasterRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_RADIUS_METERS = 200.0;

    /**
     * 반경 내 가게 목록 조회
     *
     * @param command 위도, 경도, 반경 정보
     * @return 반경 내 가게 목록
     */
    @Transactional(readOnly = true)
    public List<NearbyStoreResult> findNearbyStores(FindNearbyStoresCommand command) {
        String longitude = command.longitude();
        String latitude = command.latitude();
        double radiusMeters =
                command.radiusMeters() > 0 ? command.radiusMeters() : DEFAULT_RADIUS_METERS;

        // 좌표 유효성 검증
        BigDecimal lng = parseCoordinate(longitude);
        BigDecimal lat = parseCoordinate(latitude);

        // 반경에 따른 경계 계산 (대략적인 범위로 필터링 성능 향상)
        double radiusKm = radiusMeters / 1000.0;
        BigDecimal minLng =
                lng.subtract(
                        BigDecimal.valueOf(radiusKm / Math.cos(Math.toRadians(lat.doubleValue()))));
        BigDecimal maxLng =
                lng.add(BigDecimal.valueOf(radiusKm / Math.cos(Math.toRadians(lat.doubleValue()))));
        BigDecimal minLat = lat.subtract(BigDecimal.valueOf(radiusKm));
        BigDecimal maxLat = lat.add(BigDecimal.valueOf(radiusKm));

        // Repository에서 반경 내 가게 조회
        List<Object[]> results =
                cardSalesRepository.findNearbyStores(
                        longitude, latitude, minLng, maxLng, minLat, maxLat, radiusMeters);

        return convertToNearbyStoreResults(results);
    }

    /**
     * 가게 상세 정보 조회 (매출액, 유동인구 수, 시간대별 보행인구 수)
     *
     * @param command 가게명, 사업자등록번호
     * @return 가게 상세 정보
     */
    @Transactional(readOnly = true)
    public Optional<StoreDetailResult> findStoreDetail(FindStoreDetailCommand command) {
        // 가게 정보 조회
        Optional<CardSales> cardSalesOpt =
                cardSalesRepository.findByStoreNameAndRgstPk(command.storeName(), command.rgstPk());

        if (cardSalesOpt.isEmpty()) {
            return Optional.empty();
        }

        CardSales cardSales = cardSalesOpt.get();

        // 좌표가 없으면 유동인구 정보를 조회할 수 없음
        if (cardSales.getXpoint() == null || cardSales.getYpoint() == null) {
            return Optional.of(buildStoreDetailResultWithoutWalkData(cardSales, null, null));
        }

        // 가게 좌표 기준으로 반경 내 유동인구 정보 조회
        BigDecimal longitude = parseCoordinate(cardSales.getXpoint());
        BigDecimal latitude = parseCoordinate(cardSales.getYpoint());

        // 반경 200m 내 유동인구 데이터 조회
        double radiusKm = DEFAULT_RADIUS_METERS / 1000.0;
        BigDecimal minLng =
                longitude.subtract(
                        BigDecimal.valueOf(
                                radiusKm / Math.cos(Math.toRadians(latitude.doubleValue()))));
        BigDecimal maxLng =
                longitude.add(
                        BigDecimal.valueOf(
                                radiusKm / Math.cos(Math.toRadians(latitude.doubleValue()))));
        BigDecimal minLat = latitude.subtract(BigDecimal.valueOf(radiusKm));
        BigDecimal maxLat = latitude.add(BigDecimal.valueOf(radiusKm));

        List<Object[]> walkDataResults =
                walkMasterRepository.findNearbyWalkData(
                        longitude, latitude, minLng, maxLng, minLat, maxLat, DEFAULT_RADIUS_METERS);

        // 유동인구 데이터 집계
        Long totalWalkPopulation = calculateTotalWalkPopulation(walkDataResults);
        TimeSlotWalkPopulation timeSlotWalkPopulation =
                calculateTimeSlotWalkPopulation(walkDataResults);

        return Optional.of(
                buildStoreDetailResult(cardSales, totalWalkPopulation, timeSlotWalkPopulation));
    }

    /** 좌표 문자열을 BigDecimal로 변환 */
    private BigDecimal parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.trim().isEmpty()) {
            throw new IllegalArgumentException("좌표 값이 유효하지 않습니다.");
        }
        try {
            return new BigDecimal(coordinate.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("좌표 값이 숫자 형식이 아닙니다: " + coordinate, e);
        }
    }

    /**
     * Repository 결과를 NearbyStoreResult 리스트로 변환 Object[] 구조: [id, store_name, rgst_pk, base_ym,
     * std_ind_type_nm, sum_amt, distance_meters]
     */
    private List<NearbyStoreResult> convertToNearbyStoreResults(List<Object[]> results) {
        List<NearbyStoreResult> storeResults = new ArrayList<>();

        for (Object[] row : results) {
            Long id = row[0] != null ? ((Number) row[0]).longValue() : null;
            String storeName = row[1] != null ? (String) row[1] : null;
            String rgstPk = row[2] != null ? (String) row[2] : null;
            String baseYm = row[3] != null ? (String) row[3] : null;
            String stdIndTypeNm = row[4] != null ? (String) row[4] : null;
            BigDecimal sumAmt = row[5] != null ? (BigDecimal) row[5] : null;
            Double distanceMeters = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0;

            NearbyStoreResult result =
                    NearbyStoreResult.builder()
                            .id(id)
                            .storeName(storeName)
                            .rgstPk(rgstPk)
                            .baseYm(baseYm)
                            .stdIndTypeNm(stdIndTypeNm)
                            .sumAmt(sumAmt)
                            .distanceMeters(distanceMeters)
                            .build();

            storeResults.add(result);
        }

        return storeResults;
    }

    /**
     * 총 보행인구 수 계산 Object[] 구조: [id, corc_walk_popl_cnt, tmzn_0810_pt, tmzn_1113_pt, tmzn_1416_pt,
     * tmzn_1719_pt, tmzn_2022_pt, distance_meters]
     */
    private Long calculateTotalWalkPopulation(List<Object[]> walkDataResults) {
        return walkDataResults.stream()
                .map(
                        row -> {
                            Long poplCnt = row[1] != null ? ((Number) row[1]).longValue() : null;
                            return poplCnt != null ? poplCnt : 0L;
                        })
                .reduce(0L, Long::sum);
    }

    /**
     * 시간대별 보행인구 비율 계산 (평균) Object[] 구조: [id, corc_walk_popl_cnt, tmzn_0810_pt, tmzn_1113_pt,
     * tmzn_1416_pt, tmzn_1719_pt, tmzn_2022_pt, distance_meters]
     */
    private TimeSlotWalkPopulation calculateTimeSlotWalkPopulation(List<Object[]> walkDataResults) {
        if (walkDataResults.isEmpty()) {
            return TimeSlotWalkPopulation.builder()
                    .tmzn0810Pt(BigDecimal.ZERO)
                    .tmzn1113Pt(BigDecimal.ZERO)
                    .tmzn1416Pt(BigDecimal.ZERO)
                    .tmzn1719Pt(BigDecimal.ZERO)
                    .tmzn2022Pt(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal sum0810 = BigDecimal.ZERO;
        BigDecimal sum1113 = BigDecimal.ZERO;
        BigDecimal sum1416 = BigDecimal.ZERO;
        BigDecimal sum1719 = BigDecimal.ZERO;
        BigDecimal sum2022 = BigDecimal.ZERO;

        int count = 0;
        for (Object[] row : walkDataResults) {
            Long poplCnt = row[1] != null ? ((Number) row[1]).longValue() : null;
            if (poplCnt != null && poplCnt > 0) {
                if (row[2] != null) sum0810 = sum0810.add((BigDecimal) row[2]);
                if (row[3] != null) sum1113 = sum1113.add((BigDecimal) row[3]);
                if (row[4] != null) sum1416 = sum1416.add((BigDecimal) row[4]);
                if (row[5] != null) sum1719 = sum1719.add((BigDecimal) row[5]);
                if (row[6] != null) sum2022 = sum2022.add((BigDecimal) row[6]);
                count++;
            }
        }

        BigDecimal divisor = BigDecimal.valueOf(count);
        return TimeSlotWalkPopulation.builder()
                .tmzn0810Pt(
                        count > 0
                                ? sum0810.divide(divisor, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO)
                .tmzn1113Pt(
                        count > 0
                                ? sum1113.divide(divisor, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO)
                .tmzn1416Pt(
                        count > 0
                                ? sum1416.divide(divisor, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO)
                .tmzn1719Pt(
                        count > 0
                                ? sum1719.divide(divisor, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO)
                .tmzn2022Pt(
                        count > 0
                                ? sum2022.divide(divisor, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO)
                .build();
    }

    /** StoreDetailResult 생성 (유동인구 데이터 포함) */
    private StoreDetailResult buildStoreDetailResult(
            CardSales cardSales,
            Long totalWalkPopulation,
            TimeSlotWalkPopulation timeSlotWalkPopulation) {
        return StoreDetailResult.builder()
                .id(cardSales.getId())
                .storeName(cardSales.getStoreName())
                .rgstPk(cardSales.getRgstPk())
                .baseYm(cardSales.getBaseYm())
                .stdIndTypeNm(cardSales.getStdIndTypeNm())
                .sumAmt(cardSales.getSumAmt())
                .sumCnt(cardSales.getSumCnt())
                .meanAmt(cardSales.getMeanAmt())
                .totalWalkPopulation(totalWalkPopulation)
                .timeSlotWalkPopulation(timeSlotWalkPopulation)
                .build();
    }

    /** StoreDetailResult 생성 (유동인구 데이터 없음) */
    private StoreDetailResult buildStoreDetailResultWithoutWalkData(
            CardSales cardSales,
            Long totalWalkPopulation,
            TimeSlotWalkPopulation timeSlotWalkPopulation) {
        return StoreDetailResult.builder()
                .id(cardSales.getId())
                .storeName(cardSales.getStoreName())
                .rgstPk(cardSales.getRgstPk())
                .baseYm(cardSales.getBaseYm())
                .stdIndTypeNm(cardSales.getStdIndTypeNm())
                .sumAmt(cardSales.getSumAmt())
                .sumCnt(cardSales.getSumCnt())
                .meanAmt(cardSales.getMeanAmt())
                .totalWalkPopulation(totalWalkPopulation)
                .timeSlotWalkPopulation(timeSlotWalkPopulation)
                .build();
    }
}
