package com.quiz.model;

public class Pergunta {
    private int idPergunta;
    private String enunciado;
    private String opa;
    private String opb;
    private String opc;
    private String opd;
    private char respostaCorreta;
    private int categoriaId;

    public Pergunta() {}

    public int getIdPergunta() { return idPergunta; }
    public void setIdPergunta(int idPergunta) { this.idPergunta = idPergunta; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getOpa() { return opa; }
    public void setOpa(String opa) { this.opa = opa; }

    public String getOpb() { return opb; }
    public void setOpb(String opb) { this.opb = opb; }

    public String getOpc() { return opc; }
    public void setOpc(String opc) { this.opc = opc; }

    public String getOpd() { return opd; }
    public void setOpd(String opd) { this.opd = opd; }

    public char getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(char respostaCorreta) { this.respostaCorreta = respostaCorreta; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public void exibir() {
        System.out.println("\n" + enunciado);
        System.out.println("  A) " + opa);
        System.out.println("  B) " + opb);
        System.out.println("  C) " + opc);
        System.out.println("  D) " + opd);
    }
}
