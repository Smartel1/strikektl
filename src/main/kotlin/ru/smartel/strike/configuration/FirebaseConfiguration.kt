package ru.smartel.strike.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.smartel.strike.configuration.properties.FirebaseProperties
import java.io.IOException

@Configuration
class FirebaseConfiguration(private val properties: FirebaseProperties) {
    @Bean
    fun firebaseApp(): FirebaseApp? {
        if (properties.credentials.isBlank()) return null;
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(properties.credentials.byteInputStream()))
                .build()
            return FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    @Bean
    fun messaging(): FirebaseMessaging? = firebaseApp()?.let { return FirebaseMessaging.getInstance(it) }

    @Bean
    fun auth(): FirebaseAuth? = firebaseApp()?.let { return FirebaseAuth.getInstance(it) }
}