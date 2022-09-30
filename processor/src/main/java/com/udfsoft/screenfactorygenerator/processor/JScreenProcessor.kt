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

package com.udfsoft.screenfactorygenerator.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.udfsoft.screenfactorygenerator.annotation.JScreen
import com.udfsoft.screenfactorygenerator.processor.visitor.ActivityVisitor
import com.udfsoft.screenfactorygenerator.processor.visitor.FragmentVisitor
import com.udfsoft.screenfactorygenerator.utils.IoUtils.plusAssign
import com.udfsoft.screenfactorygenerator.utils.Utils.isChildForClass

class JScreenProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private var processed: Boolean = false

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (processed || resolver.getAllFiles().toList().isEmpty()) return emptyList()

        val symbols = resolver.getSymbolsWithAnnotation(JScreen::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        // Exit from the processor in case nothing is annotated with @JScreen.
        if (!symbols.iterator().hasNext()) return emptyList()

        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())
        symbols.forEach {
            logger.warn("Symbol: $it")
            logger.warn("Symbol: ${it.getAllSuperTypes().toList()}")
            logger.warn("Symbol: ${it.classKind.type}")
            val packageName = it.containingFile!!.packageName.asString()
            val className = it.qualifiedName!!.asString().substringAfter("${packageName}.")
            val screenClassName = "${className}Screen"
            val sourceFile = codeGenerator.createNewFile(
                dependencies = dependencies,
                packageName = packageName,
                fileName = screenClassName
            )
            sourceFile += "package $packageName\n\n"

            if (it.isChildForClass("Fragment")) {
                it.accept(FragmentVisitor(sourceFile, className, logger), Unit)
            } else if (it.isChildForClass("Activity")) {
                it.accept(ActivityVisitor(sourceFile, className, logger), Unit)
            }
            sourceFile.close()
        }

        processed = true
        val unableToProcess = symbols.filterNot { it.validate() }.toList()

        return unableToProcess
    }
}