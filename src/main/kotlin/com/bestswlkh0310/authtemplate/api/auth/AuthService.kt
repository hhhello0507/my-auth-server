package com.bestswlkh0310.authtemplate.api.auth

import com.bestswlkh0310.authtemplate.api.auth.data.enumeration.JwtPayloadKey
import com.bestswlkh0310.authtemplate.api.auth.data.enumeration.PlatformType
import com.bestswlkh0310.authtemplate.api.auth.data.req.OAuth2SignInReq
import com.bestswlkh0310.authtemplate.api.auth.data.req.RefreshReq
import com.bestswlkh0310.authtemplate.api.auth.data.req.SignInReq
import com.bestswlkh0310.authtemplate.api.auth.data.req.SignUpReq
import com.bestswlkh0310.authtemplate.api.auth.data.res.TokenRes
import com.bestswlkh0310.authtemplate.api.core.data.BaseRes
import com.bestswlkh0310.authtemplate.foundation.user.UserRepository
import com.bestswlkh0310.authtemplate.foundation.user.data.entity.User
import com.bestswlkh0310.authtemplate.foundation.user.getByUsername
import com.bestswlkh0310.authtemplate.global.exception.CustomException
import com.bestswlkh0310.authtemplate.internal.oauth2.AppleOAuth2Client
import com.bestswlkh0310.authtemplate.internal.oauth2.GoogleOAuth2Client
import com.bestswlkh0310.authtemplate.internal.token.JwtClient
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder,
    private val googleOAuth2Client: GoogleOAuth2Client,
    private val appleOAuth2Client: AppleOAuth2Client,
    private val jwtUtils: JwtClient
) {
    fun signUp(req: SignUpReq): BaseRes<TokenRes> {

        // validation
        if (req.password != req.passwordCheck) {
            throw CustomException(HttpStatus.BAD_REQUEST, "Password do not match")
        } else if (userRepository.existsByUsername(req.username)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "Already exists user")
        }

        // create user
        val user = userRepository.save(
            User(
                username = req.username,
                password = encoder.encode(req.password),
                nickname = req.nickname
            )
        )

        return BaseRes.ok(
            jwtUtils.generate(user)
        )
    }

    fun signIn(req: SignInReq): BaseRes<TokenRes> {
        // validation
        val user = userRepository.getByUsername(req.username)

        if (!encoder.matches(req.password, user.password)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "Passwords do not match")
        }

        return BaseRes.ok(
            jwtUtils.generate(user)
        )
    }

    fun refresh(req: RefreshReq): BaseRes<TokenRes> {
        jwtUtils.parseToken(req.refreshToken)

        val user = run {
            val username = jwtUtils.payload(JwtPayloadKey.USERNAME, req.refreshToken)
            userRepository.getByUsername(username)
        }

        return BaseRes.ok(
            jwtUtils.generate(user)
        )
    }

    fun oAuth2SignIn(req: OAuth2SignInReq): BaseRes<TokenRes> {
        val token = when (req.platformType) {
            PlatformType.GOOGLE -> googleSignIn(req)
            PlatformType.APPLE -> appleSignIn(req)
            else -> throw CustomException(HttpStatus.BAD_REQUEST, "Invalid platform type")
        }
        return BaseRes.ok(
            jwtUtils.generate(token)
        )
    }

    private fun googleSignIn(req: OAuth2SignInReq): User {
        // validation
        val idToken = googleOAuth2Client.verifyIdToken(req.idToken)
        val username = idToken.payload.email
        val users = userRepository.findByUsername(username)
        val user = users.firstOrNull() ?: userRepository.save(
            User(
                username = username,
                password = null,
                nickname = req.nickname,
                platformType = req.platformType
            )
        )
        return user
    }

    private fun appleSignIn(req: OAuth2SignInReq): User {
        val headers = appleOAuth2Client.parseHeader(idToken = req.idToken)
        val keys = appleOAuth2Client.applePublicKeys()
        val publicKey = appleOAuth2Client.generate(headers = headers, keys = keys)
        val claims = appleOAuth2Client.extractClaims(idToken = req.idToken, publicKey = publicKey)
        appleOAuth2Client.validateBundleId(claims = claims)


        val username = claims["email"] as? String ?: throw CustomException(HttpStatus.BAD_REQUEST, "Invalid email")
        val users = userRepository.findByUsername(username)
        val user = users.firstOrNull() ?: userRepository.save(
            User(
                username = username,
                password = null,
                nickname = req.nickname,
                platformType = req.platformType
            )
        )
        return user
    }
}