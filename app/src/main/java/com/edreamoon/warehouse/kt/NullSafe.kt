package com.edreamoon.warehouse.kt

class NullSafe {
    val value = "value"

    companion object {
        fun getNullSafe(): NullSafe? {
            return null
        }
    }
}