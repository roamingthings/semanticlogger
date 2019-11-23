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
package de.roamingthings.semanticlogger;

import org.slf4j.Logger;

import java.time.LocalDate;

import static org.slf4j.LoggerFactory.getLogger;
import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;

/**
 * Semantic Logger.
 *
 * This class wraps a `org.slf4j.Logger` and converts its log levels _debug_, _info_, _warn_ and _error_ in methods
 * with semantic names:
 *
 * debug() -&gt; forTestPurpose()
 *
 * info() -&gt; asExpectedByDefault()
 *
 * warn() -&gt; toInvestigateTomorrow()
 *
 * error() -&gt; wakeMeUpInTheMiddleOfTheNight()
 *
 * The method signatures are equal to the delegated methods.
 *
 * @author Alexander Sparkowsky
 */
public class SemanticLogger {

    private Logger delegate;

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param clazz The created logger will use the FQN of this class as a name.
     */
    SemanticLogger(Class<?> clazz) {
        delegate = getLogger(clazz);
    }

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param name The created logger will use the given name.
     */
    SemanticLogger(String name) {
        delegate = getLogger(name);
    }

    /**
     * Create a new `SemanticLogger` using the SLF4J `LoggerFactory`.
     *
     * @param logger An existing logger to be used as a delegate.
     */
    SemanticLogger(Logger logger) {
        this.delegate = logger;
    }

    /**
     * Generate a logging event with the level _error_ if the current date/time is after the date
     * in `deadlineDateInIsoFormat`.
     *
     * @param deadlineDateInIsoFormat Deadline after which the message will be logged in ISO format (e.g. 2018-10-23).
     * @param message                 Message to be logged.
     */
    public void remindToRemoveUnusedImplementationAfter(String deadlineDateInIsoFormat, String message) {
        LocalDate deadline = parse(deadlineDateInIsoFormat);

        if (now().isAfter(deadline)) {
            delegate.error(message);
        }
    }

    /**
     * Generate a logging message with _debug_ level.
     *
     * @param message Message to be logged.
     */
    public void forTestPurpose(String message) {
        delegate.debug(message);
    }

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format The format.
     * @param arg    The argument.
     */
    public void forTestPurpose(String format, Object arg) {
        delegate.debug(format, arg);
    }

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1   The first argument.
     * @param arg2   The second argument.
     */
    public void forTestPurpose(String format, Object arg1, Object arg2) {
        delegate.debug(format, arg1, arg2);
    }

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param format    The format.
     * @param arguments A list of 3 or more arguments.
     */
    public void forTestPurpose(String format, Object... arguments) {
        delegate.debug(format, arguments);
    }

    /**
     * Generate a logging message with level of _debug_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t   The exception (throwable) to log.
     */
    public void forTestPurpose(String msg, Throwable t) {
        delegate.debug(msg, t);
    }

    /**
     * Generate a logging message with _info_ level.
     *
     * @param message Message to be logged.
     */
    public void asExpectedByDefault(String message) {
        delegate.info(message);
    }

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format The format.
     * @param arg    The argument.
     */
    public void asExpectedByDefault(String format, Object arg) {
        delegate.info(format, arg);
    }

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1   The first argument.
     * @param arg2   The second argument.
     */
    public void asExpectedByDefault(String format, Object arg1, Object arg2) {
        delegate.info(format, arg1, arg2);
    }

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param format    The format.
     * @param arguments A list of 3 or more arguments.
     */
    public void asExpectedByDefault(String format, Object... arguments) {
        delegate.info(format, arguments);
    }

    /**
     * Generate a logging message with level of _info_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t   The exception (throwable) to log.
     */
    public void asExpectedByDefault(String msg, Throwable t) {
        delegate.info(msg, t);
    }

    /**
     * Generate a logging message with _warn_ level.
     *
     * @param message Message to be logged.
     */
    public void toInvestigateTomorrow(String message) {
        delegate.warn(message);
    }

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format The format.
     * @param arg    The argument.
     */
    public void toInvestigateTomorrow(String format, Object arg) {
        delegate.warn(format, arg);
    }

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1   The first argument.
     * @param arg2   The second argument.
     */
    public void toInvestigateTomorrow(String format, Object arg1, Object arg2) {
        delegate.warn(format, arg1, arg2);
    }

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param format    The format.
     * @param arguments A list of 3 or more arguments.
     */
    public void toInvestigateTomorrow(String format, Object... arguments) {
        delegate.warn(format, arguments);
    }

    /**
     * Generate a logging message with level of _warn_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t   The exception (throwable) to log.
     */
    public void toInvestigateTomorrow(String msg, Throwable t) {
        delegate.warn(msg, t);
    }

    /**
     * Generate a logging message with _error_ level.
     *
     * @param message Message to be logged.
     */
    public void wakeMeUpInTheMiddleOfTheNight(String message) {
        delegate.error(message);
    }

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format The format.
     * @param arg    The argument.
     */
    public void wakeMeUpInTheMiddleOfTheNight(String format, Object arg) {
        delegate.error(format, arg);
    }

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format The format.
     * @param arg1   The first argument.
     * @param arg2   The second argument.
     */
    public void wakeMeUpInTheMiddleOfTheNight(String format, Object arg1, Object arg2) {
        delegate.error(format, arg1, arg2);
    }

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param format    The format.
     * @param arguments A list of 3 or more arguments.
     */
    public void wakeMeUpInTheMiddleOfTheNight(String format, Object... arguments) {
        delegate.error(format, arguments);
    }

    /**
     * Generate a logging message with level of _error_ using the delegate logger.
     *
     * @param msg The message accompanying the exception.
     * @param t   The exception (throwable) to log.
     */
    public void wakeMeUpInTheMiddleOfTheNight(String msg, Throwable t) {
        delegate.error(msg, t);
    }
}
