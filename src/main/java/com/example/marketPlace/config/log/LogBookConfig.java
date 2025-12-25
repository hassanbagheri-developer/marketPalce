package com.example.marketPlace.config.log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.RequestFilters;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.core.StreamHttpLogWriter;
import org.zalando.logbook.json.CompactingJsonBodyFilter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.QueryFilters.replaceQuery;
import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
public class LogBookConfig {

    @Bean
    public Logbook logbook(HeaderFilter headerFilter) throws FileNotFoundException {
        PrintStream fileOut = new PrintStream(new FileOutputStream("logs/marketPlace_elastic_search.log", true));
        headerFilter=HeaderFilter.none();
        return Logbook.builder()
                .condition(exclude(request ->
                        request.getPath().startsWith("/swagger-ui") ||
                                request.getPath().startsWith("/v3/api-docs") ||
                                request.getPath().startsWith("/login") ||
                                request.getPath().startsWith("/api/files/upload") ||
                                request.getPath().startsWith("/public/files/upload") ||
                                request.getPath().startsWith("/public/files/convert") ||
                                request.getPath().startsWith("/public/files/**") ||
                                request.getPath().startsWith("/actuator")))
                .requestFilter(RequestFilters.replaceBody(message -> contentType("audio/*").test(message) ? "mmh mmh mmh mmh" : null))
                .responseFilter(ResponseFilters.replaceBody(message -> contentType("*/*-stream").test(message) ? "It just keeps going and going..." : null))
//                .queryFilter(accessToken())
                .queryFilter(replaceQuery("password", "<secret>"))
//                .headerFilter(authorization())
//                .headerFilter(eachHeader("X-Secret"::equalsIgnoreCase, "<secret>"))
                .bodyFilter(jsonPath("$.password").replace("unknown"))
                .headerFilter(headerFilter)
//                .bodyFilter(jsonPath("$.address").replace("X"))
//                .bodyFilter(jsonPath("$.name").replace(compile("^(\\w).+"), "$1."))
//                .bodyFilter(jsonPath("$.friends.*.name").replace(compile("^(\\w).+"), "$1."))
//                .bodyFilter(jsonPath("$.grades.*").replace(1.0))
//                .bodyFilter(jsonPath("$.studentName").delete())//remove a parameter from body
                .sink(new DefaultSink(
                        new PrincipalHttpLogFormatter(new JsonHttpLogFormatter()),
                        new StreamHttpLogWriter(fileOut)
                ))
                .bodyFilter(new CompactingJsonBodyFilter())
                .build();

    }
}
