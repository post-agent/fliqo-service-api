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
@Table(name = "tb_card_sales")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardSales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "store_name", length = 255)
    private String storeName;

    @Column(name = "rgst_pk", length = 20)
    private String rgstPk;

    @Column(name = "base_ym", length = 10)
    private String baseYm;

    @Column(name = "pnu", length = 255)
    private String pnu;

    @Column(name = "xpoint", length = 255)
    private String xpoint;

    @Column(name = "ypoint", length = 255)
    private String ypoint;

    @Column(name = "std_ind_type_cd", length = 255)
    private String stdIndTypeCd;

    @Column(name = "std_ind_type_nm", length = 255)
    private String stdIndTypeNm;

    @Column(name = "sum_amt", precision = 38, scale = 2)
    private BigDecimal sumAmt;

    @Column(name = "sum_cnt", precision = 38, scale = 2)
    private BigDecimal sumCnt;

    @Column(name = "mean_amt", precision = 38, scale = 2)
    private BigDecimal meanAmt;

    @Column(name = "median_amt", precision = 38, scale = 2)
    private BigDecimal medianAmt;

    @Column(name = "mean_age", precision = 38, scale = 2)
    private BigDecimal meanAge;

    @Column(name = "median_age", precision = 38, scale = 2)
    private BigDecimal medianAge;

    // 성별/연령대별 매출액
    @Column(name = "male_10_amt", precision = 38, scale = 2)
    private BigDecimal male10Amt;

    @Column(name = "male_20_amt", precision = 38, scale = 2)
    private BigDecimal male20Amt;

    @Column(name = "male_30_amt", precision = 38, scale = 2)
    private BigDecimal male30Amt;

    @Column(name = "male_40_amt", precision = 38, scale = 2)
    private BigDecimal male40Amt;

    @Column(name = "male_50_amt", precision = 38, scale = 2)
    private BigDecimal male50Amt;

    @Column(name = "male_60_amt", precision = 38, scale = 2)
    private BigDecimal male60Amt;

    @Column(name = "female_10_amt", precision = 38, scale = 2)
    private BigDecimal female10Amt;

    @Column(name = "female_20_amt", precision = 38, scale = 2)
    private BigDecimal female20Amt;

    @Column(name = "female_30_amt", precision = 38, scale = 2)
    private BigDecimal female30Amt;

    @Column(name = "female_40_amt", precision = 38, scale = 2)
    private BigDecimal female40Amt;

    @Column(name = "female_50_amt", precision = 38, scale = 2)
    private BigDecimal female50Amt;

    @Column(name = "female_60_amt", precision = 38, scale = 2)
    private BigDecimal female60Amt;

    // 요일별 매출액
    @Column(name = "mon_amt", precision = 38, scale = 2)
    private BigDecimal monAmt;

    @Column(name = "tue_amt", precision = 38, scale = 2)
    private BigDecimal tueAmt;

    @Column(name = "wed_amt", precision = 38, scale = 2)
    private BigDecimal wedAmt;

    @Column(name = "thu_amt", precision = 38, scale = 2)
    private BigDecimal thuAmt;

    @Column(name = "fri_amt", precision = 38, scale = 2)
    private BigDecimal friAmt;

    @Column(name = "sat_amt", precision = 38, scale = 2)
    private BigDecimal satAmt;

    @Column(name = "sun_amt", precision = 38, scale = 2)
    private BigDecimal sunAmt;

    // 시간대별 매출액
    @Column(name = "time_0104_amt", precision = 38, scale = 2)
    private BigDecimal time0104Amt;

    @Column(name = "time_0510_amt", precision = 38, scale = 2)
    private BigDecimal time0510Amt;

    @Column(name = "time_1114_amt", precision = 38, scale = 2)
    private BigDecimal time1114Amt;

    @Column(name = "time_1517_amt", precision = 38, scale = 2)
    private BigDecimal time1517Amt;

    @Column(name = "time_1819_amt", precision = 38, scale = 2)
    private BigDecimal time1819Amt;

    @Column(name = "time_2021_amt", precision = 38, scale = 2)
    private BigDecimal time2021Amt;

    @Column(name = "time_2224_amt", precision = 38, scale = 2)
    private BigDecimal time2224Amt;

    @Column(name = "franchise_amt", precision = 38, scale = 2)
    private BigDecimal franchiseAmt;

    @Column(name = "in_sgg_amt", precision = 38, scale = 2)
    private BigDecimal inSggAmt;

    @Column(name = "out_sgg_amt", precision = 38, scale = 2)
    private BigDecimal outSggAmt;

    // 성별/연령대별 건수
    @Column(name = "male_10_cnt", precision = 38, scale = 2)
    private BigDecimal male10Cnt;

    @Column(name = "male_20_cnt", precision = 38, scale = 2)
    private BigDecimal male20Cnt;

    @Column(name = "male_30_cnt", precision = 38, scale = 2)
    private BigDecimal male30Cnt;

    @Column(name = "male_40_cnt", precision = 38, scale = 2)
    private BigDecimal male40Cnt;

    @Column(name = "male_50_cnt", precision = 38, scale = 2)
    private BigDecimal male50Cnt;

    @Column(name = "male_60_cnt", precision = 38, scale = 2)
    private BigDecimal male60Cnt;

    @Column(name = "female_10_cnt", precision = 38, scale = 2)
    private BigDecimal female10Cnt;

    @Column(name = "female_20_cnt", precision = 38, scale = 2)
    private BigDecimal female20Cnt;

    @Column(name = "female_30_cnt", precision = 38, scale = 2)
    private BigDecimal female30Cnt;

    @Column(name = "female_40_cnt", precision = 38, scale = 2)
    private BigDecimal female40Cnt;

    @Column(name = "female_50_cnt", precision = 38, scale = 2)
    private BigDecimal female50Cnt;

    @Column(name = "female_60_cnt", precision = 38, scale = 2)
    private BigDecimal female60Cnt;

    // 요일별 건수
    @Column(name = "mon_cnt", precision = 38, scale = 2)
    private BigDecimal monCnt;

    @Column(name = "tue_cnt", precision = 38, scale = 2)
    private BigDecimal tueCnt;

    @Column(name = "wed_cnt", precision = 38, scale = 2)
    private BigDecimal wedCnt;

    @Column(name = "thu_cnt", precision = 38, scale = 2)
    private BigDecimal thuCnt;

    @Column(name = "fri_cnt", precision = 38, scale = 2)
    private BigDecimal friCnt;

    @Column(name = "sat_cnt", precision = 38, scale = 2)
    private BigDecimal satCnt;

    @Column(name = "sun_cnt", precision = 38, scale = 2)
    private BigDecimal sunCnt;

    // 시간대별 건수
    @Column(name = "time_0104_cnt", precision = 38, scale = 2)
    private BigDecimal time0104Cnt;

    @Column(name = "time_0510_cnt", precision = 38, scale = 2)
    private BigDecimal time0510Cnt;

    @Column(name = "time_1114_cnt", precision = 38, scale = 2)
    private BigDecimal time1114Cnt;

    @Column(name = "time_1517_cnt", precision = 38, scale = 2)
    private BigDecimal time1517Cnt;

    @Column(name = "time_1819_cnt", precision = 38, scale = 2)
    private BigDecimal time1819Cnt;

    @Column(name = "time_2021_cnt", precision = 38, scale = 2)
    private BigDecimal time2021Cnt;

    @Column(name = "time_2224_cnt", precision = 38, scale = 2)
    private BigDecimal time2224Cnt;

    @Column(name = "franchise_cnt", precision = 38, scale = 2)
    private BigDecimal franchiseCnt;

    @Column(name = "in_sgg_cnt", precision = 38, scale = 2)
    private BigDecimal inSggCnt;

    @Column(name = "out_sgg_cnt", precision = 38, scale = 2)
    private BigDecimal outSggCnt;
}
