package backend.team.ahachul_backend.api.train.adapter.`in`

import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.config.controller.CommonDocsTestConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TrainController::class)
class TrainControllerDocsTest : CommonDocsTestConfig() {

    @MockBean
    lateinit var trainUseCase: TrainUseCase

    @Test
    fun getTrainTest() {
        // given
        val response = GetTrainDto.Response(
            id = 1L,
            GetTrainDto.SubwayLine(1L, "1호선"),
            location = 3,
            organizationTrainNo = "52"
        )

        BDDMockito.given(trainUseCase.getTrain(any()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/trains/{trainNo}", 1)
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "get-train",
                    getDocsRequest(),
                    getDocsResponse(),
                    pathParameters(
                        parameterWithName("trainNo").description("열차 번호")
                    ),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        PayloadDocumentation.fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("열차 식별 번호"),
                        PayloadDocumentation.fieldWithPath("result.subwayLine.id").type(JsonFieldType.NUMBER).description("노선 식별 번호"),
                        PayloadDocumentation.fieldWithPath("result.subwayLine.name").type(JsonFieldType.STRING).description("노선 이름"),
                        PayloadDocumentation.fieldWithPath("result.location").type(JsonFieldType.NUMBER).description("열차 내 현재 위치"),
                        PayloadDocumentation.fieldWithPath("result.organizationTrainNo").type(JsonFieldType.STRING).description("열차 편대 번호"),
                    )
                )
            )
    }

    @Test
    fun getTrainRealTimesTest() {
        // given
        val response = GetTrainRealTimesDto.Response(
            subwayLineId = 1L,
            stationId = 1L,
            listOf(
                GetTrainRealTimesDto.TrainRealTime(
                    upDownType = UpDownType.DOWN,
                    nextStationDirection = "신대방방면",
                    destinationStationDirection = "성수행",
                    trainNum = "2234",
                    currentLocation = "전역 도착"
                ),
                GetTrainRealTimesDto.TrainRealTime(
                    upDownType = UpDownType.UP,
                    nextStationDirection = "봉천방면",
                    destinationStationDirection = "성수행",
                    trainNum = "2236",
                    currentLocation = "6분"
                )
            )
        )

        BDDMockito.given(trainUseCase.getTrainRealTimes(anyLong(), anyLong()))
            .willReturn(response)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/v1/trains/real-times")
                .queryParam("subwayLineId", "1")
                .queryParam("stationId", "1")
                .header("Authorization", "Bearer <Access Token>")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "get-train-real-times",
                    getDocsRequest(),
                    getDocsResponse(),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("엑세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("subwayLineId").description("지하철 노선 ID"),
                        parameterWithName("stationId").description("정류장 ID")
                    ),
                    PayloadDocumentation.responseFields(
                        *commonResponseFields(),
                        PayloadDocumentation.fieldWithPath("result.subwayLineId").type(JsonFieldType.NUMBER).description("지하철 노선 ID"),
                        PayloadDocumentation.fieldWithPath("result.stationId").type(JsonFieldType.NUMBER).description("정류장 ID"),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[]").type(JsonFieldType.ARRAY).description("해당 정류장 실시간 열차 정보 리스트"),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[].upDownType").type("UpDownType").description("상하행선구분").attributes(getFormatAttribute("UP, DOWN")),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[].nextStationDirection").type(JsonFieldType.STRING).description("다음 정류장 방향"),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[].destinationStationDirection").type(JsonFieldType.STRING).description("목적지 방향"),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[].trainNum").type(JsonFieldType.STRING).description("해당 열차 ID"),
                        PayloadDocumentation.fieldWithPath("result.trainRealTimes[].currentLocation").type(JsonFieldType.STRING).description("해당 열차 현재 위치"),
                    )
                )
            )
    }
}