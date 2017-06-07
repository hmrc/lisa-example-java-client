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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmrc.lisaexampleclient.entities.LisaResponse;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class LISAService {

    @Value("${tax.createLifeEventUrl}")
    protected String lifeEventUrl;
    @Autowired
    ServiceConnector serviceConnector;
    @Autowired
    AuthService authService;
    @Autowired ObjectMapper mapper;

    public LisaResponse createLifeEvent(String managerId, String accountId, String eventType, LocalDate date) throws IOException {
        String url = format(lifeEventUrl, managerId, accountId);
        Payload payload = new Payload();
        payload.eventDate = date;
        payload.eventType = eventType;
        String response = serviceConnector.post(url, payload, Optional.of(authService.getCurrentOauthPair().getAccessToken()));

        return mapper.readValue(response, LisaResponse.class);
    }

    private String format(String url, String managerId, String accountId) {
        org.springframework.util.Assert.hasLength(url,"Check config properties - ${tax.createLifeEventUrl} must not be blank");
        return url.replaceAll("\\{lisaManagerReferenceNumber\\}", managerId).replaceAll("\\{accountID\\}", accountId);
    }

    static class Payload implements Serializable {
        private String eventType;
        private LocalDate eventDate;

        public String getEventType() {
            return eventType;
        }

        public LocalDate getEventDate() {
            return eventDate;
        }
    }

}
