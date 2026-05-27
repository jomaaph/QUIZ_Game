package com.quiz.model;

public class PartidaPergunta {
    private int id;
    private int ipartida;
    private int idPergunta;
    private Character respostaDada;
    private boolean acertou;

    public PartidaPergunta() {}

    public PartidaPergunta(int ipartida, int idPergunta, char respostaDada, boolean acertou) {
        this.ipartida = ipartida;
        this.idPergunta = idPergunta;
        this.respostaDada = respostaDada;
        this.acertou = acertou;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIpartida() { return ipartida; }
    public void setIpartida(int ipartida) { this.ipartida = ipartida; }

    public int getIdPergunta() { return idPergunta; }
    public void setIdPergunta(int idPergunta) { this.idPergunta = idPergunta; }

    public Character getRespostaDada() { return respostaDada; }
    public void setRespostaDada(Character respostaDada) { this.respostaDada = respostaDada; }

    public boolean isAcertou() { return acertou; }
    public void setAcertou(boolean acertou) { this.acertou = acertou; }
}
