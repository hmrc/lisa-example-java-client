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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import uk.gov.hmrc.lisaexampleclient.services.AuthService;
import uk.gov.hmrc.lisaexampleclient.services.InvalidAuthenticationCodeException;
import uk.gov.hmrc.lisaexampleclient.entities.OauthPair;

public abstract class UserRestrictedPage extends WebPage {

    @SpringBean
    AuthService authService;

    public UserRestrictedPage(PageParameters parameters) {
        super(parameters);

        super.onBeforeRender();
        // No valid access token?  That means that we've not gone through the
        // Government Gateway sign-in and grant screens yet.
        OauthPair oauthPair = authService.getCurrentOauthPair();
        if (oauthPair == null || !oauthPair.isValid()) {
            /// TODO Maybe check for code first so it the pair can be refreshed by a new GG login + Grant, even when
            // the current pair is still valid
            StringValue code = getPageParameters().get("code");
            if (code.isEmpty())
                redirectToGovernmentGateway();
            else
                try {
                    authService.createNewOauthPair(code.toOptionalString());
                } catch (InvalidAuthenticationCodeException e) {
                    redirectToGovernmentGateway();
                }
        }

    }

    /**
     * Send a redirect to the browser to show the Government Gateway login page
     */
    protected void redirectToGovernmentGateway() {
        throw new RedirectToUrlException(
                authService.getAuthUrl() + "/oauth/authorize?response_type=code" +
                        "&client_id=" + authService.getClientId() +
                        "&scope=" + authService.getScope() +
                        "&redirect_uri=" + authService.getRedirectUrl());
    }
}
