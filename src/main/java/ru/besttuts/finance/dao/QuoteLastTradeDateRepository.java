package ru.besttuts.finance.dao;

import org.springframework.data.repository.CrudRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
public interface QuoteLastTradeDateRepository extends CrudRepository<QuoteLastTradeDate, String> {

}
