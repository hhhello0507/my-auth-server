package com.bestswlkh0310.authtemplate.internal.oauth2.google

import com.bestswlkh0310.authtemplate.global.exception.CustomException
import com.bestswlkh0310.authtemplate.internal.oauth2.google.data.res.GoogleOAuth2TokenRes
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.client.toEntity

@Component
class GoogleOAuth2Client(
    private val properties: GoogleOAuth2Properties,
    @Qualifier("google")
    private val restClient: RestClient
) {

    fun getToken(code: String) = restClient
        .post()
        .uri {
            it.path("token")
                .queryParam("client_id", properties.webClientId)
                .queryParam("client_secret", properties.clientSecret)
                .queryParam("code", code)
                .queryParam("grant_type", properties.grantType)
                .queryParam("redirect_uri", properties.redirectUri)
                .build()
        }
        .retrieve()
        .toEntity<GoogleOAuth2TokenRes>()
        .body ?: throw CustomException(HttpStatus.UNAUTHORIZED, "invalid google oauth2 code")


//    fun getResource(idToken: String): GoogleOAuth2ResourceRes {
//        val response = restClient.post()
//            .uri {
//                it.path("tokeninfo")
//                    .queryParam("id_token", idToken)
//                    .build()
//            }
//            .retrieve()
//            .toEntity(GoogleOAuth2ResourceRes::class.java)
//        return response.body ?: throw CustomException(HttpStatus.UNAUTHORIZED, "invalid google oauth2 token")
//    }

    fun verifyIdToken(idToken: String): GoogleIdToken {
        val verifier = GoogleIdTokenVerifier.Builder(
            NetHttpTransport(),
            GsonFactory()
        )
            .setAudience(
                listOf(
                    properties.iOSClientId,
                    properties.webClientId
                )
            )
            .build()

        return verifier.verify(idToken)
            ?: throw CustomException(HttpStatus.UNAUTHORIZED, "Invalid id token")
    }
}