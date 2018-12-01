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
package de.roamingthings.semanticlogger.kotlin

import de.roamingthings.semanticlogger.SemanticLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Matchers
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.DAYS

class SemanticLoggerTest {

    private lateinit var loggerDelegate: Logger

    private lateinit var logger: SemanticLogger

    @BeforeEach
    internal fun setup() {
        loggerDelegate = mock(Logger::class.java)
        logger = SemanticLogger(loggerDelegate)
    }

    @Test
    fun remindToRemoveUnusedImplementationAfter_should_log_message_after_deadline() {
        // given
        val yesterday = toIsoDate(now().minus(1, DAYS))

        // when
        logger.remindToRemoveUnusedImplementationAfter(yesterday, "Expected Logmessage")

        // then
        verify<Logger>(loggerDelegate, times(1)).error(Matchers.eq("Expected Logmessage"))
    }

    @Test
    fun remindToRemoveUnusedImplementationAfter_should_ignore_message_before_deadline() {
        // given
        val tomorrow = toIsoDate(now().plus(1, DAYS))

        // when
        logger.remindToRemoveUnusedImplementationAfter(tomorrow, "Unexpected Logmessage")

        // then
        verify<Logger>(loggerDelegate, never()).error(Matchers.any())
    }

    private fun toIsoDate(date: LocalDate): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
    }
}
