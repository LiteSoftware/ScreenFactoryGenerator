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

package com.udfsoft.screenfactorygenerator.utils

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument

object Utils {

    fun KSClassDeclaration.isChildForClass(parent: String) =
        getAllSuperTypes().firstOrNull { it.toString() == parent } != null

    fun KSClassDeclaration.getDeclaredPropertiesWithAnnotation(annotation: String) =
        getDeclaredProperties().filter { property ->
            property.annotations.firstOrNull {
                it.toString() == "@$annotation"
            } != null
        }

    fun KSClassDeclaration.findAnnotation(annotation: String) = annotations.first {
        it.shortName.asString() == annotation
    }

    fun List<KSValueArgument>.findArgument(argumentName: String) = first {
        it.name?.getShortName() == argumentName
    }

    fun StringBuilder.replaceFirst(oldValue: String, replacement: String): StringBuilder {
        val text = toString().replaceFirst(oldValue, replacement)
        clear()
        append(text)

        return this
    }
}