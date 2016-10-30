package ru.besttuts.finance.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.Code;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.logic.yahoo.FetchQuoteLastTradeDatesByCodeRunnable;
import ru.besttuts.finance.logic.yahoo.YahooFinanceRetrofitService;
import ru.besttuts.finance.logic.yahoo.deserializer.YahooFuturesDeserializer;
import ru.besttuts.finance.logic.yahoo.model.YahooFutures;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 * @author romanchekashov
 * @since 23.10.2016
 */
public class FetchQuoteLastTradeDatesByCodeRunnableTest {
    private final String urlFutureExpected = "http://finance.yahoo.com/quote/BZ%3DF/futures?p=BZ%3DF";
    private final Code codeBZ = Code.BZ;

//    "https://query1.finance.yahoo.com/v10/finance/quoteSummary/BZ=F?formatted=true&crumb=e.qopTac4Gd&lang=en-US&region=US&modules=futuresChain&corsDomain=finance.yahoo.com"
    FetchQuoteLastTradeDatesByCodeRunnable runnable;
    ExecutorService pool;

    @Mock
    QuoteLastTradeDateRepository mockQuoteLastTradeDateRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        runnable = new FetchQuoteLastTradeDatesByCodeRunnable(codeBZ, mockQuoteLastTradeDateRepository, createYahooFinanceService());
        pool = Executors.newCachedThreadPool();

        Mockito.when(mockQuoteLastTradeDateRepository.save(Matchers.any(QuoteLastTradeDate.class)))
                .thenReturn(new QuoteLastTradeDate(codeBZ, codeBZ.toString(), LocalDate.now()));
    }

    @Test
    public void test_url_formatter() {
        assertEquals(urlFutureExpected, runnable.createUrlFutures(codeBZ.toString()));
    }

    @Test
    public void test_run() {
        runnable.run();
    }

    private static final String HTTP_QUERY_YAHOOAPIS_COM_V10_FINANCE = "https://query1.finance.yahoo.com/v10/finance/";

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
