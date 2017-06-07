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
package uk.gov.hmrc.lisaexampleclient.pages;

import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.gov.hmrc.lisaexampleclient.services.AuthService;

import java.time.ZoneOffset;
import java.util.Date;

@RequireHttps
public class RegisterAuthSessionPage extends UserRestrictedPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    AuthService authService;

    public RegisterAuthSessionPage(final PageParameters parameters) {
        super(parameters);

        DateLabel currentAccessToken = new DateLabel("accessValidUntil", new LoadableDetachableModel() {

            @Override
            protected Date load() {
                return Date.from(authService.getCurrentOauthPair().getAccessValidUntil().toInstant(ZoneOffset.UTC));
            }
        },
                new StyleDateConverter("MM", true));

        add(currentAccessToken);

        DateLabel refreshDue = new DateLabel("refreshDue", new LoadableDetachableModel() {

            @Override
            protected Date load() {
                return Date.from(authService.getCurrentOauthPair().getRefreshValidUntil().toInstant(ZoneOffset.UTC));
            }
        },
                new StyleDateConverter("MM", true));
        add(refreshDue);

    }


}

