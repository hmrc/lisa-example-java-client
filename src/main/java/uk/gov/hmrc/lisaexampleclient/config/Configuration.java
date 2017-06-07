/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.lisaexampleclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;
import java.io.IOException;

import static java.util.Collections.singletonList;

@org.springframework.context.annotation.Configuration
public class Configuration {

    private static final String HMRC_ACCEPT = "application/vnd.hmrc.1.0+json";

    @Bean
    @Named("plainRestTemplate")
    public RestTemplate plainRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.registerModule(new JSR310Module());
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestInterceptor acceptHeaderPdf = new AcceptHeaderHttpRequestInterceptor(HMRC_ACCEPT);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(singletonList(acceptHeaderPdf));
        return restTemplate;
    }

    static class AcceptHeaderHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private final String headerValue;

        public AcceptHeaderHttpRequestInterceptor(String headerValue) {
            this.headerValue = headerValue;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
            requestWrapper.getHeaders().setAccept(singletonList(MediaType.valueOf(headerValue)));

            return execution.execute(requestWrapper, body);
        }
    }
}
