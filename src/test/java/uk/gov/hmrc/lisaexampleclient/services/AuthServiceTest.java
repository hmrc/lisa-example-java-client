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

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmrc.lisaexampleclient.entities.OauthPair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.contains;

public class AuthServiceTest {

    private static final String RESPONSE_JSON = '{' +
            "    \"access_token\": \"5ad445aaf59ff8f9e8c797f71a59597\"," +
            "    \"refresh_token\": \"157dee5130874324c42b3dee8467145\"," +
            "    \"expires_in\": 3600," +
            "    \"scope\": \"read:individual-income\"," +
            "    \"token_type\": \"bearer\"" +
            '}';
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    RestTemplate restTemplate;
    @Mock
    ResponseEntity<String> responseEntityString;
    @Captor
    ArgumentCaptor<HttpEntity> httpEntity;
    @InjectMocks
    AuthService sut;

    @Test
    public void shouldTestGetCurrentOauthPair() throws Exception {
        OauthPair ignored = createOauthPair();
        assertThat(sut.getCurrentOauthPair(), is(not(nullValue())));
    }

    @Test
    public void shouldTestCreateNewOauthPair() throws Exception {
        assertThat(sut.getCurrentOauthPair(), is(nullValue()));

        OauthPair result = createOauthPair();

        assertThat(result.isValid(), is(true));
        assertThat(result.getAccessToken(), is("5ad445aaf59ff8f9e8c797f71a59597"));
        assertThat(result.getRefreshToken(), is("157dee5130874324c42b3dee8467145"));
        assertCloseEnoughToEqualDates(result.getRefreshValidUntil(),
                LocalDateTime.now(ZoneOffset.UTC).plusHours(1L));
        assertThat(result.isDueRenewal(), is(false));
        assertCloseEnoughToEqualDates(result.getAccessValidUntil(),
                LocalDateTime.now(ZoneOffset.UTC).plusHours(4L));
        List<String> contentType = httpEntity.getValue().getHeaders().get("Content-Type");

        assertThat(contentType.get(0), is("application/x-www-form-urlencoded"));
    }

    @Test
    public void shouldTestRefreshOauthPair() throws Exception {
        OauthPair anOauthPair = createOauthPair();
        assertThat(sut.refreshOauthPair(), is(not(sameInstance(anOauthPair))));
    }

    @Test
    public void shouldGetAuthUrl() throws Exception {
        sut.authUrl = "xyz";
        assertThat(sut.getAuthUrl(), is("xyz"));
    }

    @Test
    public void shouldGetClientId() throws Exception {
        sut.clientId = "abc";
        assertThat(sut.getClientId(), is("abc"));
    }

    @Test
    public void shouldGetRedirectUrl() throws Exception {
        sut.redirectUrl = "ijk";
        assertThat(sut.getRedirectUrl(), is("ijk"));
    }

    @Test
    public void shouldInvalidateOauthPair() throws Exception {
        createOauthPair();
        assertThat(sut.getCurrentOauthPair(), is(not(nullValue())));
        sut.invalidateOauthPair();
        assertThat(sut.getCurrentOauthPair(), is(nullValue()));
    }

    OauthPair createOauthPair() throws InvalidAuthenticationCodeException {
        given(restTemplate.postForEntity(
                contains("/oauth/token"),
                httpEntity.capture(),
                eq(String.class))).willReturn(responseEntityString);
        given(responseEntityString.getBody()).willReturn(RESPONSE_JSON);
        return sut.createNewOauthPair("testing");
    }

    void assertCloseEnoughToEqualDates(LocalDateTime expected, LocalDateTime actual) {
        assertThat(Duration.between(expected, actual).getSeconds(), lessThan(Duration.ofSeconds(2L).getSeconds()));
    }
}