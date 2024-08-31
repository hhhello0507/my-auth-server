package com.bestswlkh0310.authtemplate.api.auth.jwt

import com.bestswlkh0310.authtemplate.core.user.UserRepository
import com.bestswlkh0310.authtemplate.core.user.getByUsername
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String) =
        JwtUserDetails(userRepository.getByUsername(username))
}