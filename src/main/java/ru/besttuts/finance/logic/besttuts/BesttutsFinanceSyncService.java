package ru.besttuts.finance.logic.besttuts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

//    private static final String HTTP_API = "http://localhost:8080/finance-rest/";
    private static final String HTTP_API = "http://finance.besttuts.ru/";

    @Autowired
    DozerBeanMapper dozerBeanMapper;
    @Autowired
    private QuoteLastTradeDateRepository quoteLastTradeDateRepository;

    protected BesttutsFinanceSyncService() {}

    @Async
    public void sync(){

        int currentYear = Year.now().getValue();
        List<QuoteLastTradeDate> quoteLastTradeDates = quoteLastTradeDateRepository
                .findByYear(LocalDate.of(currentYear, 1, 1), LocalDate.of(currentYear, 12, 31));
        LOG.info("[sync]: fetched from DB quoteLastTradeDates.size = {}", quoteLastTradeDates.size());

        List<BesttutsFinanceQuoteLastTradeDate> besttutsFinanceQuoteLastTradeDates = new ArrayList<>();

        quoteLastTradeDates.stream().forEach(quote -> {
            BesttutsFinanceQuoteLastTradeDate besttutsFinanceQuote = new BesttutsFinanceQuoteLastTradeDate();
            besttutsFinanceQuote.setCode(quote.getCode());
            besttutsFinanceQuote.setSymbol(quote.getSymbol());
            besttutsFinanceQuote.setLastTradeDate(java.sql.Date.valueOf(quote.getLastTradeDate()));

            besttutsFinanceQuoteLastTradeDates.add(besttutsFinanceQuote);
        });

        BesttutsFinanceRetrofitService retrofitService = createBesttutsFinanceRetrofitService();
        try {
            List<String> savedIds = retrofitService.save(besttutsFinanceQuoteLastTradeDates).execute().body();
            LOG.info("[sync]: savedIds = {}", savedIds.toString());
        } catch (IOException e) {
            LOG.error("[sync]: not complete cause {}", e.getMessage());
        }

//        Call<List<String>> call = retrofitService.save(besttutsFinanceQuoteLastTradeDates);
//        call.enqueue(new Callback<List<String>>() {
//            @Override
//            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
//                LOG.info("[sync]: savedIds = {}", response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<List<String>> call, Throwable throwable) {
//                LOG.error("[sync]: not complete cause {}", throwable.getMessage());
//            }
//        });
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
