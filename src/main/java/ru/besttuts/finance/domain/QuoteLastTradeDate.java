package ru.besttuts.finance.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
@Entity
@Table(name = "quote_last_trade_date")
public class QuoteLastTradeDate {

    @Id
    @Column(length = 16, nullable = false)
    private String symbol;

    @Column(length = 4, nullable = false)
    @Enumerated(EnumType.STRING)
    private Code code;

    @Column(name = "last_trade_date")
    private Date lastTradeDate;

    protected QuoteLastTradeDate() {}

    public QuoteLastTradeDate(Code code, String symbol, Date lastTradeDate) {
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

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
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
