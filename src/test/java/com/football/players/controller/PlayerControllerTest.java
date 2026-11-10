package com.football.players.controller;

import com.football.players.model.Player;
import com.football.players.repository.PlayerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testGetAllPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testAddPlayer() throws Exception {
        mockMvc.perform(post("/api/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Lionel Messi",
                        "position": "Forward",
                        "team": "Inter Miami",
                        "jerseyNumber": 10
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lionel Messi"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testUpdatePlayer() throws Exception {
        Player player = new Player(
                "Messi", "Forward", "Barcelona", 10);
        repository.save(player);

        mockMvc.perform(put("/api/players/" + player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Messi",
                        "position": "Forward",
                        "team": "Inter Miami",
                        "jerseyNumber": 10
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team").value("Inter Miami"));
    }

    @Test
    void testDeletePlayer() throws Exception {
        Player player = new Player(
                "Ronaldo", "Forward", "Al Nassr", 7);
        repository.save(player);

        mockMvc.perform(delete("/api/players/" + player.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMissingPlayer() throws Exception {
        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isNotFound());
    }
}
