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

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OauthPairTest {

    private OauthPair sut;
    private static final String access_token = "5ad445aaf59ff8f9e8c797f71a59597";
    private static final String refresh_token = "157dee5130874324c42b3dee8467145";
    private Clock fixedClock;
    private LocalDateTime fixedNow;

    @Before
    public void setUp() throws Exception {
        fixedNow = LocalDateTime.of(2017, 3, 31, 17, 12, 54, 32);
        fixedClock = Clock.fixed(fixedNow.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        sut = new OauthPair(refresh_token, access_token, LocalDateTime.now(fixedClock), fixedClock);
    }

    @Test
    public void shouldTestIsRefreshTokenValid() throws Exception {
        sut = new OauthPair(refresh_token, access_token, fixedNow.plusMinutes(1L), fixedClock);
        assertThat(sut.isValid(), is(true)); // renewal date is in future
    }
    
    @Test
    public void shouldTestIsRefreshTokenNotValid() throws Exception {
        sut = new OauthPair(refresh_token, access_token, fixedNow.minusMinutes(1L), fixedClock);
        assertThat(sut.isValid(), is(false));  // renewal date is in past
    }

    @Test
    public void shouldTestNotDueRenewalIfOverTenMinutes() throws Exception {
        sut = new OauthPair(refresh_token, access_token, LocalDateTime.now(fixedClock), fixedClock);
        assertThat(sut.isDueRenewal(), is(false)); // renewal date is in probably ~4 hours
    }

    @Test
    public void shouldTestIsAccessTokenDueRenewalIfUnderTenMinutes() throws Exception {
        sut = new OauthPair(refresh_token, access_token, LocalDateTime.now(fixedClock), fixedClock);
        Clock clock = Clock.offset(fixedClock, Duration.ofHours(3L).plusMinutes(51L)); // 9 minutes to go
        sut.setClock(clock);
        assertThat(sut.isDueRenewal(), is(true));
    }

    @Test
    public void shouldTestIsAccessTokenNotDueRenewalIfOverTenMinutes() throws Exception {
        sut = new OauthPair(refresh_token, access_token, LocalDateTime.now(fixedClock), fixedClock);
        Clock clock = Clock.offset(fixedClock, Duration.ofHours(3L).plusMinutes(49L)); // 11 minutes to go
        sut.setClock(clock);
        assertThat(sut.isDueRenewal(), is(false));
    }

    @Test
    public void shouldTestSetRefreshToken() throws Exception {
        sut.setRefreshToken("abc");
        assertThat(sut.getRefreshToken(), is("abc"));
    }

    @Test
    public void shouldTestSetAccessToken() throws Exception {
        sut.setAccessToken("abc");
        assertThat(sut.getAccessToken(), is("abc"));
    }

    @Test
    public void shouldTestSetRefreshValidUntil() throws Exception {
        LocalDateTime dateNow = LocalDateTime.now(fixedClock);
        sut.setRefreshValidUntil(dateNow);
        assertThat(sut.getRefreshValidUntil(), is(dateNow));
    }

    @Test
    public void shouldTestSetAccessValidUntil() throws Exception {
        LocalDateTime dateNow = LocalDateTime.now(fixedClock);
        sut.setAccessValidUntil(dateNow);
        assertThat(sut.getAccessValidUntil(), is(dateNow));
    }

}