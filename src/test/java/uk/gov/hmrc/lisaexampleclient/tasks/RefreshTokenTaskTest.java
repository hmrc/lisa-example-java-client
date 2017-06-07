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
package uk.gov.hmrc.lisaexampleclient.tasks;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.hmrc.lisaexampleclient.services.AuthService;
import uk.gov.hmrc.lisaexampleclient.services.InvalidAuthenticationCodeException;
import uk.gov.hmrc.lisaexampleclient.entities.OauthPair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class RefreshTokenTaskTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @InjectMocks
    RefreshTokenTask sut;
    @Mock
    AuthService authService;
    @Mock
    OauthPair oauthPair;

    @Test
    public void shouldTestNotRefreshTokenAsInvalidPair() throws Exception {
        given(authService.isOAuthPairValid()).willReturn(false);
        sut.refreshToken();
        verify(authService, never()).refreshOauthPair();
    }

    @Test
    public void shouldTestRefreshToken() throws Exception {
        given(authService.isOAuthPairValid()).willReturn(true);
        given(authService.getCurrentOauthPair()).willReturn(oauthPair);
        given(oauthPair.isDueRenewal()).willReturn(true);
        LocalDateTime dateTime = LocalDateTime.parse(
                "2017-03-04T11:30:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        given(oauthPair.getRefreshValidUntil()).willReturn(dateTime);

        sut.refreshToken();

        verify(authService, times(1)).refreshOauthPair();
    }

    @Test
    public void shouldTestRefreshTokenNotDueRenewal() throws Exception {
        given(authService.isOAuthPairValid()).willReturn(true);
        given(authService.getCurrentOauthPair()).willReturn(oauthPair);
        given(oauthPair.isDueRenewal()).willReturn(false);
        LocalDateTime dateTime = LocalDateTime.parse(
                "2017-03-04T11:30:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        given(oauthPair.getRefreshValidUntil()).willReturn(dateTime);

        sut.refreshToken();

        verify(authService, never()).refreshOauthPair();
    }

    @Test
    public void shouldTestRefreshTokenRenewalException() throws Exception {
        given(authService.isOAuthPairValid()).willReturn(true);
        given(authService.getCurrentOauthPair()).willReturn(oauthPair);
        given(oauthPair.isDueRenewal()).willReturn(true);
        LocalDateTime dateTime = LocalDateTime.parse(
                "2017-03-04T11:30:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        given(oauthPair.getRefreshValidUntil()).willReturn(dateTime);
        given(authService.refreshOauthPair()).willThrow(new InvalidAuthenticationCodeException());
        sut.refreshToken();
    }

}