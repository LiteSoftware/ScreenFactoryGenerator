/*
 * Copyright 2022 javavirys
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.udfsoft.screenfactorygenerator.processor.visitor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import java.io.OutputStream

class FragmentVisitor(
    file: OutputStream,
    className: String,
    private val logger: KSPLogger,
    screenManagerClassStringBuilder: StringBuilder
) : BaseVisitor(file, className, logger, screenManagerClassStringBuilder) {

    override fun getParentClassString() = FRAGMENT_CLASS_NAME

    override fun getImportForGenericClass(
        generateScreenMethodArgument: KSValueArgument
    ) = buildString {
        if (generateScreenMethodArgument.value == true) {
            appendLine("import com.github.terrakok.cicerone.androidx.FragmentScreen")
        }

        appendLine("import androidx.core.os.bundleOf")
        appendLine("import androidx.fragment.app.Fragment")
        appendLine("import com.udfsoft.screenfactorygenerator.BaseScreen\n")
    }

    override fun getClassBody(
        params: Sequence<KSPropertyDeclaration>,
        generateScreenMethodArgument: KSValueArgument,
        className: String
    ) = buildString {
        appendLine(
            if (params.toList().isEmpty()) {
                "    fun newInstance() = ${className}()\n"
            } else {

                val paramsForSetArguments = params.map {
                    "\"${it.toString().uppercase()}\" to $it"
                }.joinToString(",")

                val paramsInString = params.map {
                    "$it: ${it.type}"
                }.joinToString()

                "    fun newInstance($paramsInString) = ${className}().apply {\n" +
                        "       arguments = bundleOf($paramsForSetArguments)\n" +
                        "    }\n"
            }
        )

        appendLine("    fun ${className}.initArguments() {")

        val paramsForGetArguments = params.map {
            "       $it = arguments?.get(\"${it.toString().uppercase()}\") as ${it.type}"
        }.joinToString("\n")

        appendLine(paramsForGetArguments)
        appendLine("    }")

        if (generateScreenMethodArgument.value == true) {
            appendLine()
            appendLine(
                if (params.toList().isEmpty()) {
                    "    fun get${className}Screen() = FragmentScreen { newInstance() }\n"
                } else {

                    val paramsWithTypeInString = params.map {
                        "$it: ${it.type}"
                    }.joinToString()

                    val paramsInString = params.map {
                        "$it"
                    }.joinToString()

                    "    fun get${className}Screen($paramsWithTypeInString) = FragmentScreen {\n" +
                            "       newInstance($paramsInString)\n" +
                            "   }\n"
                }
            )
        }
    }
}