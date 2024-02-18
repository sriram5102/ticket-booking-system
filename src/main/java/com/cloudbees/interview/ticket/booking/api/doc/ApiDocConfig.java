package com.cloudbees.interview.ticket.booking.api.doc;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocConfig {

    @Bean
    public void configure() {
        SpringDocUtils.getConfig().addHiddenRestControllers(BasicErrorController.class);
    }
}
