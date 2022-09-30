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
import com.udfsoft.screenfactorygenerator.processor.mapper.KsPropertyDeclarationToGetExtraString
import com.udfsoft.screenfactorygenerator.utils.IoUtils.plusAssign
import com.udfsoft.screenfactorygenerator.utils.Utils.findAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.findArgument
import com.udfsoft.screenfactorygenerator.utils.Utils.getDeclaredPropertiesWithAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.isChildForClass
import java.io.OutputStream

class ActivityVisitor(
    private val file: OutputStream,
    private val className: String,
    private val logger: KSPLogger,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind != ClassKind.CLASS) {
            logger.error("Only class can be annotated with @JScreen", classDeclaration)
            return
        }

        val isActivity = classDeclaration.isChildForClass("Activity")

        if (!isActivity) {
            logger.error("Class must be inherited from Activity", classDeclaration)
            return
        }

        val annotation: KSAnnotation = classDeclaration.findAnnotation(JScreen::class.simpleName!!)

        val generateScreenMethodArgument = annotation.arguments.findArgument("generateScreenMethod")

        val params =
            classDeclaration.getDeclaredPropertiesWithAnnotation(JParam::class.simpleName!!)

        if (generateScreenMethodArgument.value == true) {
            file += "import com.github.terrakok.cicerone.androidx.ActivityScreen\n"
        }

        file += "import android.content.Context\n"
        file += "import android.content.Intent\n"
        file += "import androidx.core.os.bundleOf\n"
        file += "import androidx.fragment.app.Fragment\n"
        file += "import com.udfsoft.screenfactorygenerator.BaseScreen\n\n"

        file += "object ${className}Screen : BaseScreen {\n\n"

        file += if (params.toList().isEmpty()) {
            "    fun newIntent(context: Context) = Intent(context, ${className}::class.java)\n\n"
        } else {
            val paramsForSetArguments = params.map {
                "intent.putExtra(\"${it.toString().uppercase()}\", $it)"
            }.joinToString("\n")

            val paramsInString = params.map {
                "$it: ${it.type}"
            }.joinToString()

            "    fun newIntent(context: Context, $paramsInString): Intent { \n" +
                    "       val intent = Intent(context, ${className}::class.java)\n" +
                    "       $paramsForSetArguments\n" +
                    "       return intent\n" +
                    "    }\n\n"
        }

        file += "    fun ${className}.bind() {\n"

        val paramsForGetArguments = params.map(KsPropertyDeclarationToGetExtraString()::transform)
            .joinToString("\n")

        file += "$paramsForGetArguments\n"
        file += "    }\n"

        if (generateScreenMethodArgument.value == true) {
            file += "\n"
            file += if (params.toList().isEmpty()) {
                "    fun get${className}Screen() = ActivityScreen { newIntent(it) }\n"
            } else {

                val paramsWithTypeInString = params.map {
                    "$it: ${it.type}"
                }.joinToString()

                val paramsInString = params.map {
                    "$it"
                }.joinToString()

                "    fun get${className}Screen($paramsWithTypeInString) = ActivityScreen {\n" +
                        "       newIntent(it, $paramsInString)\n" +
                        "   }\n"
            }
        }

        file += "}\n"
    }
}