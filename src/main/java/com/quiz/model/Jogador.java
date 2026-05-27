package com.quiz.model;

import java.time.LocalDateTime;

public class Jogador {
    private int id;
    private String username;
    private String senha;
    private LocalDateTime createdAt;

    public Jogador() {}

    public Jogador(String username, String senha) {
        this.username = username;
        this.senha = senha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Jogador{id=" + id + ", username='" + username + "'}";
    }
}
