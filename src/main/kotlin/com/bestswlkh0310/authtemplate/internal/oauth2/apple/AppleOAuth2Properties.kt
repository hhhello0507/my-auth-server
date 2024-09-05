package com.bestswlkh0310.authtemplate.internal.oauth2.apple

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppleOAuth2Properties(
    @Value("\${oauth2.apple.bundle-id}") val appleBundleId: String,
    @Value("\${oauth2.apple.grant-type}") val appleGrantType: String,
)