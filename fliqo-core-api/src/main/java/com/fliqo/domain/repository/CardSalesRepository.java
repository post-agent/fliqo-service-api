package com.fliqo.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fliqo.domain.entity.CardSales;

public interface CardSalesRepository extends JpaRepository<CardSales, Long> {

    /**
     * 반경 내 가게 목록 조회 (PostGIS)
     *
     * @param longitude 중심점 경도
     * @param latitude 중심점 위도
     * @param radiusMeters 반경 (미터)
     * @return 반경 내 가게 목록
     */
    @Query(
            value =
                    "SELECT cs.id, cs.store_name, cs.rgst_pk, cs.base_ym, cs.std_ind_type_nm, cs.sum_amt, "
                            + "ST_Distance("
                            + "  ST_GeomFromText('POINT(' || cs.xpoint || ' ' || cs.ypoint || ')', 4326), "
                            + "  ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326)"
                            + ") * 111000 AS distance_meters "
                            + "FROM tb_card_sales cs "
                            + "WHERE cs.xpoint IS NOT NULL "
                            + "  AND cs.ypoint IS NOT NULL "
                            + "  AND cs.xpoint ~ '^[0-9.]+$' "
                            + "  AND cs.ypoint ~ '^[0-9.]+$' "
                            + "  AND CAST(cs.xpoint AS NUMERIC) BETWEEN :minLng AND :maxLng "
                            + "  AND CAST(cs.ypoint AS NUMERIC) BETWEEN :minLat AND :maxLat "
                            + "  AND ST_Distance("
                            + "    ST_GeomFromText('POINT(' || cs.xpoint || ' ' || cs.ypoint || ')', 4326), "
                            + "    ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326)"
                            + "  ) * 111000 <= :radiusMeters "
                            + "ORDER BY distance_meters ASC",
            nativeQuery = true)
    List<Object[]> findNearbyStores(
            @Param("longitude") String longitude,
            @Param("latitude") String latitude,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng,
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("radiusMeters") double radiusMeters);

    /**
     * 가게명과 사업자등록번호로 가게 조회 (가장 최근 데이터)
     *
     * @param storeName 가게명
     * @param rgstPk 사업자등록번호
     * @return 가게 정보 (가장 최근 base_ym 기준)
     */
    @Query(
            "SELECT cs FROM CardSales cs WHERE cs.storeName = :storeName AND cs.rgstPk = :rgstPk ORDER BY cs.baseYm DESC LIMIT 1")
    Optional<CardSales> findByStoreNameAndRgstPk(
            @Param("storeName") String storeName, @Param("rgstPk") String rgstPk);

    /**
     * 가게명으로 가게 목록 조회
     *
     * @param storeName 가게명
     * @return 가게 목록
     */
    List<CardSales> findByStoreName(String storeName);

    /**
     * 사업자등록번호로 가게 목록 조회
     *
     * @param rgstPk 사업자등록번호
     * @return 가게 목록
     */
    List<CardSales> findByRgstPk(String rgstPk);
}
