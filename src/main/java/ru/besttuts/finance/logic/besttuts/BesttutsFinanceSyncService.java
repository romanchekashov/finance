package ru.besttuts.finance.logic.besttuts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.besttuts.model.BesttutsFinanceQuoteLastTradeDate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author romanchekashov
 * @since 29.10.2016
 */
@Service
public class BesttutsFinanceSyncService {
    private static final Logger LOG = LoggerFactory.getLogger(BesttutsFinanceSyncService.class);

    private static final String HTTP_API = "http://localhost:8080/finance-rest/";

    @Autowired
    DozerBeanMapper dozerBeanMapper;
    @Autowired
    private QuoteLastTradeDateRepository quoteLastTradeDateRepository;

    protected BesttutsFinanceSyncService() {}

    @Async
    public void sync(){
        List<QuoteLastTradeDate> quoteLastTradeDates = (List<QuoteLastTradeDate>) quoteLastTradeDateRepository.findAll();
//                .findByLastTradeDateGreaterThan(LocalDate.of(1970, 1, 1), new Sort("code", "last_trade_date"));
        LOG.info("[sync]: fetched from DB quoteLastTradeDates.size = {}", quoteLastTradeDates.size());

        List<BesttutsFinanceQuoteLastTradeDate> besttutsFinanceQuoteLastTradeDates = new ArrayList<>();

        int currentYear = Year.now().getValue();
        quoteLastTradeDates.stream().forEach(quote -> {
            if(currentYear == quote.getLastTradeDate().getYear()){
                besttutsFinanceQuoteLastTradeDates.add(dozerBeanMapper
                        .map(quote, BesttutsFinanceQuoteLastTradeDate.class));
            }
        });

        BesttutsFinanceRetrofitService retrofitService = createBesttutsFinanceRetrofitService();
        try {
            List<String> savedIds = retrofitService.save(
                    besttutsFinanceQuoteLastTradeDates).execute().body();
            LOG.info("[sync]: savedIds = {}", savedIds.toString());
        } catch (IOException e) {
            LOG.error("[sync]: not complete cause {}", e.getMessage());
        }
    }

    private BesttutsFinanceRetrofitService createBesttutsFinanceRetrofitService(){
        ObjectMapper mapper = new ObjectMapper();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_API)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        return retrofit.create(BesttutsFinanceRetrofitService.class);
    }
}
