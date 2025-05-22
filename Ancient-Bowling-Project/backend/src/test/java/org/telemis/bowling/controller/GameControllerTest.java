package org.telemis.bowling.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.telemis.bowling.model.Game;
import org.telemis.bowling.service.GameService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    void shouldCreateGame() throws Exception {
        String gameId = "test-game-id";
        when(gameService.createGame()).thenReturn(gameId);

        mockMvc.perform(post("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().string(gameId));
    }

    @Test
    void shouldAddPlayerToGame() throws Exception {
        String gameId = "test-game-id";
        String playerName = "Alice";
        
        doNothing().when(gameService).addPlayer(anyString(), anyString());

        mockMvc.perform(post("/api/games/{gameId}/players", gameId)
                        .param("playerName", playerName))
                .andExpect(status().isOk());
    }

    @Test
    void shouldStartGame() throws Exception {
        String gameId = "test-game-id";
        
        doNothing().when(gameService).startGame(anyString());

        mockMvc.perform(post("/api/games/{gameId}/start", gameId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldMakeThrow() throws Exception {
        String gameId = "test-game-id";
        int pins = 5;
        
        doNothing().when(gameService).addThrow(anyString(), anyInt());

        mockMvc.perform(post("/api/games/{gameId}/throw", gameId)
                        .param("pins", String.valueOf(pins)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetGame() throws Exception {
        String gameId = "test-game-id";
        Game game = new Game();
        game.addPlayer("Player1");
        game.addPlayer("Player2");
        
        when(gameService.getGame(anyString())).thenReturn(game);

        mockMvc.perform(get("/api/games/{gameId}", gameId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetScoreboard() throws Exception {
        String gameId = "test-game-id";
        Game game = new Game();
        game.addPlayer("Player1");
        game.addPlayer("Player2");
        game.start();
        
        when(gameService.getGame(anyString())).thenReturn(game);

        mockMvc.perform(get("/api/games/{gameId}/scoreboard", gameId))
                .andExpect(status().isOk());
    }
}
