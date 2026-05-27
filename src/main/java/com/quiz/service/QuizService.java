package com.quiz.service;

import com.quiz.dao.*;
import com.quiz.model.*;

import java.sql.SQLException;
import java.util.List;

public class QuizService {

    private final PerguntaDAO perguntaDAO       = new PerguntaDAO();
    private final PartidaDAO partidaDAO         = new PartidaDAO();
    private final PartidaPerguntaDAO ppDAO      = new PartidaPerguntaDAO();
    private final CategoriaDAO categoriaDAO     = new CategoriaDAO();

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listarTodas();
    }

    public Partida iniciarPartida(int idJogador, int categoriaId) throws SQLException {
        Partida partida = new Partida(idJogador, categoriaId);
        return partidaDAO.criar(partida);
    }

    public List<Pergunta> buscarPerguntas(int categoriaId, int quantidade) throws SQLException {
        return perguntaDAO.buscarAleatoriasPorCategoria(categoriaId, quantidade);
    }

    public boolean responder(int ipartida, Pergunta pergunta, char respostaDada) throws SQLException {
        boolean acertou = Character.toLowerCase(respostaDada) == pergunta.getRespostaCorreta();
        PartidaPergunta pp = new PartidaPergunta(ipartida, pergunta.getIdPergunta(), respostaDada, acertou);
        ppDAO.salvarResposta(pp);
        return acertou;
    }

    public int buscarPontuacaoFinal(int ipartida) throws SQLException {
        return partidaDAO.buscarPontuacao(ipartida);
    }
}
