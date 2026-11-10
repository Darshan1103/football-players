package com.football.players.repository;

import com.football.players.model.Player;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PlayerRepository {

    private final ConcurrentHashMap<Long, Player> players
            = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Player> findAll() {
        return new ArrayList<>(players.values());
    }

    public Player findById(Long id) {
        return players.get(id);
    }

    public Player save(Player player) {
        if (player.getId() == null) {
            player.setId(idGenerator.incrementAndGet());
        }

        players.put(player.getId(), player);
        return player;
    }

    public void deleteById(Long id) {
        players.remove(id);
    }

    public void deleteAll() {
        players.clear();
        idGenerator.set(0);
    }
}
