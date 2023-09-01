package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.member.adapter.web.out.MemberRepository
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.domain.Congestion
import backend.team.ahachul_backend.api.train.domain.model.TrainArrivalCode
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.TrainCongestionClient
import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto.Section
import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto.Train
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.model.RegionType
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class TrainCongestionServiceTest(
    @Autowired val trainCongestionService: TrainCongestionService,
    @Autowired val memberRepository: MemberRepository
): CommonServiceTestConfig() {

    @MockBean
    lateinit var trainCongestionClient: TrainCongestionClient

    @MockBean
    lateinit var stationReader: StationReader

    @MockBean
    lateinit var trainCacheUtils: TrainCacheUtils

    lateinit var station: StationEntity

    @BeforeEach
    fun setUp() {
        val member = memberRepository.save(
            MemberEntity(
                nickname = "nickname",
                provider = ProviderType.GOOGLE,
                providerUserId = "providerUserId",
                email = "email",
                gender = GenderType.MALE,
                ageRange = "20",
                status = MemberStatusType.ACTIVE
            )
        )
        member.id.let { RequestUtils.setAttribute("memberId", it) }
    }

    @Test
    fun 열차_혼잡도_퍼센트에_따라_색깔을_반환한다() {
        // given
        val congestionResult = TrainCongestionDto(
            success = true,
            code = 100,
            data = Train(
                subwayLine = "2",
                trainY = "2034",
                congestionResult = Section(
                    congestionTrain = "35",
                    congestionCar = "20|31|36|100|41|38|50|51|38|230",
                    congestionType =  1
                )
            )
        )
        given(trainCongestionClient.getCongestions(anyLong(), anyInt())).willReturn(congestionResult)

        val realTimeTrainData = listOf(
            createTrainRealTime(1, "2236", "6분", TrainArrivalCode.RUNNING),
            createTrainRealTime(2, "2238", "전역 도착", TrainArrivalCode.BEFORE_STATION_ARRIVE),
            createTrainRealTime(1, "2234", "전역 도착", TrainArrivalCode.BEFORE_STATION_ARRIVE)
        )
        given(trainCacheUtils.getCache(anyLong(), anyLong())).willReturn(realTimeTrainData)

        val subwayLine = SubwayLineEntity(
            id = 2,
            name = "2호선",
            regionType = RegionType.METROPOLITAN
        )

        station = StationEntity(
            id = 1,
            name = "뚝섬역",
            identity = 1002000210,
            subwayLine = subwayLine
        )

        given(stationReader.getById(anyLong())).willReturn(station)

        // when
        val result = trainCongestionService.getTrainCongestion(station.id)

        // then
        val expected = listOf(
            Congestion.SMOOTH.name,
            Congestion.SMOOTH.name,
            Congestion.MODERATE.name,
            Congestion.CONGESTED.name,
            Congestion.MODERATE.name,
            Congestion.MODERATE.name,
            Congestion.MODERATE.name,
            Congestion.MODERATE.name,
            Congestion.MODERATE.name,
            Congestion.VERY_CONGESTED.name
        )
        assertThat(result.congestions.size).isEqualTo(10)
        for (i: Int in 0 ..9) {
            assertThat(result.congestions[i].sectionNo).isEqualTo(i+1)
            assertThat(result.congestions[i].congestionColor).isEqualTo(expected[i])
        }
    }

    @Test
    fun 지원하지_않는_노선이면_예외가_발생한다() {
        // given
        val subwayLine = SubwayLineEntity(
            id = 1,
            name = "1호선",
            regionType = RegionType.METROPOLITAN
        )

        station = StationEntity(
            id = 1,
            name = "서울역",
            identity = 1002000210,
            subwayLine = subwayLine
        )

        given(stationReader.getById(anyLong())).willReturn(station)

        // when + then
        Assertions.assertThatThrownBy {
            trainCongestionService.getTrainCongestion(station.id)
        }
            .isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(ResponseCode.INVALID_SUBWAY_LINE.message)
    }

    private fun createTrainRealTime(
        stationOrder: Int, trainNum: String , currentLocation: String, currentTrainArrivalCode: TrainArrivalCode)
            : GetTrainRealTimesDto.TrainRealTime{
        return GetTrainRealTimesDto.TrainRealTime(
            subwayId = "",
            stationOrder = stationOrder,
            upDownType = UpDownType.DOWN,
            nextStationDirection = "신대방방면",
            destinationStationDirection = "성수행",
            trainNum = trainNum,
            currentLocation = currentLocation,
            currentTrainArrivalCode = currentTrainArrivalCode
        )
    }
}
