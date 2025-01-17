package backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostStatus
import backend.team.ahachul_backend.api.lost.domain.model.LostType
import backend.team.ahachul_backend.common.dto.ImageDto
import java.time.format.DateTimeFormatter

class GetLostPostDto {

    data class Response(
        val id: Long,
        val title: String,
        val content: String,
        val writer: String?,
        val createdBy: String?,
        val createdAt: String,
        val subwayLineId: Long?,
        val commentCnt: Int,
        val status: LostStatus,
        val storage: String?,
        val storageNumber: String?,
        val pageUrl: String?,
        val images: List<ImageDto>?,
        val categoryName: String?,
        val externalSourceImageUrl: String?,
        val recommendPosts: List<RecommendResponse>,
        val isFromLost112: Boolean,
        val lostType: LostType
    ) {
        companion object {
            fun of(entity: LostPostEntity, commentCnt: Int, images: List<ImageDto>, recommendPosts: List<RecommendResponse>): Response {
                return Response(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    writer = entity.member?.nickname,
                    createdBy = entity.createdBy,
                    createdAt = entity.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    subwayLineId = entity.subwayLine?.id,
                    commentCnt = commentCnt,
                    status = entity.status,
                    storage = entity.storage,
                    storageNumber = entity.storageNumber,
                    pageUrl = entity.pageUrl,
                    images = images,
                    categoryName = entity.category?.name,
                    externalSourceImageUrl = entity.externalSourceFileUrl,
                    recommendPosts = recommendPosts,
                    isFromLost112 = entity.isFromLost112(),
                    lostType = entity.lostType
                )
            }
        }
    }

    data class RecommendResponse(
        val id: Long,
        val title: String,
        val writer: String,
        val imageUrl: String?,
        val createdAt: String
    )
}
