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
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.udfsoft.screenfactorygenerator.annotation.JParam
import com.udfsoft.screenfactorygenerator.annotation.JScreen
import com.udfsoft.screenfactorygenerator.utils.IoUtils.plusAssign
import com.udfsoft.screenfactorygenerator.utils.Utils.findAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.findArgument
import com.udfsoft.screenfactorygenerator.utils.Utils.getDeclaredPropertiesWithAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.isChildForClass
import java.io.OutputStream

class FragmentVisitor(
    private val file: OutputStream,
    private val className: String,
    private val logger: KSPLogger,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind != ClassKind.CLASS) {
            logger.error("Only class can be annotated with @JScreen", classDeclaration)
            return
        }

        val isFragment = classDeclaration.isChildForClass("Fragment")

        if (!isFragment) {
            logger.error("Class must be inherited from Fragment", classDeclaration)
            return
        }

        val annotation: KSAnnotation = classDeclaration.findAnnotation(JScreen::class.simpleName!!)

        val generateScreenMethodArgument = annotation.arguments.findArgument("generateScreenMethod")

        val params =
            classDeclaration.getDeclaredPropertiesWithAnnotation(JParam::class.simpleName!!)

        if (generateScreenMethodArgument.value == true) {
            file += "import com.github.terrakok.cicerone.androidx.FragmentScreen\n"
        }

        file += "import androidx.core.os.bundleOf\n"
        file += "import androidx.fragment.app.Fragment\n"
        file += "import com.udfsoft.screenfactorygenerator.BaseScreen\n\n"

        file += "object ${className}Screen : BaseScreen {\n\n"

        file += if (params.toList().isEmpty()) {
            "    fun newInstance() = ${className}()\n\n"
        } else {

            val paramsForSetArguments = params.map {
                "\"${it.toString().uppercase()}\" to $it"
            }.joinToString(",")

            val paramsInString = params.map {
                "$it: ${it.type}"
            }.joinToString()

            "    fun newInstance($paramsInString) = ${className}().apply {\n" +
                    "       arguments = bundleOf($paramsForSetArguments)\n" +
                    "    }\n\n"
        }

        file += "    fun ${className}.bind() {\n"

        val paramsForGetArguments = params.map {
            "       $it = arguments?.get(\"${it.toString().uppercase()}\") as ${it.type}"
        }.joinToString("\n")

        file += "$paramsForGetArguments\n"
        file += "    }\n"

        if (generateScreenMethodArgument.value == true) {
            file += "\n"
            file += if (params.toList().isEmpty()) {
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
        }

        file += "}\n"
    }
}