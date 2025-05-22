package org.telemis.bowling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telemis.bowling.model.Game;
import org.telemis.bowling.service.GameService;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game", description = "The Ancient African Bowling Game API")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Create a new game", description = "Creates a new bowling game and returns its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game created successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<String> createGame() {
        String gameId = gameService.createGame();
        return ResponseEntity.ok(gameId);
    }

    @Operation(summary = "Add a player to the game", description = "Adds a new player to an existing game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Player added successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found"),
        @ApiResponse(responseCode = "400", description = "Game already started or invalid player name")
    })
    @PostMapping("/{gameId}/players")
    public ResponseEntity<Void> addPlayer(
            @Parameter(description = "ID of the game") @PathVariable String gameId,
            @Parameter(description = "Name of the player to add") @RequestParam String playerName) {
        gameService.addPlayer(gameId, playerName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Start the game", description = "Starts a game that has at least 2 players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game started successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found"),
        @ApiResponse(responseCode = "400", description = "Not enough players or game already started")
    })
    @PostMapping("/{gameId}/start")
    public ResponseEntity<Void> startGame(
            @Parameter(description = "ID of the game") @PathVariable String gameId) {
        gameService.startGame(gameId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Make a throw", description = "Records a throw for the current player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Throw recorded successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found"),
        @ApiResponse(responseCode = "400", description = "Invalid number of pins or game not started")
    })
    @PostMapping("/{gameId}/throw")
    public ResponseEntity<Void> makeThrow(
            @Parameter(description = "ID of the game") @PathVariable String gameId,
            @Parameter(description = "Number of pins knocked down (0-15)") @RequestParam int pins) {
        gameService.addThrow(gameId, pins);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get game state", description = "Retrieves the current state of the game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game state retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Game.class))),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(
            @Parameter(description = "ID of the game") @PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        return ResponseEntity.ok(game);
    }

    @Operation(summary = "Get scoreboard", description = "Retrieves the current scoreboard of the game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Scoreboard retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{gameId}/scoreboard")
    public List<Game.PlayerScore> getScoreboard(
            @Parameter(description = "ID of the game") @PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        return game.getScoreboard();
    }
}

