package org.dcam.model.core

class PolicyManager {

    private val policies:List<Policy> = mutableListOf()

    fun checkAll(request: Request): List<Policy> {
        val problems:MutableList<Policy> = mutableListOf()
        policies.forEach {
            if (it.check(request).not()) {
                problems.add(it)
            }
        }

        return problems
    }

    companion object {
        fun fromString(s:String) : PolicyManager = PolicyManager()
    }
}