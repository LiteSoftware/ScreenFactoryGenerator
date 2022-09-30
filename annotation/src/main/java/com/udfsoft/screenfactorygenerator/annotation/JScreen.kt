package com.udfsoft.screenfactorygenerator.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class JScreen(val generateScreenMethod: Boolean = true)
