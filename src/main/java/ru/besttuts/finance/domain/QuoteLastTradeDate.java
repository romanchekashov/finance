package ru.besttuts.finance.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
@Entity
public class QuoteLastTradeDate {

    @Id
    @Column(length = 16, nullable = false)
    private String symbol;

    @Column(length = 8, nullable = false)
    private String code;

    private Date lastTradeDate;

    protected QuoteLastTradeDate() {}

    public QuoteLastTradeDate(String code, String symbol, Date lastTradeDate) {
        this.code = code;
        this.symbol = symbol;
        this.lastTradeDate = lastTradeDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(Date lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    @Override
    public String toString() {
        return "QuoteLastTradeDate{" +
                "code='" + code + '\'' +
                ", symbol='" + symbol + '\'' +
                ", lastTradeDate=" + lastTradeDate +
                '}';
    }
}
