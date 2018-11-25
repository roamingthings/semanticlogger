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

object SemanticLoggerFactory {

    @JvmStatic
    fun getLogger(clazz: Class<*>): SemanticLogger =
        SemanticLogger(clazz)

    @JvmStatic
    fun getLogger(name: String): SemanticLogger =
        SemanticLogger(name)

    @JvmStatic
    fun getLogger(loggerDelegate: Logger): SemanticLogger =
        SemanticLogger(loggerDelegate)
}
