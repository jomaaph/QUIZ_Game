package com.quiz.dao;

import com.quiz.config.DatabaseConnection;
import com.quiz.model.Jogador;

import java.sql.*;
import java.util.Optional;

public class JogadorDAO {

    public Jogador salvar(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO jogador (username, senha) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, jogador.getUsername());
            stmt.setString(2, jogador.getSenha());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) jogador.setId(rs.getInt(1));
            }
        }
        return jogador;
    }

    public Optional<Jogador> buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM jogador WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Jogador> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM jogador WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    private Jogador mapear(ResultSet rs) throws SQLException {
        Jogador j = new Jogador();
        j.setId(rs.getInt("id"));
        j.setUsername(rs.getString("username"));
        j.setSenha(rs.getString("senha"));
        j.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return j;
    }
}
