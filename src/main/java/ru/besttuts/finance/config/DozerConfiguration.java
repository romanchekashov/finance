package ru.besttuts.finance.config;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author romanchekashov
 * @since 22.10.2016
 */
@Configuration
public class DozerConfiguration {

    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozerBean() {
        List<String> mappingFiles = Arrays.asList(
                "dozer-configration-mapping.xml"
        );

        DozerBeanMapper dozerBean = new DozerBeanMapper();
        dozerBean.setMappingFiles(mappingFiles);
        return dozerBean;
    }
}
