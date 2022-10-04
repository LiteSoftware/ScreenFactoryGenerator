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
import com.google.devtools.ksp.symbol.*
import com.udfsoft.screenfactorygenerator.annotation.JParam
import com.udfsoft.screenfactorygenerator.annotation.JScreen
import com.udfsoft.screenfactorygenerator.utils.IoUtils.plusAssign
import com.udfsoft.screenfactorygenerator.utils.Utils.findAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.findArgument
import com.udfsoft.screenfactorygenerator.utils.Utils.getDeclaredPropertiesWithAnnotation
import com.udfsoft.screenfactorygenerator.utils.Utils.isChildForClass
import com.udfsoft.screenfactorygenerator.utils.Utils.replaceFirst
import java.io.OutputStream

abstract class BaseVisitor(
    private val file: OutputStream,
    private val className: String,
    private val logger: KSPLogger,
    private val screenManagerClassStringBuilder: StringBuilder
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind != ClassKind.CLASS) {
            logger.error("Only class can be annotated with @JScreen", classDeclaration)
            return
        }

        val isChild = classDeclaration.isChildForClass(getParentClassString())

        if (!isChild) {
            logger.error("Class must be inherited from ${getParentClassString()}", classDeclaration)
            return
        }

        val annotation: KSAnnotation = classDeclaration.findAnnotation(JScreen::class.simpleName!!)

        val generateScreenMethodArgument = annotation.arguments.findArgument(CICERONE_FIELD)

        val params =
            classDeclaration.getDeclaredPropertiesWithAnnotation(JParam::class.simpleName!!)

        file += getImportForGenericClass(generateScreenMethodArgument)

        file += "object ${className}Screen : BaseScreen {\n\n"
        file += getClassBody(params, generateScreenMethodArgument, className)
        file += "}\n"

        val packageName = classDeclaration.containingFile!!.packageName.asString()
        val importIndex = screenManagerClassStringBuilder.indexOf("import")
        screenManagerClassStringBuilder.insert(
            importIndex,
            "import ${packageName}.${className}Screen.initArguments\n"
        )
        screenManagerClassStringBuilder.insert(importIndex, "import ${packageName}.$className\n")

        if (getParentClassString() == ACTIVITY_CLASS_NAME) {
            createScreenManagerBody("// initArguments(Activities)", name = "activity")
        } else if (getParentClassString() == FRAGMENT_CLASS_NAME) {
            createScreenManagerBody("// initArguments(Fragments)", name = "fragment")
        }
    }

    private fun createScreenManagerBody(replaceString: String, name: String) {
        if (screenManagerClassStringBuilder.contains(replaceString)) {
            val body = buildString {
                appendLine("when ($name) {")
                appendLine("            is $className -> $name.initArguments()")
                append("        }")
            }
            screenManagerClassStringBuilder.replaceFirst(replaceString, body)
        } else {
            val body = buildString {
                appendLine("when ($name) {")
                append("            is $className -> $name.initArguments()")
            }
            screenManagerClassStringBuilder.replaceFirst("when ($name) {", body)
        }
    }

    abstract fun getParentClassString(): String

    abstract fun getImportForGenericClass(generateScreenMethodArgument: KSValueArgument): String

    abstract fun getClassBody(
        params: Sequence<KSPropertyDeclaration>,
        generateScreenMethodArgument: KSValueArgument,
        className: String
    ): String

    companion object {
        private const val CICERONE_FIELD = "generateCiceroneScreenMethod"
        const val FRAGMENT_CLASS_NAME = "Fragment"
        const val ACTIVITY_CLASS_NAME = "Activity"
    }
}