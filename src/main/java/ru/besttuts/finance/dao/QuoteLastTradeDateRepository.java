package ru.besttuts.finance.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
public interface QuoteLastTradeDateRepository extends JpaRepository<QuoteLastTradeDate, String> {

    @Query("select q from QuoteLastTradeDate q where q.lastTradeDate >= ?1 and q.lastTradeDate <= ?2 order by q.lastTradeDate")
    List<QuoteLastTradeDate> findByYear(LocalDate minDate, LocalDate maxDate);

    List<QuoteLastTradeDate> findByLastTradeDateLessThanOrderByLastTradeDate(LocalDate lastTradeDate);
}
