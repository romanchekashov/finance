package ru.besttuts.finance.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.besttuts.finance.domain.Code;
import ru.besttuts.finance.domain.QuoteLastTradeDate;

import java.time.LocalDate;
import java.util.List;

/**
 * @author romanchekashov
 * @since 12.11.2016
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class QuoteLastTradeDateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuoteLastTradeDateRepository repository;

    @Test
    public void testExample() throws Exception {
        int YEAR = 2016;
        this.entityManager.persist(new QuoteLastTradeDate(Code.BZ, "BZZ16.NYM", LocalDate.of(2016, 11, 30)));
        this.entityManager.persist(new QuoteLastTradeDate(Code.BZ, "BZF17.NYM", LocalDate.of(2016, 12, 29)));
        this.entityManager.persist(new QuoteLastTradeDate(Code.BZ, "BZG17.NYM", LocalDate.of(2017, 1, 1)));
        List<QuoteLastTradeDate> quotes = this.repository.findByYear(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31));
        quotes.stream().forEach(q -> {
            assertThat(q.getLastTradeDate().getYear()).isEqualTo(YEAR);
        });
    }
}
