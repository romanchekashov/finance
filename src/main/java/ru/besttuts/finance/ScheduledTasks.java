package ru.besttuts.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.logic.FetchQuoteLastTradeDatesByCodeRunnable;
import ru.besttuts.finance.logic.yahoo.YahooFinanceService;
import ru.besttuts.finance.logic.yahoo.deserializer.YahooFuturesDeserializer;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author romanchekashov
 * @since 22.10.2016
 *
 * @link http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html
 */
@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final String HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE = "https://query1.finance.yahoo.com/v10/finance/";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    QuoteLastTradeDateRepository quoteLastTradeDateRepository;
//    private final long timeRate = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

    @Scheduled(fixedRate = 600000)
    public void reportCurrentTime() {
        LOG.info("The time is now {}", dateFormat.format(new Date()));

        String[] codes = {"BZ", "CL", "GC", "SI", "PL", "PA", "HG", "NG", "C", "S", "ZW", "CC", "KC", "CT", "LB", "OJ", "SB"};
        ExecutorService pool = Executors.newCachedThreadPool();
        for (String code: codes){
            pool.execute(new FetchQuoteLastTradeDatesByCodeRunnable(code, quoteLastTradeDateRepository, createYahooFinanceService()));
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
