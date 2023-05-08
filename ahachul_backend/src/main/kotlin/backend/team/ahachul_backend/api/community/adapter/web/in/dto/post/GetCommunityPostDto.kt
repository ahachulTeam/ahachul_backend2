package backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityCategoryType
import backend.team.ahachul_backend.common.model.RegionType
import java.time.LocalDateTime

class GetCommunityPostDto {

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val categoryType: CommunityCategoryType,
        val views: Int,
        val likes: Int,
        val region: RegionType,
        val createdAt: LocalDateTime,
        val createdBy: String,
        val writer: String
    ) {
        companion object {
            fun from(entity: CommunityPostEntity): Response {
                return Response(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    categoryType = entity.categoryType,
                    views = entity.views,
                    // TODO 좋아요 구현 후
                    likes = 0,
                    region = entity.regionType,
                    createdAt = entity.createdAt,
                    createdBy = entity.createdBy,
                    writer = entity.member?.nickname!!
                )
            }
        }
    }
}