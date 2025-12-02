package com.example.demobase.service;

import com.example.demobase.dto.ScoreboardDTO;
import com.example.demobase.model.Game;
import com.example.demobase.model.Player;
import com.example.demobase.repository.GameRepository;
import com.example.demobase.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreboardServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ScoreboardService scoreboardService;

    @Test
    void testGetScoreboard() {
        // Preparar datos de prueba
        Player player1 = new Player();
        player1.setId(1L);
        player1.setNombre("Jugador 1");
        player1.setFecha(LocalDate.now());

        Player player2 = new Player();
        player2.setId(2L);
        player2.setNombre("Jugador 2");
        player2.setFecha(LocalDate.now());

        Game game1 = new Game();
        game1.setJugador(player1);
        game1.setResultado("GANADO");
        game1.setPuntaje(20);

        Game game2 = new Game();
        game2.setJugador(player2);
        game2.setResultado("PERDIDO");
        game2.setPuntaje(5);

        // Configurar mocks
        when(playerRepository.findAll()).thenReturn(List.of(player1, player2));
        when(gameRepository.findByJugador(player1)).thenReturn(List.of(game1));
        when(gameRepository.findByJugador(player2)).thenReturn(List.of(game2));

        // Ejecutar
        List<ScoreboardDTO> result = scoreboardService.getScoreboard();

        // Verificar
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(20, result.get(0).getPuntajeTotal());
        assertEquals(5, result.get(1).getPuntajeTotal());
        verify(playerRepository, times(1)).findAll();
        verify(gameRepository, times(2)).findByJugador(any(Player.class));
    }

    @Test
    void testGetScoreboardByPlayer() {
        // Preparar datos de prueba
        Player player = new Player();
        player.setId(1L);
        player.setNombre("Test Player");
        player.setFecha(LocalDate.now());

        Game game1 = new Game();
        game1.setJugador(player);
        game1.setResultado("GANADO");
        game1.setPuntaje(20);

        Game game2 = new Game();
        game2.setJugador(player);
        game2.setResultado("PERDIDO");
        game2.setPuntaje(5);

        // Configurar mocks
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(gameRepository.findByJugador(player)).thenReturn(List.of(game1, game2));

        // Ejecutar
        ScoreboardDTO result = scoreboardService.getScoreboardByPlayer(1L);

        // Verificar
        assertNotNull(result);
        assertEquals(1L, result.getIdJugador());
        assertEquals("Test Player", result.getNombreJugador());
        assertEquals(25, result.getPuntajeTotal());
        assertEquals(2, result.getPartidasJugadas());
        assertEquals(1, result.getPartidasGanadas());
        assertEquals(1, result.getPartidasPerdidas());
        verify(playerRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findByJugador(player);
    }
}

