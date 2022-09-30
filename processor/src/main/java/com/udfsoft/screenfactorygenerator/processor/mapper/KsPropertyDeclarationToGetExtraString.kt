package com.udfsoft.screenfactorygenerator.processor.mapper

import com.google.devtools.ksp.symbol.KSPropertyDeclaration

class KsPropertyDeclarationToGetExtraString : MapperInterface<KSPropertyDeclaration, String> {

    override fun transform(param: KSPropertyDeclaration) = when (param.type.toString()) {
        "String" -> {
            "       $param = intent.get${param.type}Extra(\"${
                param.toString().uppercase()
            }\")!!"
        }
        else -> {
            "       $param = intent.get${param.type}Extra(\"${
                param.toString().uppercase()
            }\", $param)"
        }
    }
}