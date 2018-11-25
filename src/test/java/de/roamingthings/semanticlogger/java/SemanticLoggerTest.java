/*
 * Copyright © 2018 Alexander Sparkowsky
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
package de.roamingthings.semanticlogger.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SemanticLoggerTest {

    private Logger loggerDelegate;

    private SemanticLogger logger;

    @BeforeEach
    void setup() {
        loggerDelegate = mock(Logger.class);
        logger = new SemanticLogger(loggerDelegate);
    }

    @Test
    void remindToRemoveUnusedImplementationAfter_should_log_message_after_deadline() {
        // given
        String yesterday = toIsoDate(now().minus(1, DAYS));

        // when
        logger.remindToRemoveUnusedImplementationAfter(yesterday, "Expected Logmessage");

        // then
        verify(loggerDelegate, times(1)).error(eq("Expected Logmessage"));
    }

    @Test
    void remindToRemoveUnusedImplementationAfter_should_ignore_message_before_deadline() {
        // given
        String tomorrow = toIsoDate(now().plus(1, DAYS));

        // when
        logger.remindToRemoveUnusedImplementationAfter(tomorrow, "Unexpected Logmessage");

        // then
        verify(loggerDelegate, never()).error(any());
    }

     private String toIsoDate(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
    }
}
