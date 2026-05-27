package com.quiz.dao;

import com.quiz.config.DatabaseConnection;
import com.quiz.model.Pergunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerguntaDAO {

    public List<Pergunta> buscarAleatoriasPorCategoria(int categoriaId, int limite) throws SQLException {
        List<Pergunta> lista = new ArrayList<>();
        String sql = "SELECT * FROM perguntas WHERE categoriaId = ? ORDER BY RAND() LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            stmt.setInt(2, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Pergunta mapear(ResultSet rs) throws SQLException {
        Pergunta p = new Pergunta();
        p.setIdPergunta(rs.getInt("id_pergunta"));
        p.setEnunciado(rs.getString("enunciado"));
        p.setOpa(rs.getString("opa"));
        p.setOpb(rs.getString("opb"));
        p.setOpc(rs.getString("opc"));
        p.setOpd(rs.getString("opd"));
        p.setRespostaCorreta(rs.getString("respostaCorreta").charAt(0));
        p.setCategoriaId(rs.getInt("categoriaId"));
        return p;
    }
}
