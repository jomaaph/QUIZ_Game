package com.quiz.model;

import java.time.LocalDateTime;

public class Partida {
    private int ipartida;
    private LocalDateTime data;
    private int ponto;
    private int idJogador;
    private int categoriaId;

    public Partida() {}

    public Partida(int idJogador, int categoriaId) {
        this.idJogador = idJogador;
        this.categoriaId = categoriaId;
    }

    public int getIpartida() { return ipartida; }
    public void setIpartida(int ipartida) { this.ipartida = ipartida; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public int getPonto() { return ponto; }
    public void setPonto(int ponto) { this.ponto = ponto; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
}
