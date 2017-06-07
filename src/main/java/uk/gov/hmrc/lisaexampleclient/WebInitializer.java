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

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
public class WebInitializer implements ServletContextInitializer {

    private static final String PARAM_APP_BEAN = "applicationBean";

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        FilterRegistration filter = sc.addFilter("wicket-filter",
                WicketFilter.class);
        filter.setInitParameter(WicketFilter.APP_FACT_PARAM,
                SpringWebApplicationFactory.class.getName());
        filter.setInitParameter(PARAM_APP_BEAN, "wicketWebApplication");
        // This line is the only surprise when comparing to the equivalent
        // web.xml. Without some initialization seems to be missing.
        filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        filter.addMappingForUrlPatterns(null, false, "/*");
    }

}
