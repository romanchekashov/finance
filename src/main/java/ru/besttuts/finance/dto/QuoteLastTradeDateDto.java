package ru.besttuts.finance.dto;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
public class QuoteLastTradeDateDto {

    private String code;
    private String symbol;
    private long lastTradeDate;

    protected QuoteLastTradeDateDto() {}

    public QuoteLastTradeDateDto(String code, String symbol, long lastTradeDate) {
        this.code = code;
        this.symbol = symbol;
        this.lastTradeDate = lastTradeDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(long lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }
}
