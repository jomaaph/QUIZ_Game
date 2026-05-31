package com.quiz.ui;

import com.quiz.model.Jogador;
import com.quiz.model.Partida;
import com.quiz.model.Pergunta;
import com.quiz.service.QuizService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

public class TelaQuiz extends JFrame {

    private final Jogador jogador;
    private final QuizService quizService = new QuizService();
    private List<Pergunta> perguntas;
    private Partida partida;

    private int indice = 0;
    private int acertos = 0;

    // Componentes
    private JLabel lblPergunta;
    private JLabel lblStatus;
    private JLabel lblTitulo;
    private JLabel lblFeedback;
    private JLabel lblTempo;
    private JButton[] botoes;
    private JButton btnProxima;
    private JProgressBar progressBar;

    // Timer
    private Timer timerPergunta;
    private int tempoRestante = 20;
    private static final int TEMPO_LIMITE = 20;

    // Sistema Elástico de Escala 16:9
    private double escala = 1.0;

    // Cores do Tema Escuro Customizado (Figma Neo-Dark)
    private final Color BG = new Color(15, 15, 25);
    private final Color CARD = new Color(30, 30, 46);
    private final Color TEXTO = Color.WHITE;
    private final Color AZUL = new Color(99, 102, 241);
    private final Color VERDE = new Color(34, 197, 94);
    private final Color VERMELHO = new Color(239, 68, 68);

    private final Color[] CORES_OPCOES = {
            new Color(244, 63, 94),   // Rosa/Vermelho Tech
            new Color(14, 165, 233),  // Azul Cyber
            new Color(16, 185, 129),  // Verde Matrix
            new Color(234, 179, 8)    // Amarelo Alerta
    };

    public TelaQuiz(Jogador jogador, int categoriaId, int qtd) {
        this.jogador = jogador;
        setTitle("Quiz Game - Painel de Operações");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 1. Calcula proporções dinâmicas 16:9 com base no monitor
        configurarTamanhoResponsivo();

        setLocationRelativeTo(null);
        setResizable(false);

        try {
            partida = quizService.iniciarPartida(jogador.getId(), categoriaId);
            perguntas = quizService.buscarPerguntas(categoriaId, qtd);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao iniciar o quiz: " + e.getMessage());
            dispose();
            return;
        }

        construirUI();
        exibirPergunta();
    }

    private void configurarTamanhoResponsivo() {
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        int larguraJanela = (int) (tamanhoTela.width * 0.80); // Ocupa 80% do monitor atual
        int alturaJanela = (larguraJanela * 9) / 16;          // Trava proporção 16:9

        if (larguraJanela < 1152) {
            larguraJanela = 1152;
            alturaJanela = 648;
        }

        setSize(larguraJanela, alturaJanela);
        this.escala = (double) larguraJanela / 1344.0;
    }

    private int esc(int pixels) {
        return (int) Math.round(pixels * escala);
    }

    private void construirUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);

        // --- CONTAINER CENTRAL ELÁSTICO ---
        JPanel container = new JPanel(new BorderLayout(0, esc(24))) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(24), esc(24)));

                g2.setColor(new Color(55, 55, 75));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, esc(24), esc(24)));
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(esc(32), esc(40), esc(32), esc(40)));

        // Define o dimensionamento do container proporcional à resolução calculada
        Dimension dimContainer = new Dimension(esc(1180), esc(660));
        container.setPreferredSize(dimContainer);

        // ==========================================
        // HEADER RESPONSIVO
        // ==========================================
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        lblTitulo = new JLabel("QUIZ SYSTEM");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, esc(28)));
        lblTitulo.setForeground(AZUL);

        lblStatus = new JLabel("", SwingConstants.RIGHT);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, esc(14)));
        lblStatus.setForeground(new Color(148, 163, 184));

        lblTempo = new JLabel("");
        lblTempo.setFont(new Font("Segoe UI", Font.BOLD, esc(30)));
        lblTempo.setForeground(AZUL);
        lblTempo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTempo.setPreferredSize(new Dimension(esc(80), esc(40)));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(esc(320), esc(10)));
        progressBar.setBackground(new Color(47, 47, 65));
        progressBar.setForeground(AZUL);
        progressBar.setBorderPainted(false);

        JPanel direitaHeader = new JPanel(new GridBagLayout());
        direitaHeader.setOpaque(false);
        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0; gbcHeader.gridy = 0; gbcHeader.anchor = GridBagConstraints.EAST;
        direitaHeader.add(lblStatus, gbcHeader);

        gbcHeader.gridy++;
        gbcHeader.insets = new Insets(esc(6), 0, 0, 0);
        direitaHeader.add(progressBar, gbcHeader);

        gbcHeader.gridy++;
        gbcHeader.insets = new Insets(esc(100), 0, 0, 0);
        direitaHeader.add(lblTempo, gbcHeader);

        JPanel esquerdaHeader = new JPanel(new GridBagLayout());
        esquerdaHeader.setOpaque(false);
        GridBagConstraints titleHeader = new GridBagConstraints();
        titleHeader.anchor = GridBagConstraints.WEST;
        titleHeader.insets = new Insets(esc(-100), 0, 0, 0);
        esquerdaHeader.add(lblTitulo, titleHeader);

        header.add(esquerdaHeader, BorderLayout.WEST);
        header.add(direitaHeader, BorderLayout.EAST);

        // ==========================================
        // ÁREA DE CONTEÚDO (PERGUNTA + RESPOSTAS)
        // ==========================================
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gbcCentro = new GridBagConstraints();
        gbcCentro.gridx = 0; gbcCentro.gridy = 0;
        gbcCentro.fill = GridBagConstraints.HORIZONTAL;
        gbcCentro.weightx = 1.0;

        // Bloco Arredondado do Card de Pergunta
        JPanel cardPergunta = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(38, 38, 58));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(16), esc(16)));

                g2.setColor(new Color(63, 63, 89));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, esc(16), esc(16)));
                g2.dispose();
            }
        };
        cardPergunta.setOpaque(false);
        cardPergunta.setBorder(new EmptyBorder(esc(32), esc(24), esc(32), esc(24)));

        lblPergunta = new JLabel("", SwingConstants.CENTER);
        lblPergunta.setForeground(TEXTO);
        lblPergunta.setFont(new Font("Segoe UI", Font.BOLD, esc(22)));
        cardPergunta.add(lblPergunta, BorderLayout.CENTER);
        centro.add(cardPergunta, gbcCentro);

        // Grid Adaptável de Alternativas (2x2)
        gbcCentro.gridy++;
        gbcCentro.insets = new Insets(esc(24), 0, 0, 0);

        JPanel gridAlternativas = new JPanel(new GridLayout(2, 2, esc(20), esc(20)));
        gridAlternativas.setOpaque(false);

        botoes = new JButton[4];
        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(12), esc(12)));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, esc(15)));
            btn.setForeground(Color.WHITE);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(esc(18), esc(24), esc(18), esc(24)));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setPreferredSize(new Dimension(esc(480), esc(72)));

            final int idx = i;
            btn.addActionListener(e -> responder(idx));
            botoes[i] = btn;
            gridAlternativas.add(btn);
        }
        centro.add(gridAlternativas, gbcCentro);

        // ==========================================
        // FOOTER (FEEDBACK + PRÓXIMA)
        // ==========================================
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        lblFeedback = new JLabel(" ", SwingConstants.LEFT);
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, esc(20)));

        btnProxima = new JButton("PRÓXIMA") {
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
        btnProxima.setOpaque(false);
        btnProxima.setContentAreaFilled(false);
        btnProxima.setBackground(AZUL);
        btnProxima.setForeground(Color.WHITE);
        btnProxima.setFocusPainted(false);
        btnProxima.setFont(new Font("Segoe UI", Font.BOLD, esc(14)));
        btnProxima.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProxima.setBorder(BorderFactory.createEmptyBorder(esc(12), esc(32), esc(12), esc(32)));
        btnProxima.setVisible(false);
        btnProxima.addActionListener(e -> proximaPergunta());

        footer.add(lblFeedback, BorderLayout.WEST);
        footer.add(btnProxima, BorderLayout.EAST);

        // --- COMPOSIÇÃO DOS ELEMENTOS ---
        container.add(header, BorderLayout.NORTH);
        container.add(centro, BorderLayout.CENTER);
        container.add(footer, BorderLayout.SOUTH);

        root.add(container);
        add(root);
    }

    private void exibirPergunta() {
        Pergunta p = perguntas.get(indice);

        lblStatus.setText("MÓDULO DE PERGUNTAS: " + (indice + 1) + " / " + perguntas.size() + "  |  ACERTOS: " + acertos);
        progressBar.setMaximum(perguntas.size());
        progressBar.setValue(indice + 1);

        // O HTML garante a quebra de linha automática nas caixas de texto responsivas
        lblPergunta.setText("<html><div style='text-align: center; width: " + esc(800) + "px;'>" + p.getEnunciado() + "</div></html>");

        String[] opcoes = { p.getOpa(), p.getOpb(), p.getOpc(), p.getOpd() };
        String[] letras = { "A", "B", "C", "D" };

        for (int i = 0; i < 4; i++) {
            botoes[i].setEnabled(true);
            botoes[i].setBackground(CORES_OPCOES[i]);
            botoes[i].setText(letras[i] + ")  " + opcoes[i]);
        }

        lblFeedback.setText(" ");
        btnProxima.setVisible(false);

        // Inicia o timer
        iniciarTimer();
    }

    private void responder(int idx) {
        // Para o timer quando o usuário responde
        pararTimer();

        Pergunta p = perguntas.get(indice);
        char[] letras = { 'a', 'b', 'c', 'd' };
        char resposta = letras[idx];
        boolean acertou;

        try {
            acertou = quizService.responder(partida.getIpartida(), p, resposta);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro operacional: " + e.getMessage());
            return;
        }

        for (JButton b : botoes) {
            b.setEnabled(false);
            // Escurece levemente as alternativas não selecionadas para destacar a resposta
            b.setBackground(new Color(40, 40, 55));
        }

        if (acertou) {
            acertos++;
            botoes[idx].setBackground(VERDE);
            lblFeedback.setForeground(VERDE);
            lblFeedback.setText("✓  RESPOSTA CORRETA");
        } else {
            botoes[idx].setBackground(VERMELHO);
            int correta = "abcd".indexOf(p.getRespostaCorreta());
            botoes[correta].setBackground(VERDE);
            lblFeedback.setForeground(VERMELHO);
            lblFeedback.setText("✕  RESPOSTA INCORRETA");
        }

        boolean ultima = (indice == perguntas.size() - 1);
        btnProxima.setText(ultima ? "VER RESULTADO" : "PRÓXIMA PERGUNTA");
        btnProxima.setVisible(true);
    }

    private void proximaPergunta() {
        pararTimer();
        indice++;
        if (indice >= perguntas.size()) {
            try {
                int pontuacao = quizService.buscarPontuacaoFinal(partida.getIpartida());
                new TelaResultado(jogador, acertos, perguntas.size(), pontuacao).setVisible(true);
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao obter pontuação: " + e.getMessage());
            }
        } else {
            exibirPergunta();
        }
    }

    private void iniciarTimer() {
        tempoRestante = TEMPO_LIMITE;
        atualizarLblTempo();

        if (timerPergunta != null) {
            timerPergunta.stop();
        }

        timerPergunta = new Timer(1000, e -> {
            tempoRestante--;
            atualizarLblTempo();

            if (tempoRestante <= 0) {
                pararTimer();
                marcarComoPerguntaErrada();
            }
        });
        timerPergunta.start();
    }

    private void pararTimer() {
        if (timerPergunta != null) {
            timerPergunta.stop();
        }
    }

    private void atualizarLblTempo() {
        if (tempoRestante <= 5) {
            lblTempo.setForeground(VERMELHO);
        } else {
            lblTempo.setForeground(AZUL);
        }
        lblTempo.setText(tempoRestante + "s");
    }

    private void marcarComoPerguntaErrada() {
        Pergunta p = perguntas.get(indice);

        try {
            // Registra a resposta como nula (tempo esgotado)
            quizService.registrarRespostaNula(partida.getIpartida(), p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar resposta: " + e.getMessage());
            return;
        }

        // Desabilita todos os botões
        for (JButton b : botoes) {
            b.setEnabled(false);
            b.setBackground(new Color(40, 40, 55));
        }

        int correta = "abcd".indexOf(p.getRespostaCorreta());
        botoes[correta].setBackground(VERDE);
        lblFeedback.setForeground(VERMELHO);
        lblFeedback.setText("✕  TEMPO ESGOTADO");

        boolean ultima = (indice == perguntas.size() - 1);
        btnProxima.setText(ultima ? "VER RESULTADO" : "PRÓXIMA PERGUNTA");
        btnProxima.setVisible(true);
    }
}