package ru.besttuts.finance.logic.yahoo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author romanchekashov
 * @since 24.10.2016
 */
public class YahooFutures {
    private List<String> futures = new ArrayList<>();

    public YahooFutures() {}

    public List<String> getFutures() {
        return futures;
    }

    public void setFutures(List<String> futures) {
        this.futures = futures;
    }
}
