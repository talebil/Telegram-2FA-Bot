package com.p1neapplexpress.ci.bot

object FeatureMemoryStore {
    private val memory = hashMapOf<Long, HashMap<String, Any>>()

    fun allocate(userId: Long) {
        if (userId !in memory) memory[userId] = hashMapOf()
    }

    fun get(userId: Long) = memory[userId]
}