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

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmrc.lisaexampleclient.entities.LisaResponse;
import uk.gov.hmrc.lisaexampleclient.pages.components.CustomFeedbackPanel;
import uk.gov.hmrc.lisaexampleclient.services.AuthService;
import uk.gov.hmrc.lisaexampleclient.services.LISAService;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequireHttps
public class LISAManagerPage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    AuthService authService;

    @SpringBean
    LISAService lisaService;

    private static final List<String> EVENTTYPES = Arrays.asList("LISA Investor Terminal Ill Health", "LISA Investor Death", "House Purchase");

    private static final Logger logger = LoggerFactory.getLogger(LISAManagerPage.class);

    public LISAManagerPage() {
        super();

        add(new CustomFeedbackPanel("feedback"));

        add(new MarkupContainer("warning") {
            @Override
            public boolean isVisible() {
                return !authService.isOAuthPairValid();
            }
        });

        final Event event = new Event();

        //Example data - saves typing every time
        event.managerId = "Z123456";
        event.accountId = "1000000403";
        event.type = "LISA Investor Terminal Ill Health";
        event.date = new Date();

        Form lisaform = new Form("lisaform", new CompoundPropertyModel<>(event)) {
            @Override
            public boolean isVisible() {
                return authService.isOAuthPairValid();
            }
        };
        add(lisaform);

        lisaform.add(new RequiredTextField<String>("managerId"));
        lisaform.add(new RequiredTextField<String>("accountId"));

        lisaform.add(new DropDownChoice<>("type", EVENTTYPES));

        lisaform.add(new DateTextField("date", new StyleDateConverter("S-", true)).setRequired(true));

        lisaform.add(new Button("submit") {
            @Override
            public void onSubmit() {
                logger.debug(event.toString());
                try {
                    LisaResponse result = lisaService.createLifeEvent(event.getManagerId(), event.accountId, event.getType(), event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    success(result.getData().getLifeEventId() + ' ' + result.getData().getMessage());
                } catch (Exception e) {
                    error(e.getMessage());
                }
            }
        });
    }

    static class Event implements Serializable {
        private String managerId;
        private String accountId;
        private Date date;
        private String type;

        public String getManagerId() {
            return managerId;
        }

        public void setManagerId(String managerId) {
            this.managerId = managerId;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String toString() {
            return managerId + ' ' + accountId + ' ' + type;
        }
    }
}


