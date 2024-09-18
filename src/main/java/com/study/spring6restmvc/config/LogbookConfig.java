package com.study.spring6restmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

@Configuration
public class LogbookConfig {

    @Bean
    public Sink logbookLogStash() {
        var logFormatter = new JsonHttpLogFormatter();
        return new LogstashLogbackSink(logFormatter);
    }
}
