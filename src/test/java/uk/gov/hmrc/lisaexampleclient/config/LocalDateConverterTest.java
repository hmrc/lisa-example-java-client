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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LocalDateConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private LocalDateConverter sut;

    @Before
    public void setUp() throws Exception {
        sut = new LocalDateConverter();
    }

    @Test
    public void shouldTestConvertToObject() throws Exception {
        LocalDate result = sut.convertToObject("2017-12-31", Locale.UK);
        assertThat(result, is(LocalDate.of(2017, 12, 31)));
    }

    @Test
    public void shouldFailConvertToObject() throws Exception {
        thrown.expect(DateTimeParseException.class);
        thrown.expectMessage("Text '**NONSENSE**' could not be parsed at index 0");
        sut.convertToObject("**NONSENSE**", Locale.UK);
    }

    @Test
    public void shouldTestConvertToString() throws Exception {
        String result = sut.convertToString(LocalDate.of(2017, 12, 30), Locale.UK);
        assertThat(result, is("2017-12-30"));
    }

}