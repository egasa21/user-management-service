package com.batch14.usermanagementservice.utils

import com.batch14.usermanagementservice.domain.Constant
import com.batch14.usermanagementservice.exception.CustomException
import io.jsonwebtoken.JwtException

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {
    @Value("\${jwt.secret-key}")
    private lateinit var SECRET_KEY: String

//    private val LOG: Logger = Logger.getLogger(JwtUtil::class.java)

    fun generateToken(id: Int, role: String): String {
        try {
            val signatureAlgorithm = SignatureAlgorithm.HS256
            val signingKey = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
            val exp = Date(System.currentTimeMillis() + 60000) // 15 minutes

            return Jwts.builder()
                .setSubject(id.toString())
                .claim("id_user", id.toString())
                .claim("role", role)
                .signWith(signingKey, signatureAlgorithm)
                .setExpiration(exp)
                .compact()


        } catch (e: JwtException) {
            throw CustomException("Internal server error", 500, Constant.STATUS_ERROR)
        }
    }
}