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
package uk.gov.hmrc.lisaexampleclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;

public class ServiceConnectorTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    RestTemplate restTemplate;
    @Mock
    ResponseEntity<java.lang.String> response;
    @Captor
    ArgumentCaptor<HttpEntity<String>> entity;
    ServiceConnector sut;
    ObjectMapper mapper = new ObjectMapper();
    static final String jsonResponse = "{\n" +
            "  \"data\": {\n" +
            "    \"lifeEventId\": \"9876543210\",\n" +
            "    \"message\": \"Life Event Created.\"\n" +
            "  },\n" +
            "  \"success\": true,\n" +
            "  \"status\": 201\n" +
            "}";
    ;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new ISO8601DateFormat());
        mapper.registerModule(new JSR310Module());
        sut = new ServiceConnector(restTemplate, mapper);
    }

    @Test
    public void shouldTestGet() throws Exception {
        Optional<String> accessToken = Optional.of("abcdefghijklmno");
        String url = "http://somewhere.over.the.rainbow/stuff";
        given(restTemplate.getForEntity(url, String.class)).willReturn(response);
        given(response.getStatusCode()).willReturn(HttpStatus.OK);
        given(response.getBody()).willReturn(jsonResponse);
        String result = sut.get(url, accessToken);
        assertThat(result, is(jsonResponse));
    }

    @Test
    public void shouldTestGetThrowsUnauthorized() throws Exception {
        Optional<String> accessToken = Optional.of("abcdefghijklmno");
        String url = "http://somewhere.over.the.rainbow/stuff";
        given(restTemplate.getForEntity(url, String.class)).willReturn(response);
        given(response.getStatusCode()).willReturn(HttpStatus.UNAUTHORIZED);
        thrown.expect(UnauthorizedException.class);
        sut.get(url, accessToken);
    }

    @Test
    public void shouldTestPost() throws Exception {
        String url = "http://somewhere.over.the.rainbow/stuff";
        Optional<String> accessToken = Optional.of("abcdefghijklmno");
        String someStringResponse = "someStringResponse";
        given(restTemplate.postForObject(eq(url), entity.capture(), eq(String.class))).willReturn(someStringResponse);
        String result = sut.post(url, new Payload(), accessToken);
        assertThat(result, is(someStringResponse));
        HttpHeaders headers = entity.getValue().getHeaders();
        assertThat(headers.get("Authorization"), hasItem("Bearer " + accessToken.get()));
        assertThat(headers.getAccept(), hasItem(MediaType.valueOf("application/vnd.hmrc.1.0+json")));
        assertThat(headers.getContentType(), is(MediaType.APPLICATION_JSON));
    }

    static class Payload implements Serializable {
        private String eventType = "someEvent";
        private LocalDate eventDate = LocalDate.parse("2017-05-24", DateTimeFormatter.ISO_LOCAL_DATE);

        public String getEventType() {
            return eventType;
        }

        public LocalDate getEventDate() {
            return eventDate;
        }
    }

}