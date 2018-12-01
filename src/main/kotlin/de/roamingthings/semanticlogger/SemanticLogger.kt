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

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalDate.now
import java.time.LocalDate.parse

/**
 * Semantic Logger.
 *
 * This class wraps a `org.slf4j.Logger` and converts its log levels _debug_, _info_, _warn_ and _error_ in methods
 * with semantic names:
 *
 * debug() -> forTestPurpose()
 *
 * info() -> asExpectedByDefault()
 *
 * warn() -> toInvestigateTomorrow()
 *
 * error() -> wakeMeUpInTheMiddleOfTheNight()
 *
 * The method signatures are equal to the delegated methods.
 *
 * @author Alexander Sparkowsky
 */
class SemanticLogger {

    private var delegate: Logger

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param clazz The created logger will use the FQN of this class as a name.
     */
    internal constructor(clazz: Class<*>) {
        delegate = getLogger(clazz)
    }

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param name The created logger will use the given name.
     */
    internal constructor(name: String) {
        delegate = getLogger(name)
    }

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param logger An existing logger to be used as a delegate.
     */
    internal constructor(loggerDelegate: Logger) {
        this.delegate = loggerDelegate
    }

    /**
     * Generate a logging event with the level _error_ if the current date/time is after the date
     * in `deadlineDateInIsoFormat`.
     *
     * @param deadlineDateInIsoFormat Deadline after which the message will be logged in ISO format (e.g. 2018-10-23).
     * @param message Message to be logged.
     */
    fun remindToRemoveUnusedImplementationAfter(deadlineDateInIsoFormat: String, message: String) {
        val deadline = parse(deadlineDateInIsoFormat)

        if (now().isAfter(deadline)) {
            delegate.error(message)
        }
    }

    /**
     * Generate a logging message with _debug_ level.
     *
     * @param message Message to be logged.
     */
    fun forTestPurpose(message: String) = delegate.debug(message)

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format The format.
     * @param arg The argument.
     */
    fun forTestPurpose(format: String, arg: Any) = delegate.debug(format, arg)

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     */
    fun forTestPurpose(format: String, arg1: Any, arg2: Any) = delegate.debug(format, arg1, arg2)

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format The format.
     * @param arguments A list of 3 or more arguments.
     */
    fun forTestPurpose(format: String, vararg arguments: Any) = delegate.debug(format, *arguments)

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t The exception (throwable) to log.
     */
    fun forTestPurpose(msg: String, t: Throwable) = delegate.debug(msg, t)

    /**
     * Generate a logging message with _info_ level.
     *
     * @param message Message to be logged.
     */
    fun asExpectedByDefault(message: String) = delegate.info(message)

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format The format.
     * @param arg The argument.
     */
    fun asExpectedByDefault(format: String, arg: Any) = delegate.info(format, arg)

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     */
    fun asExpectedByDefault(format: String, arg1: Any, arg2: Any) = delegate.info(format, arg1, arg2)

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format The format.
     * @param arguments A list of 3 or more arguments.
     */
    fun asExpectedByDefault(format: String, vararg arguments: Any) = delegate.info(format, *arguments)

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t The exception (throwable) to log.
     */
    fun asExpectedByDefault(msg: String, t: Throwable) = delegate.info(msg, t)

    /**
     * Generate a logging message with _warn_ level.
     *
     * @param message Message to be logged.
     */
    fun toInvestigateTomorrow(message: String) = delegate.warn(message)

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format The format.
     * @param arg The argument.
     */
    fun toInvestigateTomorrow(format: String, arg: Any) = delegate.warn(format, arg)

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     */
    fun toInvestigateTomorrow(format: String, arg1: Any, arg2: Any) = delegate.warn(format, arg1, arg2)

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format The format.
     * @param arguments A list of 3 or more arguments.
     */
    fun toInvestigateTomorrow(format: String, vararg arguments: Any) = delegate.warn(format, *arguments)

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t The exception (throwable) to log.
     */
    fun toInvestigateTomorrow(msg: String, t: Throwable) = delegate.warn(msg, t)

    /**
     * Generate a logging message with _error_ level.
     *
     * @param message Message to be logged.
     */
    fun wakeMeUpInTheMiddleOfTheNight(message: String) = delegate.error(message)

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format The format.
     * @param arg The argument.
     */
    fun wakeMeUpInTheMiddleOfTheNight(format: String, arg: Any) = delegate.error(format, arg)

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     */
    fun wakeMeUpInTheMiddleOfTheNight(format: String, arg1: Any, arg2: Any) = delegate.error(format, arg1, arg2)

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format The format.
     * @param arguments A list of 3 or more arguments.
     */
    fun wakeMeUpInTheMiddleOfTheNight(format: String, vararg arguments: Any) = delegate.error(format, *arguments)

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t The exception (throwable) to log.
     */
    fun wakeMeUpInTheMiddleOfTheNight(msg: String, t: Throwable) = delegate.error(msg, t)
}
