package com.fliqo.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_walk_mst")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "base_yymm", nullable = false, length = 6)
    private String baseYymm;

    @Column(name = "cell_id", nullable = false, length = 50)
    private String cellId;

    @Column(name = "cell_xcrd", precision = 12, scale = 6)
    private BigDecimal cellXcrd;

    @Column(name = "cell_ycrd", precision = 12, scale = 6)
    private BigDecimal cellYcrd;

    @Column(name = "ctdo_cd", length = 10)
    private String ctdoCd;

    @Column(name = "ctdo_nm", length = 100)
    private String ctdoNm;

    @Column(name = "ccw_cd", length = 10)
    private String ccwCd;

    @Column(name = "ccw_nm", length = 100)
    private String ccwNm;

    @Column(name = "adng_cd", length = 10)
    private String adngCd;

    @Column(name = "adng_nm", length = 100)
    private String adngNm;

    @Column(name = "emd_cd", length = 10)
    private String emdCd;

    @Column(name = "emd_nm", length = 100)
    private String emdNm;

    @Column(name = "corc_walk_popl_cnt")
    private Long corcWalkPoplCnt;

    @Column(name = "corc_ntwk_popl_cnt")
    private Long corcNtwkPoplCnt;

    @Column(name = "walk_male_pt", precision = 5, scale = 2)
    private BigDecimal walkMalePt;

    @Column(name = "walk_u20_pt", precision = 5, scale = 2)
    private BigDecimal walkU20Pt;

    @Column(name = "walk_20_pt", precision = 5, scale = 2)
    private BigDecimal walk20Pt;

    @Column(name = "walk_30_pt", precision = 5, scale = 2)
    private BigDecimal walk30Pt;

    @Column(name = "walk_40_pt", precision = 5, scale = 2)
    private BigDecimal walk40Pt;

    @Column(name = "walk_50_pt", precision = 5, scale = 2)
    private BigDecimal walk50Pt;

    @Column(name = "tmzn_0810_pt", precision = 5, scale = 2)
    private BigDecimal tmzn0810Pt;

    @Column(name = "tmzn_1113_pt", precision = 5, scale = 2)
    private BigDecimal tmzn1113Pt;

    @Column(name = "tmzn_1416_pt", precision = 5, scale = 2)
    private BigDecimal tmzn1416Pt;

    @Column(name = "tmzn_1719_pt", precision = 5, scale = 2)
    private BigDecimal tmzn1719Pt;

    @Column(name = "tmzn_2022_pt", precision = 5, scale = 2)
    private BigDecimal tmzn2022Pt;

    @Column(name = "resd_pop_cnt")
    private Long resdPopCnt;
}
