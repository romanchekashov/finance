package ru.besttuts.finance.logic.yahoo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

/**
 * @author romanchekashov
 * @since 24.10.2016
 */
public interface YahooFinanceRetrofitService {

    @GET("quoteSummary/{code}=F?formatted=true&crumb=e.qopTac4Gd&lang=en-US&region=US&modules=futuresChain&corsDomain=finance.yahoo.com")
    Call<YahooFutures> yahooFuturesCall(@Path("code") String code);
}
