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

import de.roamingthings.semanticlogger.SemanticLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static de.roamingthings.semanticlogger.SemanticLoggerFactory.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("SemanticLoggerFactory")
class SemanticLoggerFactoryTest {

    @Test
    @DisplayName("Should return logger for given class")
    void should_return_logger_for_class() {
        // given

        // when
        SemanticLogger logger = getLogger(Object.class);

        // then
        assertThat(logger).isNotNull();
    }

    @Test
    @DisplayName("Should return logger for given name")
    void should_return_logger_for_name() {
        // given

        // when
        SemanticLogger logger = getLogger("A class");

        // then
        assertThat(logger).isNotNull();
    }

    @Test
    @DisplayName("Should return logger for given logger delegate")
    void should_return_logger_for_delegate() {
        // given
        Logger delegate = mock(Logger.class);

        // when
        SemanticLogger logger = getLogger(delegate);

        // then
        assertThat(logger).isNotNull();
    }
}