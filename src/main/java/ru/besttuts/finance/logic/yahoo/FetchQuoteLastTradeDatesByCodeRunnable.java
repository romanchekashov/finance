package ru.besttuts.finance.logic.yahoo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.Code;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

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
    YahooFinanceRetrofitService service;
    CountDownLatch latch;

    private Code code;

    protected FetchQuoteLastTradeDatesByCodeRunnable() {}

    public FetchQuoteLastTradeDatesByCodeRunnable(Code code, QuoteLastTradeDateRepository quoteLastTradeDateRepository, YahooFinanceRetrofitService service) {
        this.code = code;
        this.quoteLastTradeDateRepository = quoteLastTradeDateRepository;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            YahooFutures yahooFutures = fetchYahooFutures(code);
            for (String symbol: yahooFutures.getFutures()) {
                try {
                    Document docQuote = Jsoup.connect(createUrlFuture(symbol)).get();
                    Elements settlementDate = docQuote.select("td[data-test='SETTLEMENT_DATE-value']");
                    Date date = dateFormat.parse(settlementDate.html().trim());
                    LOG.info("save symbol {} with settlementDate {}.", symbol, settlementDate.html());
                    quoteLastTradeDateRepository.save(new QuoteLastTradeDate(code, symbol, date));
                } catch (ParseException e) {
                    LOG.warn("cannot save symbol {} because {}.", symbol, e.getMessage());
                } catch (IOException e) {
                    LOG.warn("Cannot fetch settlementDate of: {} because {}.", symbol, e.getMessage());
                    quoteLastTradeDateRepository.save(new QuoteLastTradeDate(code, symbol, new Date(0)));
                }
            }
        } catch (IOException e) {
            LOG.warn("Cannot fetch code: {} because {}.", code, e.getMessage());
            quoteLastTradeDateRepository.save(new QuoteLastTradeDate(code, String.valueOf(code), new Date(0)));
        }

        if(null != latch) latch.countDown();
    }

    public String createUrlFuture(String symbol){
        return String.format(urlFuture, symbol, symbol);
    }

    public String createUrlFutures(String code){
        return String.format(urlFutures, code, code);
    }

    public YahooFutures fetchYahooFutures(Code code) throws IOException {
        return service.yahooFuturesCall(String.valueOf(code)).execute().body();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
