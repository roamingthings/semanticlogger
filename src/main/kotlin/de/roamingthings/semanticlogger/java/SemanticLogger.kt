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
package de.roamingthings.semanticlogger.java

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalDate.now
import java.time.LocalDate.parse

class SemanticLogger {

    private var delegate: Logger

    internal constructor(clazz: Class<*>) {
        delegate = getLogger(clazz)
    }

    internal constructor(name: String) {
        delegate = getLogger(name)
    }

    internal constructor(loggerDelegate: Logger) {
        this.delegate = loggerDelegate
    }

    fun remindToRemoveUnusedImplementationAfter(deadlineInIsoFormat: String, message: String) {
        val deadline = parse(deadlineInIsoFormat)

        if (now().isAfter(deadline)) {
            delegate.error(message)
        }
    }

    fun forTestPurpose(message: String) = delegate.debug(message)

    fun forTestPurpose(format: String, arg: Any) = delegate.debug(format, arg)

    fun forTestPurpose(format: String, arg1: Any, arg2: Any) = delegate.debug(format, arg1, arg2)

    fun forTestPurpose(format: String, vararg arguments: Any) = delegate.debug(format, *arguments)

    fun forTestPurpose(msg: String, t: Throwable) = delegate.debug(msg, t)

    fun asExpectedByDefault(message: String) = delegate.info(message)

    fun asExpectedByDefault(format: String, arg: Any) = delegate.info(format, arg)

    fun asExpectedByDefault(format: String, arg1: Any, arg2: Any) = delegate.info(format, arg1, arg2)

    fun asExpectedByDefault(format: String, vararg arguments: Any) = delegate.info(format, *arguments)

    fun asExpectedByDefault(msg: String, t: Throwable) = delegate.info(msg, t)

    fun toInvestigateTomorrow(message: String) = delegate.warn(message)

    fun toInvestigateTomorrow(format: String, arg: Any) = delegate.warn(format, arg)

    fun toInvestigateTomorrow(format: String, arg1: Any, arg2: Any) = delegate.warn(format, arg1, arg2)

    fun toInvestigateTomorrow(format: String, vararg arguments: Any) = delegate.warn(format, *arguments)

    fun toInvestigateTomorrow(msg: String, t: Throwable) = delegate.warn(msg, t)

    fun wakeMeUpInTheMiddleOfTheNight(message: String) = delegate.error(message)

    fun wakeMeUpInTheMiddleOfTheNight(format: String, arg: Any) = delegate.error(format, arg)

    fun wakeMeUpInTheMiddleOfTheNight(format: String, arg1: Any, arg2: Any) = delegate.error(format, arg1, arg2)

    fun wakeMeUpInTheMiddleOfTheNight(format: String, vararg arguments: Any) = delegate.error(format, *arguments)

    fun wakeMeUpInTheMiddleOfTheNight(msg: String, t: Throwable) = delegate.error(msg, t)
}
