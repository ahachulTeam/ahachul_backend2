package backend.team.ahachul_backend.api.common.application.port.out

import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity

interface SubwayLineStationReader {

    fun findAll(): List<SubwayLineStationEntity>
}
