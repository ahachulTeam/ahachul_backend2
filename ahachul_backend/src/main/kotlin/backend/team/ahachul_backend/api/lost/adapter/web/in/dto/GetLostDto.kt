package backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.lost.domain.model.LostStatus

class GetLostDto {
    data class AllResponse(
        var lostList: MutableList<Response> = ArrayList()
    )

    data class Response(
        var title: String = "",
        var content: String = "",
        var writer: String = "",
        var date: String = "",
        var lostLine: String = "",
        var status: LostStatus = LostStatus.PROGRESS,
        var chats: Int = 0,
        var imgUrls: List<String> = ArrayList(),
        var storage: String? = "",
        var storage_number: String? = ""
    )
}