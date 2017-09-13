package com.ems.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class RisingLevels {
    private Double DlastOneMonthTotal;
    private BigDecimal risingLevelsOfAYearAgo;
    private BigDecimal risingLevelsOfAMonthAgo;

    public RisingLevels() {
        super();
    }

    public RisingLevels(Double dlastOneMonthTotal, BigDecimal risingLevelsOfAYearAgo, BigDecimal risingLevelsOfAMonthAgo) {
        DlastOneMonthTotal = dlastOneMonthTotal;
        this.risingLevelsOfAYearAgo = risingLevelsOfAYearAgo;
        this.risingLevelsOfAMonthAgo = risingLevelsOfAMonthAgo;
    }

    public Double getDlastOneMonthTotal() {
        return DlastOneMonthTotal;
    }

    public void setDlastOneMonthTotal(Double dlastOneMonthTotal) {
        DlastOneMonthTotal = dlastOneMonthTotal;
    }

    public BigDecimal getRisingLevelsOfAYearAgo() {
        return risingLevelsOfAYearAgo;
    }

    public void setRisingLevelsOfAYearAgo(BigDecimal risingLevelsOfAYearAgo) {
        this.risingLevelsOfAYearAgo = risingLevelsOfAYearAgo;
    }

    public BigDecimal getRisingLevelsOfAMonthAgo() {
        return risingLevelsOfAMonthAgo;
    }

    public void setRisingLevelsOfAMonthAgo(BigDecimal risingLevelsOfAMonthAgo) {
        this.risingLevelsOfAMonthAgo = risingLevelsOfAMonthAgo;
    }

    @Override
    public String toString() {
        return "RisingLevels{" +
                "DlastOneMonthTotal=" + DlastOneMonthTotal +
                ", risingLevelsOfAYearAgo=" + risingLevelsOfAYearAgo +
                ", risingLevelsOfAMonthAgo=" + risingLevelsOfAMonthAgo +
                '}';
    }

}
