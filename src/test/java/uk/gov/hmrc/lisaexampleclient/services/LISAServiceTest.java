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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import uk.gov.hmrc.lisaexampleclient.entities.LisaResponse;
import uk.gov.hmrc.lisaexampleclient.entities.OauthPair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class LISAServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    ServiceConnector serviceConnector;
    @Mock
    AuthService authService;
    @Mock
    OauthPair oauthPair;
    @InjectMocks
    LISAService sut;
    static final String LIFE_EVENT_URL = "http://tax/authUrl/lifetime-isa/manager/{lisaManagerReferenceNumber" +
            "}/accounts/{accountID" +
            "}/events";

    @Before
    public void onSetup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new ISO8601DateFormat());
        sut.mapper = mapper;
    }

    @Test
    public void shouldTestCreateLifeEvent() throws Exception {
        String response = "{\n" +
                "  \"data\": {\n" +
                "    \"lifeEventId\": \"9876543210\",\n" +
                "    \"message\": \"Life Event Created.\"\n" +
                "  },\n" +
                "  \"success\": true,\n" +
                "  \"status\": 201\n" +
                "}";
        given(authService.getCurrentOauthPair()).willReturn(oauthPair);
        given(oauthPair.getAccessToken()).willReturn("1234567890");
        given(serviceConnector.post(anyString(), anyObject(), anyObject())).willReturn(response);
        sut.lifeEventUrl = LIFE_EVENT_URL;
        LocalDate localDate = LocalDate.parse("2017-05-24", DateTimeFormatter.ISO_LOCAL_DATE);
        LisaResponse lifeEvent = sut.createLifeEvent("someManagerId", "someAccountID", "someEventType", localDate);
        assertThat(lifeEvent.isSuccess(), is(true));
        assertThat(lifeEvent.getStatus(), is(HttpStatus.CREATED.value()));
        assertThat(lifeEvent.getData().getLifeEventId(), is("9876543210"));
        assertThat(lifeEvent.getData().getMessage(), is("Life Event Created."));
    }

}