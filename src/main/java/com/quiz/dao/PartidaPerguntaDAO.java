package com.quiz.dao;

import com.quiz.config.DatabaseConnection;
import com.quiz.model.PartidaPergunta;

import java.sql.*;
import java.sql.Types;

public class PartidaPerguntaDAO {

    public void salvarResposta(PartidaPergunta pp) throws SQLException {
        String sql = "INSERT INTO partida_pergunta (ipartida, id_pergunta, respostaDada, acertou) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pp.getIpartida());
            stmt.setInt(2, pp.getIdPergunta());
            if (pp.getRespostaDada() == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, pp.getRespostaDada().toString());
            }
            stmt.setBoolean(4, pp.isAcertou());
            stmt.executeUpdate();
        }
    }
}
