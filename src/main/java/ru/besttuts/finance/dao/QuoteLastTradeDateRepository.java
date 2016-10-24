package ru.besttuts.finance.dao;

import org.springframework.data.repository.CrudRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;

import java.util.Date;
import java.util.List;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
public interface QuoteLastTradeDateRepository extends CrudRepository<QuoteLastTradeDate, String> {

    List<QuoteLastTradeDate> findByLastTradeDateGreaterThanOrderByLastTradeDate(Date lastTradeDate);

    List<QuoteLastTradeDate> findByLastTradeDateLessThanOrderByLastTradeDate(Date lastTradeDate);
}
