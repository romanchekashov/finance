package ru.besttuts.finance.rest;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.dto.QuoteLastTradeDateDto;
import ru.besttuts.finance.logic.besttuts.BesttutsFinanceSyncService;
import ru.besttuts.finance.logic.yahoo.ParseYahooForQuoteLastTradeDateService;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FinanceRestController {

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    QuoteLastTradeDateRepository quoteLastTradeDateRepository;
    @Autowired
    ParseYahooForQuoteLastTradeDateService lastTradeDateService;
    @Autowired
    private BesttutsFinanceSyncService besttutsFinanceSyncService;

    @RequestMapping(value = "/quote-last-trade-date", headers = {"Accept=application/json"})
    public List<QuoteLastTradeDateDto> quoteLastTradeDates() {

        int currentYear = Year.now().getValue();
        List<QuoteLastTradeDate> quoteLastTradeDates = quoteLastTradeDateRepository
                .findByYear(LocalDate.of(currentYear, 1, 1), LocalDate.of(currentYear, 12, 31));
        List<QuoteLastTradeDateDto> quoteLastTradeDateDtos = new ArrayList<>();

        quoteLastTradeDates.stream().forEach(quote -> {
            QuoteLastTradeDateDto dto = new QuoteLastTradeDateDto();
            dto.setCode(quote.getCode().toString());
            dto.setSymbol(quote.getSymbol());
            dto.setLastTradeDate(quote.getLastTradeDate().toEpochDay());

            quoteLastTradeDateDtos.add(dto);
        });

        return quoteLastTradeDateDtos;
    }

    @RequestMapping("/parse-yahoo-for-quote-last-trade-date")
    public String parseYahooForQuoteLastTradeDate() {

        lastTradeDateService.execute();

        return "OK";
    }

    @RequestMapping("/sync-finance-besttuts-ru")
    public String syncFinanceBesttutsRu() {

        besttutsFinanceSyncService.sync();

        return "OK";
    }

}