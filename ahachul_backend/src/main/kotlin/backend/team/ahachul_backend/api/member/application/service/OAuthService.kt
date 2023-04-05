package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.member.adapter.web.`in`.dto.LoginMemberDto
import backend.team.ahachul_backend.api.member.application.port.`in`.OAuthUseCase
import backend.team.ahachul_backend.api.member.application.port.`in`.command.LoginMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.common.client.GoogleMemberClient
import backend.team.ahachul_backend.common.client.KakaoMemberClient
import backend.team.ahachul_backend.common.dto.GoogleUserInfoDto
import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto
import backend.team.ahachul_backend.common.properties.JwtProperties
import backend.team.ahachul_backend.common.utils.JwtUtils
import org.springframework.stereotype.Service

@Service
class OAuthService(
        private val memberWriter: MemberWriter,
        private val kakaoMemberClient: KakaoMemberClient,
        private val googleMemberClient: GoogleMemberClient,
        private val jwtUtils: JwtUtils,
        private val jwtProperties: JwtProperties
): OAuthUseCase {

    override fun login(command: LoginMemberCommand): LoginMemberDto.Response {
        var memberId = ""
        when (command.providerType) {
            ProviderType.KAKAO -> {
                val userInfo = getKakaoMemberInfo(command.providerCode)
                memberId = memberWriter.save(MemberEntity.of(command, userInfo)).toString()
            }
            ProviderType.GOOGLE -> {
//                val userInfo = getGoogleMemberInfo(command.providerCode)
//                memberId = memberWriter.save(MemberDomainMapper.toEntity(Member.of(command, userInfo))).toString()
            }
        }
        return makeLoginResponse(memberId)
    }


    private fun getKakaoMemberInfo(provideCode: String): KakaoMemberInfoDto {
        val accessToken = kakaoMemberClient.getAccessTokenByCode(provideCode)
        return kakaoMemberClient.getMemberInfoByAccessToken(accessToken)
    }

    private fun getGoogleMemberInfo(provideCode: String): GoogleUserInfoDto? {
        val accessToken = googleMemberClient.getAccessTokenByCode(provideCode)
        return googleMemberClient.getMemberInfoByAccessToken(accessToken!!)
    }

    private fun makeLoginResponse(memberId: String): LoginMemberDto.Response {
        return LoginMemberDto.Response(
                memberId = memberId,
                accessToken = jwtUtils.createToken(memberId, jwtProperties.accessTokenExpireTime),
                accessTokenExpiresIn = jwtProperties.accessTokenExpireTime,
                refreshToken = jwtUtils.createToken(memberId, jwtProperties.refreshTokenExpireTime),
                refreshTokenExpiresIn = jwtProperties.refreshTokenExpireTime
        )
    }
}

