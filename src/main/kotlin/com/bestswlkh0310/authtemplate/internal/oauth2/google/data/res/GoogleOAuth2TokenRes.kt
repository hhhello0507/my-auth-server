package com.bestswlkh0310.authtemplate.internal.oauth2.google.data.res

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleOAuth2TokenRes(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Long,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("id_token") val idToken: String
)