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

/**
 * This factory is a utility class to produce instances of semantic loggers.
 *
 * Please note that all methods in <code>LoggerFactory</code> are static.
 *
 * @author Alexander Sparkowsky
 */
object SemanticLoggerFactory {

    /**
     * Create an instance using a class as a name.
     *
     * @param clazz The created logger will use the FQN of this class as a name.
     */
    @JvmStatic
    fun getLogger(clazz: Class<*>): SemanticLogger =
        SemanticLogger(clazz)

    /**
     * Create an instance using a given name.
     *
     * @param name The created logger will use the given name.
     */
    @JvmStatic
    fun getLogger(name: String): SemanticLogger =
        SemanticLogger(name)

    /**
     * Create an instance using a given logger.
     *
     * @param logger An existing logger to be used as a delegate.
     */
    @JvmStatic
    fun getLogger(loggerDelegate: Logger): SemanticLogger =
        SemanticLogger(loggerDelegate)
}
