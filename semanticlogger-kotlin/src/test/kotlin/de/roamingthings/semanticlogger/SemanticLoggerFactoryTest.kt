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
package de.roamingthings.semanticlogger

import de.roamingthings.semanticlogger.SemanticLoggerFactory.getLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.slf4j.Logger

internal class SemanticLoggerFactoryTest {

    @Test
    fun `should return logger for class`() {
        // given

        // when
        val logger = getLogger(Any::class.java)

        // then
        assertThat(logger).isNotNull
    }

    @Test
    fun `should return logger for name`() {
        // given

        // when
        val logger = getLogger("A class")

        // then
        assertThat(logger).isNotNull
    }

    @Test
    fun `should return logger for delegate`() {
        // given
        val delegate = mock(Logger::class.java)

        // when
        val logger = getLogger(delegate)

        // then
        assertThat(logger).isNotNull
    }
}
