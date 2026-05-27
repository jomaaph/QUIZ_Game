package com.quiz.service;

import com.quiz.dao.JogadorDAO;
import com.quiz.model.Jogador;
import com.quiz.util.SenhaUtil;

import java.sql.SQLException;
import java.util.Optional;

public class JogadorService {

    private final JogadorDAO jogadorDAO = new JogadorDAO();

    public Jogador cadastrar(String username, String senha) throws SQLException {
        Optional<Jogador> existente = jogadorDAO.buscarPorUsername(username);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Username ja esta em uso.");
        }
        Jogador jogador = new Jogador(username, SenhaUtil.hash(senha));
        return jogadorDAO.salvar(jogador);
    }

    public Jogador autenticar(String username, String senha) throws SQLException {
        Jogador jogador = jogadorDAO.buscarPorUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));

        if (!SenhaUtil.verificar(senha, jogador.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }
        return jogador;
    }
}
