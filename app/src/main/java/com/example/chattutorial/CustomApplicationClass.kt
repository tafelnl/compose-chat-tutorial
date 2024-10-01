package com.example.chattutorial

import android.app.Application
import android.os.Handler
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.extensions.globalState
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.WwfBzU1GZr0brt_fXnqKdKhz3oj0rbDUm2DqJO_SS5U"

class CustomApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(
            appContext = applicationContext,
        )
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = this,
        )

        // 2 - Set up the client for API calls and with the plugin for offline storage
        ChatClient.Builder("uun7ywwamhs9", applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.NOTHING) // Set to NOTHING in prod
            .build()

        login()
        // The next line does NOT make it crash
        login()

        // The next couple of lines DO make it crash
        Handler().postDelayed({
            login()
        }, 2000)
    }

    companion object {
        private val job = Job()
        private val scope = CoroutineScope(job)

        fun login() {
            // Uncommenting the lines below would prevent the crash:
            // if (ChatClient.instance().clientState.initializationState.value != InitializationState.NOT_INITIALIZED) {
            //     return
            // }

            println("CustomStreamLog - login called")

            val tokenProvider = object : TokenProvider {
                override fun loadToken(): String = loadStreamToken()
                // Replacing the line above with the line below would prevent the crash:
                // override fun loadToken(): String = token
            }

            val chatClient = ChatClient.instance()

            // 3 - Authenticate and connect the user
            val user = User(
                id = "tutorial-droid",
                name = "Tutorial Droid",
                image = "https://bit.ly/2TIt8NR"
            )

            chatClient.connectUser(
                user = user,
                tokenProvider = tokenProvider
            ).enqueue { response ->
                println("CustomStreamLog - response")
                if (response.isSuccess) {
                    println("CustomStreamLog - isSuccess")
                    scope.launch {
                        // The line below isn't really meant to be,
                        // but I couldn't reproduce reliably without adding it
                        chatClient.clientState.initializationState.collectLatest {
                            println("CustomStreamLog - channelUnreadCount: ${chatClient.globalState.channelUnreadCount.value}")
                        }
                    }
                }
            }
        }
    }
}