package com.bestswlkh0310.authtemplate.global.config

import com.bestswlkh0310.authtemplate.global.properties.OAuth2Properties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Qualifier("google oauth2 client")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class GoogleOAuth2RestClient

@Configuration
class RestClientConfig(
    private val oAuth2Properties: OAuth2Properties
) {
    @Bean
    @GoogleOAuth2RestClient
    fun googleOAuth2RestClient() = RestClient.builder()
        .baseUrl("https://oauth2.googleapis.com")
        .build()
}