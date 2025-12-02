package com.example.demobase.service;

import com.example.demobase.dto.GameResponseDTO;
import com.example.demobase.model.Game;
import com.example.demobase.model.GameInProgress;
import com.example.demobase.model.Player;
import com.example.demobase.model.Word;
import com.example.demobase.repository.GameInProgressRepository;
import com.example.demobase.repository.GameRepository;
import com.example.demobase.repository.PlayerRepository;
import com.example.demobase.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameInProgressRepository gameInProgressRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private GameService gameService;

    // Métodos helper para crear mocks
    private Player createMockPlayer(Long id, String nombre) {
        Player player = new Player();
        player.setId(id);
        player.setNombre(nombre);
        player.setFecha(LocalDate.now());
        return player;
    }

    private Word createMockWord(Long id, String palabra, boolean utilizada) {
        Word word = new Word();
        word.setId(id);
        word.setPalabra(palabra);
        word.setUtilizada(utilizada);
        return word;
    }

    private GameInProgress createMockGameInProgress(Long id, Player player, Word word, String letrasIntentadas, int intentosRestantes) {
        GameInProgress gameInProgress = new GameInProgress();
        gameInProgress.setId(id);
        gameInProgress.setJugador(player);
        gameInProgress.setPalabra(word);
        gameInProgress.setLetrasIntentadas(letrasIntentadas);
        gameInProgress.setIntentosRestantes(intentosRestantes);
        gameInProgress.setFechaInicio(LocalDateTime.now());
        return gameInProgress;
    }

    @Test
    void testStartGame() {
        // Preparar datos de prueba usando helpers
        Player player = createMockPlayer(1L, "Test Player");
        Word word = createMockWord(1L, "PROGRAMADOR", false);
        GameInProgress gameInProgress = createMockGameInProgress(1L, player, word, "", 7);

        // Configurar mocks
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(wordRepository.findRandomWord()).thenReturn(Optional.of(word));
        when(gameInProgressRepository.findByJugadorAndPalabra(1L, 1L)).thenReturn(Optional.empty());
        when(gameInProgressRepository.save(any(GameInProgress.class))).thenReturn(gameInProgress);

        // Ejecutar
        GameResponseDTO response = gameService.startGame(1L);

        // Verificar
        assertNotNull(response);
        assertEquals("___________", response.getPalabraOculta());
        assertEquals(7, response.getIntentosRestantes());
        assertFalse(response.getPalabraCompleta());
        assertEquals(0, response.getPuntajeAcumulado());
        verify(wordRepository, times(1)).save(word);
        verify(gameInProgressRepository, times(1)).save(any(GameInProgress.class));
    }

    @Test
    void testMakeGuess() {
        // Preparar datos de prueba usando helpers
        Player player = createMockPlayer(1L, "Test Player");
        Word word = createMockWord(1L, "PROGRAMADOR", true);
        GameInProgress gameInProgress = createMockGameInProgress(1L, player, word, "", 7);

        // Configurar mocks
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(gameInProgressRepository.findByJugadorIdOrderByFechaInicioDesc(1L))
                .thenReturn(java.util.List.of(gameInProgress));
        when(gameInProgressRepository.save(any(GameInProgress.class))).thenReturn(gameInProgress);

        // Ejecutar
        GameResponseDTO response = gameService.makeGuess(1L, 'A');

        // Verificar
        assertNotNull(response);
        assertTrue(response.getPalabraOculta().contains("A"));
        assertTrue(response.getLetrasIntentadas().contains('A'));
        assertEquals(7, response.getIntentosRestantes());
        verify(gameInProgressRepository, times(1)).save(any(GameInProgress.class));
    }


    @Test
    void testGameWonAndSaved() {
        // Preparar datos de prueba usando helpers
        Player player = createMockPlayer(1L, "Test Player");
        Word word = createMockWord(1L, "HOLA", true);
        GameInProgress gameInProgress = createMockGameInProgress(1L, player, word, "H,O,L", 5);

        // Configurar mocks
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(gameInProgressRepository.findByJugadorIdOrderByFechaInicioDesc(1L))
                .thenReturn(java.util.List.of(gameInProgress));
        when(gameInProgressRepository.save(any(GameInProgress.class))).thenReturn(gameInProgress);

        // Ejecutar - letra final para completar la palabra
        GameResponseDTO response = gameService.makeGuess(1L, 'A');

        // Verificar
        assertNotNull(response);
        assertEquals("HOLA", response.getPalabraOculta());
        assertTrue(response.getPalabraCompleta());
        assertEquals(20, response.getPuntajeAcumulado());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameInProgressRepository, times(1)).delete(any(GameInProgress.class));
    }
}

