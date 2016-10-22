package ru.besttuts.finance.rest;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.besttuts.finance.dao.QuoteLastTradeDateRepository;
import ru.besttuts.finance.domain.QuoteLastTradeDate;
import ru.besttuts.finance.dto.QuoteLastTradeDateDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/finance/api", headers = {"Accept=application/json"})
public class FinanceRestController {

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    QuoteLastTradeDateRepository quoteLastTradeDateRepository;

    @RequestMapping("/quote-last-trade-date")
    public List<QuoteLastTradeDateDto> quoteLastTradeDates() {

        quoteLastTradeDateRepository.save(new QuoteLastTradeDate("BZ", "BZX16.NYM", new Date(1475193600000L)));
        quoteLastTradeDateRepository.save(new QuoteLastTradeDate("BZ", "BZZ16.NYM", new Date(1477872000000L)));
        quoteLastTradeDateRepository.save(new QuoteLastTradeDate("BZ", "BZF17.NYM", new Date(1480464000000L)));
        quoteLastTradeDateRepository.save(new QuoteLastTradeDate("BZ", "BZG17.NYM", new Date(1482969600000L)));

        List<QuoteLastTradeDate> quoteLastTradeDates = (List<QuoteLastTradeDate>) quoteLastTradeDateRepository.findAll();
        List<QuoteLastTradeDateDto> quoteLastTradeDateDtos = new ArrayList<>();

        for (QuoteLastTradeDate quoteLastTradeDate: quoteLastTradeDates){
            quoteLastTradeDateDtos.add(dozerBeanMapper.map(quoteLastTradeDate, QuoteLastTradeDateDto.class));
        }

        return quoteLastTradeDateDtos;
    }

}