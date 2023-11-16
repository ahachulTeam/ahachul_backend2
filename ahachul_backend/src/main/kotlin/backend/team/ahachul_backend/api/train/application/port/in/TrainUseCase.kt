package backend.team.ahachul_backend.api.train.application.port.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.command.GetCongestionCommand

interface TrainUseCase {

    fun getTrain(trainNo: String): GetTrainDto.Response

    fun getTrainRealTimes(stationId: Long, subwayLineId: Long): List<GetTrainRealTimesDto.TrainRealTime>

    fun getTrainCongestion(command: GetCongestionCommand): GetCongestionDto.Response
}
