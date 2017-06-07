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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmrc.lisaexampleclient.services.AuthService;
import uk.gov.hmrc.lisaexampleclient.services.InvalidAuthenticationCodeException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class RefreshTokenTask {

    final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenTask.class);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter
            .ofPattern("dd LLL yyyy HH:mm:ss", Locale.UK)
            .withZone(ZoneId.systemDefault());

    @Autowired
    public RefreshTokenTask(AuthService authService) {
        this.authService = authService;
    }

    @Scheduled(fixedRate = 60000)
    public void refreshToken() {
        if (authService.isOAuthPairValid()) {
            log.debug("Refresh due before {}",
                    dateFormat.format(authService.getCurrentOauthPair().getRefreshValidUntil()));
            try {
                if (authService.getCurrentOauthPair().isDueRenewal()) {
                    log.info("Refreshing access token");
                    authService.refreshOauthPair();
                } else {
                    log.debug("Access token is not due renewal.");
                }
            } catch (InvalidAuthenticationCodeException e) {
                log.error("Unable to refresh the access token", e);
            }
        } else {
            log.warn("No valid access token.  Trusted Admin must log in again.");
        }
    }
}