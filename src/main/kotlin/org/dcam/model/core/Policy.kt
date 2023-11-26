package org.dcam.model.core

class Policy {
    fun check(request: Request): Boolean {
        return false
    }

    val description : String = "No activity authorized between may 5 and may 10 included"

}
