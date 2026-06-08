package com.quiz.dao;

import com.quiz.config.DatabaseConnection;
import com.quiz.model.Partida;

import java.sql.*;

public class PartidaDAO {

    public Partida criar(Partida partida) throws SQLException {
        String sql = "INSERT INTO partida (id_jogador, categoriaId) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, partida.getIdJogador());
            stmt.setInt(2, partida.getCategoriaId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) partida.setIpartida(rs.getInt(1));
            }
        }
        return partida;
    }

    public int buscarPontuacao(int ipartida) throws SQLException {
        String sql = "SELECT ponto FROM partida WHERE ipartida = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ipartida);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("ponto");
            }
        }
        return 0;
    }

    public void atualizarKwanzas(int ipartida, int kwanzas) throws SQLException {
        String sql = "UPDATE partida SET kwanzas = ? WHERE ipartida = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, kwanzas);
            stmt.setInt(2, ipartida);
            stmt.executeUpdate();
        }
    }
}
