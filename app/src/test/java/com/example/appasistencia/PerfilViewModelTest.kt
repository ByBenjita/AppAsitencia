package com.example.appasistencia.viewmodel

import com.example.appasistencia.model.auth.entities.Perfil
import com.example.appasistencia.repository.PerfilRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var repository: PerfilRepository
    lateinit var viewModel: PerfilViewModel

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Sobrescribimos el repo real usando reflection
        viewModel = spyk(PerfilViewModel(), recordPrivateCalls = true)
        viewModel.apply {
            this::class.java.getDeclaredField("repository").apply {
                isAccessible = true
                set(viewModel, repository)
            }
        }
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "fetchPerfil debe guardar perfil correctamente" {
        val fakePerfil = Perfil(idPerson = 1, company = "Company", name = "Juan", phone = "9884664", rut = "21846459-1", users = listOf())

        coEvery { repository.getPerfil(1) } returns fakePerfil

        viewModel.fetchPerfil(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.perfil.value shouldBe fakePerfil
        viewModel.isLoading.value shouldBe false
        viewModel.error.value shouldBe null
    }

    "fetchPerfil debe manejar excepciones" {
        coEvery { repository.getPerfil(1) } throws RuntimeException("Error")

        viewModel.fetchPerfil(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.perfil.value shouldBe null
        viewModel.error.value shouldBe "Error"
        viewModel.isLoading.value shouldBe false
    }
})
