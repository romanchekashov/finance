package ru.besttuts.finance.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.Code;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.yahoo.YahooFinanceService;
import ru.besttuts.finance.logic.yahoo.deserializer.YahooFuturesDeserializer;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author romanchekashov
 * @since 25.10.2016
 */
public class ParseYahooFinanceForQuoteLastTradeDate {
    private static final Logger LOG = LoggerFactory.getLogger(ParseYahooFinanceForQuoteLastTradeDate.class);

    private static final String HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE = "https://query1.finance.yahoo.com/v10/finance/";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    QuoteLastTradeDateRepository quoteLastTradeDateRepository;

    public ParseYahooFinanceForQuoteLastTradeDate(
            QuoteLastTradeDateRepository quoteLastTradeDateRepository) {
        this.quoteLastTradeDateRepository = quoteLastTradeDateRepository;
    }

    public void execute(){
        LOG.info("execute at {}", Calendar.getInstance().getTime());

        List<QuoteLastTradeDate> quoteLastTradeDates = (List<QuoteLastTradeDate>)
                quoteLastTradeDateRepository.findAll();
        if(quoteLastTradeDates.isEmpty()){
            fetchQuotesWithAllCodes();
        }

        quoteLastTradeDates = quoteLastTradeDateRepository
                .findByLastTradeDateLessThanOrderByLastTradeDate(new Date(10));

        Set<Code> codesToFetchAgain = new HashSet<>();
        for (QuoteLastTradeDate quoteLastTradeDate: quoteLastTradeDates){
            codesToFetchAgain.add(quoteLastTradeDate.getCode());
        }

        if (codesToFetchAgain.isEmpty()){
            return;
        }

        ExecutorService pool = Executors.newCachedThreadPool();
        YahooFinanceService yahooFinanceService = createYahooFinanceService();
        codesToFetchAgain.stream().forEach(code -> {
            pool.execute(new FetchQuoteLastTradeDatesByCodeRunnable(
                    code, quoteLastTradeDateRepository, yahooFinanceService));
        });
    }


    private void fetchQuotesWithAllCodes(){
        String[] codes = {"BZ", "CL", "GC", "SI", "PL", "PA", "HG", "NG",
                "C", "S", "ZW", "CC", "KC", "CT", "LB", "OJ", "SB"};
        final CountDownLatch latch = new CountDownLatch(codes.length);

        ExecutorService pool = Executors.newCachedThreadPool();
        YahooFinanceService yahooFinanceService = createYahooFinanceService();
        for (String code: codes){
            FetchQuoteLastTradeDatesByCodeRunnable runnable = new FetchQuoteLastTradeDatesByCodeRunnable(
                    Code.valueOf(code), quoteLastTradeDateRepository, yahooFinanceService);
            runnable.setLatch(latch);
            pool.execute(runnable);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error("fetchQuotesWithAllCodes not complete cause {}", e.getMessage());
        }

    }

    private YahooFinanceService createYahooFinanceService(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(YahooFutures.class, new YahooFuturesDeserializer(YahooFutures.class));
        mapper.registerModule(module);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        return retrofit.create(YahooFinanceService.class);
    }
}
