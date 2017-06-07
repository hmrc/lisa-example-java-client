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
package uk.gov.hmrc.lisaexampleclient.config;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule(); // use Rule not the mockito runner-allow use of Spring runner
    @InjectMocks
    private Configuration sut;

    @Test
    public void shouldTestRestTemplate() throws Exception {
        List<ClientHttpRequestInterceptor> interceptors = sut.restTemplate().getInterceptors();
        assertThat(interceptors, hasSize(1));
        assertThat(interceptors.get(0), is(instanceOf(Configuration.AcceptHeaderHttpRequestInterceptor.class)));
    }

}