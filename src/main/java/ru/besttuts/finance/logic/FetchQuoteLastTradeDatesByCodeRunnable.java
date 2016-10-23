package ru.besttuts.finance.logic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.yahoo.YahooFinanceService;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author romanchekashov
 * @since 23.10.2016
 */
public class FetchQuoteLastTradeDatesByCodeRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FetchQuoteLastTradeDatesByCodeRunnable.class);

    private static final String urlFutures = "http://finance.yahoo.com/quote/%s%%3DF/futures?p=%s%%3DF";
    private static final String urlFuture = "http://finance.yahoo.com/quote/%s?p=%s";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //2016-10-31

    QuoteLastTradeDateRepository quoteLastTradeDateRepository;
    YahooFinanceService service;

    private String code;

    protected FetchQuoteLastTradeDatesByCodeRunnable() {}

    public FetchQuoteLastTradeDatesByCodeRunnable(String code, QuoteLastTradeDateRepository quoteLastTradeDateRepository, YahooFinanceService service) {
        this.code = code;
        this.quoteLastTradeDateRepository = quoteLastTradeDateRepository;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            YahooFutures yahooFutures = fetchYahooFutures(code);
            for (String symbol: yahooFutures.getFutures()) {
                LOG.info("symbol is: {}", symbol);
                LOG.info("url is: {}", createUrlFuture(symbol));

                Document docQuote = Jsoup.connect(createUrlFuture(symbol)).get();

                try {
                    Elements settlementDate = docQuote.select("td[data-test='SETTLEMENT_DATE-value']");
                    Date date = dateFormat.parse(settlementDate.html().trim());
                    LOG.info("settlementDate is: {}", settlementDate.html());
                    quoteLastTradeDateRepository.save(new QuoteLastTradeDate(code, symbol, date));
                } catch (ParseException e) {
                    LOG.warn("settlementDate is: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createUrlFuture(String symbol){
        return String.format(urlFuture, symbol, symbol);
    }

    public String createUrlFutures(String code){
        return String.format(urlFutures, code, code);
    }

    public YahooFutures fetchYahooFutures(String code) throws IOException {
        return service.yahooFuturesCall(code).execute().body();
    }
}
