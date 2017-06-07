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
package uk.gov.hmrc.lisaexampleclient;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uk.gov.hmrc.lisaexampleclient.config.LocalDateConverter;
import uk.gov.hmrc.lisaexampleclient.pages.HomePage;
import uk.gov.hmrc.lisaexampleclient.pages.LISAManagerPage;
import uk.gov.hmrc.lisaexampleclient.pages.RegisterAuthSessionPage;

import java.time.LocalDate;

@Component
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class WicketWebApplication extends WebApplication {

    private static final Logger logger = LoggerFactory.getLogger(WicketWebApplication.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * spring boot main method to build context
     *
     * @param args
     */
    public static void main(String... args) {
        SpringApplication.run(WicketWebApplication.class, args);
    }

    /**
     * provides page for default request
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    /**
     * <ul>
     * <li>making the wicket components injectable by activating the
     * SpringComponentInjector</li>
     * <li>mounting the test page</li>
     * <li>logging spring service method output to showcase working
     * integration</li>
     * </ul>
     */
    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(
                new SpringComponentInjector(this, applicationContext));
        getMarkupSettings().setStripWicketTags(true);

        setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig()));

        mountPage("/admin", RegisterAuthSessionPage.class);
        mountPage("/lisa", LISAManagerPage.class);

    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
        locator.set(LocalDate.class, new LocalDateConverter());
        return locator;
    }
}
