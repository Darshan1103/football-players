
package com.football.players.service;

import com.football.players.model.Player;
import com.football.players.repository.PlayerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Get all players
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Get player by ID
    public Player getPlayerById(Long id) {
        Player player = playerRepository.findById(id);

        if (player == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found");
        }

        return player;
    }

    // Add a new player
    public Player addPlayer(Player player) {
        player.setId(null);
        return playerRepository.save(player);
    }

    // Update an existing player
    public Player updatePlayer(Long id, Player updatedPlayer) {
        Player player = getPlayerById(id);

        player.setName(updatedPlayer.getName());
        player.setPosition(updatedPlayer.getPosition());
        player.setTeam(updatedPlayer.getTeam());
        player.setJerseyNumber(updatedPlayer.getJerseyNumber());

        return playerRepository.save(player);
    }

    // Delete a player
    public void deletePlayer(Long id) {
        getPlayerById(id);
        playerRepository.deleteById(id);
    }
}
