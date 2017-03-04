package ru.besttuts.finance.logic.yahoo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.Code;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.besttuts.BesttutsFinanceSyncService;
import ru.besttuts.finance.logic.yahoo.deserializer.YahooFuturesDeserializer;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author romanchekashov
 * @since 25.10.2016
 */
@Service
public class ParseYahooForQuoteLastTradeDateService {
    private static final Logger LOG = LoggerFactory.getLogger(ParseYahooForQuoteLastTradeDateService.class);

    private static final String HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE = "https://query1.finance.yahoo.com/v10/finance/";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private QuoteLastTradeDateRepository quoteLastTradeDateRepository;
    @Autowired
    private BesttutsFinanceSyncService besttutsFinanceSyncService;

    protected ParseYahooForQuoteLastTradeDateService() {}

    @Async
    public void execute(){
        LOG.info("execute at {}", Calendar.getInstance().getTime());

        fetchQuotes(Arrays.asList(Code.values()));

        List<QuoteLastTradeDate> quoteLastTradeDates = quoteLastTradeDateRepository
                .findByLastTradeDateLessThanOrderByLastTradeDate(LocalDate.of(1970, 1, 1));

        Set<Code> codesToFetchAgain = new HashSet<>();
        for (QuoteLastTradeDate quoteLastTradeDate: quoteLastTradeDates){
            codesToFetchAgain.add(quoteLastTradeDate.getCode());
        }

        if (!codesToFetchAgain.isEmpty()){
            fetchQuotes(new ArrayList<>(codesToFetchAgain));
        }

        LOG.info("Parsing END at {}", Calendar.getInstance().getTime());
    }

    private void fetchQuotes(List<Code> codesToFetchAgain){
        ExecutorService pool = Executors.newSingleThreadExecutor();
        YahooFinanceRetrofitService yahooFinanceService = createYahooFinanceService();
        final CountDownLatch latch = new CountDownLatch(codesToFetchAgain.size());
        codesToFetchAgain.stream().forEach(code -> {
            FetchQuoteLastTradeDatesByCodeRunnable runnable = new FetchQuoteLastTradeDatesByCodeRunnable(
                    code, quoteLastTradeDateRepository, yahooFinanceService);
            runnable.setLatch(latch);
            pool.execute(runnable);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error("fetchQuotes not complete cause {}", e.getMessage());
        }
    }

    private YahooFinanceRetrofitService createYahooFinanceService(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(YahooFutures.class, new YahooFuturesDeserializer(YahooFutures.class));
        mapper.registerModule(module);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        return retrofit.create(YahooFinanceRetrofitService.class);
    }
}
