package ru.besttuts.finance.logic.besttuts;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ru.besttuts.finance.logic.besttuts.model.BesttutsFinanceQuoteLastTradeDate;

import java.util.List;

/**
 * @author romanchekashov
 * @since 24.10.2016
 */
public interface BesttutsFinanceRetrofitService {

    @GET("api/quote-last-trade-dates")
    Call<BesttutsFinanceQuoteLastTradeDate> findAll();

    @POST("api/quote-last-trade-dates")
    Call<List<String>> save(@Body List<BesttutsFinanceQuoteLastTradeDate> quoteLastTradeDates);
}
