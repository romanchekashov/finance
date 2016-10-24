package ru.besttuts.finance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.logic.ParseYahooFinanceForQuoteLastTradeDate;

import java.util.Calendar;

/**
 * @author romanchekashov
 * @since 22.10.2016
 *
 * @link http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html
 * @link http://stackoverflow.com/questions/26147044/spring-cron-expression-for-every-day-101am
 */
@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    QuoteLastTradeDateRepository quoteLastTradeDateRepository;

    /**
     * second, minute, hour, day of month, month, day(s) of week
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void parseYahooFinanceForQuoteLastTradeDate() {
        LOG.info("ScheduledTasks[parseYahooFinanceForQuoteLastTradeDate] {}",
                Calendar.getInstance().getTime());

        new ParseYahooFinanceForQuoteLastTradeDate(quoteLastTradeDateRepository).execute();
    }


}
