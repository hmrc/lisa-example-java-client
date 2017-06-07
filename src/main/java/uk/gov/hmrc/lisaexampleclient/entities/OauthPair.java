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
package uk.gov.hmrc.lisaexampleclient.entities;


import java.time.Clock;
import java.time.LocalDateTime;

public class OauthPair {

    private Clock clock = Clock.systemUTC();
    private String accessToken;
    private String refreshToken;
    private LocalDateTime refreshValidUntil;
    private LocalDateTime accessValidUntil;


    public OauthPair(String accessToken, String refreshToken, LocalDateTime refreshValidUntil) {
        this(accessToken, refreshToken, refreshValidUntil, Clock.systemUTC());
    }

    // Constructor for testing - allow passing in a fixed clock for testing e.g. :
    // Clock.fixed(Instant.parse("2017-04-28T07:24:30.856Z"), ZoneId.of("UTC"));
    OauthPair(String accessToken, String refreshToken, LocalDateTime refreshValidUntil, Clock clock) {
        this.clock = clock;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshValidUntil = refreshValidUntil;
        this.accessValidUntil = LocalDateTime.now(clock).plusHours(4L);// refresh due in 4 hours
    }

    public boolean isValid() {
        return refreshValidUntil.isAfter(LocalDateTime.now(clock));
    }

    public boolean isDueRenewal() {
        LocalDateTime tenMinutesBeforeRenewalDue = accessValidUntil.minusMinutes(10L);
        return LocalDateTime.now(clock).isAfter(tenMinutesBeforeRenewalDue);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getRefreshValidUntil() {
        return refreshValidUntil;
    }

    public void setRefreshValidUntil(LocalDateTime refreshValidUntil) {
        this.refreshValidUntil = refreshValidUntil;
    }

    public LocalDateTime getAccessValidUntil() {
        return accessValidUntil;
    }

    public void setAccessValidUntil(LocalDateTime accessValidUntil) {
        this.accessValidUntil = accessValidUntil;
    }

    void setClock(Clock clock) {
        if (clock == null) {
            clock = Clock.systemUTC();
        }
        this.clock = clock;
    }
}
