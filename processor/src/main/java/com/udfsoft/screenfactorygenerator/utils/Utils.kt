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
}