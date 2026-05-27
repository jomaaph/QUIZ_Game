package com.quiz.model;

public class Categoria {
    private int categoriaId;
    private String categoria;

    public Categoria() {}

    public Categoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return "Categoria{id=" + categoriaId + ", nome='" + categoria + "'}";
    }
}
