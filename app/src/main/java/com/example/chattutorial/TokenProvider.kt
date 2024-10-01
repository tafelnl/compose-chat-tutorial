package com.example.chattutorial

fun loadStreamToken(): String {
    return try {
        // In a real world application we would possibly make a call using `okhttp3`
        // But for simplicity this does the trick as well
        MockHttpCall().execute().use {
            token
        }
    } catch (exception: Exception) {
        ""
    }
}

class MockHttpCall {
    fun execute(): AutoCloseable {
        // Simulate some delay
        Thread.sleep(500)
        return AutoCloseable {}
    }
}