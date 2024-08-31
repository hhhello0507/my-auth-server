package com.bestswlkh0310.authtemplate.api.auth.data.req

import com.bestswlkh0310.authtemplate.global.exception.CustomException
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus

data class SignUpReq(
    @Size(min = 2, max = 24)
    val username: String,

    @Size(min = 2, max = 24)
    val password: String,

    @Size(min = 2, max = 24)
    val passwordCheck: String,

    @Size(min = 2, max = 24)
    val nickname: String
) {
}