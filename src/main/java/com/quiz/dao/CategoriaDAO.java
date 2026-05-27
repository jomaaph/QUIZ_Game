package com.quiz.dao;

import com.quiz.config.DatabaseConnection;
import com.quiz.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setCategoriaId(rs.getInt("categoriaId"));
                c.setCategoria(rs.getString("categoria"));
                lista.add(c);
            }
        }
        return lista;
    }
}
