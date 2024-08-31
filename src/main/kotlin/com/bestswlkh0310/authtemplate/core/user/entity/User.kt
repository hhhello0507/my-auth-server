package com.bestswlkh0310.authtemplate.core.user.entity

import com.bestswlkh0310.authtemplate.api.auth.data.enumeration.PlatformType
import com.bestswlkh0310.authtemplate.core.user.UserRole
import jakarta.persistence.*

@Entity(name = "`user`")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    val id: Long = 0,

    @Column(nullable = false)
    val username: String,

    val password: String?,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val platformType: PlatformType = PlatformType.DEFAULT
)