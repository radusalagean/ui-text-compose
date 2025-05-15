package com.radusalagean.uitextcompose.core

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal API and should not be used directly. It is intended for use only by other uitextcompose modules."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY
)
public annotation class InternalApi