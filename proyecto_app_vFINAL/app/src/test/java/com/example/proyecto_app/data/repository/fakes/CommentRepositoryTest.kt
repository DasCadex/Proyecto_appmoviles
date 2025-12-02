package com.example.proyecto_app.data.repository.fakes

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.ComentarioDto
import com.example.proyecto_app.data.repository.CommentRepository
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import retrofit2.Response.success


class CommentRepositoryTest{
    private val FakeComment = ComentarioDto (
        id = 1,
        publicationId = 1,
        userId = 100,
        authorName = "cade",
        text = "test",
        createdAt = "2025-01-01"
    )
    @Test
    fun `getCommentsForPublication devuelve lista correcta cuando API responde`(){
        val api = mockk<PixelHubApi>()

        coEvery { api.getComentarios(1) } returns listOf(FakeComment)
        val repository = CommentRepository(api)
        runBlocking {
            val result = mutableListOf<ComentarioDto>()
            repository.getCommentsForPublication(1).collect { lista ->
                result.addAll(lista)
            }
            assert(result.size == 1)
            assert(result[0].authorName == "cade")


        }


    }
    @Test
    fun `addComment crea un comentario`(){
        val api = mockk<PixelHubApi>()
        coEvery { api.crearComentario(any()) } returns FakeComment
        runBlocking {
            val repository = CommentRepository(api)
            repository.addComment(FakeComment)

            // Verificamos que se llamó a la API
            coVerify { api.crearComentario(FakeComment) }


        }

    }


}