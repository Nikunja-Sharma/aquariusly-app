package com.nikunja.aquariusly.data.remote.api

import com.nikunja.aquariusly.data.remote.dto.ChatRequestDto
import com.nikunja.aquariusly.data.remote.dto.ChatResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ChatApiService {

    @POST("ai/chat")
    suspend fun chat(@Body request: ChatRequestDto): Response<ChatResponseDto>

    @Streaming
    @POST("ai/chat/stream")
    suspend fun chatStream(@Body request: ChatRequestDto): Response<ResponseBody>
}
