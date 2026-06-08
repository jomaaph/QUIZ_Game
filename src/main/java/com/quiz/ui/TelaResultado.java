package com.quiz.ui;

import com.quiz.model.Jogador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TelaResultado extends JFrame {

    // Paleta de Cores do Tema (Figma Neo-Dark Consistente)
    private final Color COR_BG      = new Color(15, 15, 25);
    private final Color COR_CARD    = new Color(30, 30, 46);
    private final Color COR_AZUL    = new Color(99, 102, 241);
    private final Color COR_VERDE   = new Color(34, 197, 94);
    private final Color COR_TEXTO   = new Color(205, 214, 244);
    private final Color COR_LARANJA = new Color(251, 146, 60);

    private double escala = 1.0;

    public TelaResultado(Jogador jogador, int acertos, int total, int pontuacao) {
        setTitle("Quiz Game - Desempenho");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Configura tamanho da janela mantendo a proporção 16:9 responsiva
        configurarTamanhoResponsivo();

        setLocationRelativeTo(null);
        setResizable(false);
        construirUI(jogador, acertos, total, pontuacao);
    }

    private void configurarTamanhoResponsivo() {
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        int larguraJanela = (int) (tamanhoTela.width * 0.70); // Ocupa 70% do monitor atual
        int alturaJanela = (larguraJanela * 9) / 16;          // Força proporção 16:9

        if (larguraJanela < 1024) {
            larguraJanela = 1024;
            alturaJanela = 576;
        }

        setSize(larguraJanela, alturaJanela);
        this.escala = (double) larguraJanela / 1344.0;
    }

    private int esc(int pixels) {
        return (int) Math.round(pixels * escala);
    }

    private void construirUI(Jogador jogador, int acertos, int total, int pontuacao) {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(COR_BG);

        // --- CARD CENTRAL ---
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(24), esc(24)));

                g2.setColor(new Color(55, 55, 75));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, esc(24), esc(24)));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(esc(40), esc(60), esc(40), esc(60)));
        card.setPreferredSize(new Dimension(esc(620), esc(500)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Emoji Dinâmico com base na Performance
        double pct = (double) acertos / total;
        String emoji = pct >= 0.8 ? "🏆" : pct >= 0.5 ? "⚡" : "🎯";

        JLabel icone = new JLabel(emoji, SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(54)));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, esc(12), 0);
        card.add(icone, gbc);

        // 2. Título da Conclusão
        JLabel titulo = new JLabel("Partida Concluída!", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, esc(26)));
        titulo.setForeground(COR_AZUL);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, esc(4), 0);
        card.add(titulo, gbc);

        // 3. Identificação do Usuário
        JLabel lblJogador = new JLabel("Jogador: " + jogador.getUsername().toUpperCase(), SwingConstants.CENTER);
        lblJogador.setFont(new Font("Segoe UI", Font.PLAIN, esc(14)));
        lblJogador.setForeground(new Color(148, 163, 184));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, esc(20), 0);
        card.add(lblJogador, gbc);

        // 4. Divisor Estilizado
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(63, 63, 89));
        gbc.gridy = 3; gbc.insets = new Insets(0, esc(20), esc(20), esc(20));
        card.add(sep, gbc);

        // 5. Bloco de Dados - Acertos
        JLabel lblAcertos = new JLabel("Acertos: " + acertos + " / " + total, SwingConstants.CENTER);
        lblAcertos.setFont(new Font("Segoe UI", Font.BOLD, esc(22)));
        lblAcertos.setForeground(COR_VERDE);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, esc(6), 0);
        card.add(lblAcertos, gbc);

        // 6. Bloco de Dados - Percentual de Aproveitamento
        int perc = (int) (pct * 100);
        JLabel lblPerc = new JLabel("Aproveitamento: " + perc + "%", SwingConstants.CENTER);
        lblPerc.setFont(new Font("Segoe UI", Font.PLAIN, esc(15)));
        lblPerc.setForeground(COR_TEXTO);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, esc(6), 0);
        card.add(lblPerc, gbc);

        // 7. Bloco de Dados - Pontuação Acumulada
        JLabel lblPontos = new JLabel("Pontuação Final: " + pontuacao + " KZ", SwingConstants.CENTER);
        lblPontos.setFont(new Font("Segoe UI", Font.BOLD, esc(16)));
        lblPontos.setForeground(COR_LARANJA);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, esc(32), 0);
        card.add(lblPontos, gbc);

        // 8. Painel Lateral de Ações (Botões)
        JPanel painelBtns = new JPanel(new GridLayout(1, 2, esc(16), 0));
        painelBtns.setOpaque(false);

        JButton btnNova = new JButton("NOVA PARTIDA") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(10), esc(10)));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estilizarBtn(btnNova, COR_AZUL, COR_BG);
        btnNova.addActionListener(e -> {
            // Retorna o jogador diretamente para a seleção de módulos sem deslogar
            new TelaCategoria(jogador).setVisible(true);
            dispose();
        });

        JButton btnSair = new JButton("SAIR DO JOGO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(10), esc(10)));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estilizarBtn(btnSair, new Color(239, 68, 68), Color.WHITE);
        btnSair.addActionListener(e -> System.exit(0));

        painelBtns.add(btnNova);
        painelBtns.add(btnSair);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(painelBtns, gbc);

        root.add(card);
        add(root);
    }

    private void stylizeButtonTextOnly(JButton b, Color corFundo, Color corTexto) {
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBackground(corFundo);
        b.setForeground(corTexto);
        b.setFont(new Font("Segoe UI", Font.BOLD, esc(13)));
        b.setBorder(BorderFactory.createEmptyBorder(esc(12), esc(24), esc(12), esc(24)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Método auxiliar para manter compatibilidade com a assinatura do seu método original
    private void estilizarBtn(JButton b, Color cor) {
        stylizeButtonTextOnly(b, cor, new Color(15, 15, 25));
    }

    private void estilizarBtn(JButton b, Color corFundo, Color corTexto) {
        stylizeButtonTextOnly(b, corFundo, corTexto);
    }
}