package com.football.players.service;

import com.football.players.model.Player;
import com.football.players.repository.PlayerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest {

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        PlayerRepository repository = new PlayerRepository();
        playerService = new PlayerService(repository);
    }

    @Test
    void testAddPlayer() {
        Player player = new Player(
                "Lionel Messi", "Forward", "Inter Miami", 10);

        Player saved = playerService.addPlayer(player);

        assertNotNull(saved.getId());
        assertEquals("Lionel Messi", saved.getName());
    }

    @Test
    void testGetAllPlayers() {
        playerService.addPlayer(
                new Player("Messi", "Forward", "Inter Miami", 10));

        List<Player> players = playerService.getAllPlayers();

        assertEquals(1, players.size());
    }

    @Test
    void testGetPlayerById() {
        Player saved = playerService.addPlayer(
                new Player("Ronaldo", "Forward", "Al Nassr", 7));

        Player found = playerService.getPlayerById(saved.getId());

        assertEquals("Ronaldo", found.getName());
    }

    @Test
    void testUpdatePlayer() {
        Player saved = playerService.addPlayer(
                new Player("Messi", "Forward", "Barcelona", 10));

        Player updated = new Player(
                "Messi", "Forward", "Inter Miami", 10);

        Player result = playerService.updatePlayer(
                saved.getId(), updated);

        assertEquals("Inter Miami", result.getTeam());
    }

    @Test
    void testDeletePlayer() {
        Player saved = playerService.addPlayer(
                new Player("Neymar", "Forward", "Santos", 11));

        playerService.deletePlayer(saved.getId());

        assertTrue(playerService.getAllPlayers().isEmpty());
    }

    @Test
    void testPlayerNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> playerService.getPlayerById(999L));
    }
}
