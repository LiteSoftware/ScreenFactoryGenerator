package com.udfsoft.screenfactorygenerator.processor.mapper

interface MapperInterface<T, R> {

    fun transform(param: T): R
}