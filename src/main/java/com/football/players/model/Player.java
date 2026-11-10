package com.football.players.model;

public class Player {

    private Long id;
    private String name;
    private String position;
    private String team;
    private int jerseyNumber;

    public Player() {
    }

    public Player(String name, String position, String team, int jerseyNumber) {
        this.name = name;
        this.position = position;
        this.team = team;
        this.jerseyNumber = jerseyNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }
}
