package backend.team.ahachul_backend.api.common.adapter.`in`.dto

import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity

class SearchSubwayLineDto {

    data class Response(
        val subwayLines: List<SubwayLine>
    )
}

data class SubwayLine(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val stations: List<StationDto>
) {

    companion object {
        fun of(subwayLine: SubwayLineEntity, stations: List<StationDto>): SubwayLine {
            return SubwayLine(
                id = subwayLine.id,
                name = subwayLine.name,
                phoneNumber = subwayLine.phoneNumber,
                stations = stations
            )
        }
    }
}
