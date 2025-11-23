package com.fliqo.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fliqo.domain.entity.WalkMaster;

public interface WalkMasterRepository extends JpaRepository<WalkMaster, Long> {

    /**
     * 반경 내 유동인구 정보 조회 (PostGIS)
     *
     * @param longitude 중심점 경도
     * @param latitude 중심점 위도
     * @param radiusMeters 반경 (미터)
     * @return 반경 내 유동인구 정보 목록
     */
    @Query(
            value =
                    "SELECT wm.id, wm.corc_walk_popl_cnt, wm.tmzn_0810_pt, wm.tmzn_1113_pt, "
                            + "wm.tmzn_1416_pt, wm.tmzn_1719_pt, wm.tmzn_2022_pt, "
                            + "ST_Distance("
                            + "  ST_GeomFromText('POINT(' || wm.cell_xcrd || ' ' || wm.cell_ycrd || ')', 4326), "
                            + "  ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326)"
                            + ") * 111000 AS distance_meters "
                            + "FROM tb_walk_mst wm "
                            + "WHERE wm.cell_xcrd IS NOT NULL "
                            + "  AND wm.cell_ycrd IS NOT NULL "
                            + "  AND wm.cell_xcrd BETWEEN :minLng AND :maxLng "
                            + "  AND wm.cell_ycrd BETWEEN :minLat AND :maxLat "
                            + "  AND ST_Distance("
                            + "    ST_GeomFromText('POINT(' || wm.cell_xcrd || ' ' || wm.cell_ycrd || ')', 4326), "
                            + "    ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326)"
                            + "  ) * 111000 <= :radiusMeters "
                            + "ORDER BY distance_meters ASC",
            nativeQuery = true)
    List<Object[]> findNearbyWalkData(
            @Param("longitude") BigDecimal longitude,
            @Param("latitude") BigDecimal latitude,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng,
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("radiusMeters") double radiusMeters);

    /**
     * base_yymm과 cell_id로 조회
     *
     * @param baseYymm 기준년월
     * @param cellId 셀 ID
     * @return 유동인구 정보
     */
    Optional<WalkMaster> findByBaseYymmAndCellId(String baseYymm, String cellId);

    /**
     * base_yymm으로 조회
     *
     * @param baseYymm 기준년월
     * @return 유동인구 정보 목록
     */
    List<WalkMaster> findByBaseYymm(String baseYymm);
}
