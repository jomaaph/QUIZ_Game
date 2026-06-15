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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TelaQuiz extends JFrame {

    private final Jogador jogador;
    private final QuizService quizService = new QuizService();
    private List<Pergunta> perguntas;
    private Partida partida;

    private int indice = 0;
    private int acertos = 0;
    private int placar = 0;

    // Ajudas (cada uma pode ser usada apenas 1 vez)
    private boolean ajudaPularUsada = false;
    private boolean ajudaEliminarUsada = false;
    private boolean ajudaTempoUsada = false;

    // Componentes UI
    private JLabel lblPergunta;
    private JLabel lblStatus;
    private JLabel lblTitulo;
    private JLabel lblPlacar;
    private JLabel lblFeedback;
    private JLabel lblTempo;
    private JButton[] botoes;
    private JButton btnProxima;
    private JProgressBar progressBar;

    // Botões de ajuda
    private JButton btnAjudaPular;
    private JButton btnAjudaEliminar;
    private JButton btnAjudaTempo;

    // Timer
    private Timer timerPergunta;
    private int tempoRestante = 20;
    private static final int TEMPO_LIMITE = 20;
    private static final int BONUS_TEMPO = 15;

    // Escala responsiva
    private double escala = 1.0;

    // Paleta Neo-Dark
    private final Color BG        = new Color(10, 10, 20);
    private final Color CARD      = new Color(22, 22, 38);
    private final Color CARD2     = new Color(30, 30, 50);
    private final Color BORDA     = new Color(50, 50, 75);
    private final Color TEXTO     = Color.WHITE;
    private final Color AZUL      = new Color(99, 102, 241);
    private final Color VERDE     = new Color(34, 197, 94);
    private final Color VERMELHO  = new Color(239, 68, 68);
    private final Color AMARELO   = new Color(34, 197, 94);
    private final Color CINZA_SUB = new Color(100, 116, 139);

    private final Color[] CORES_OPCOES = {
            new Color(99, 102, 241),
            new Color(99, 102, 241),
            new Color(99, 102, 241),
            new Color(99, 102, 241)

    };

    // Ajuda: referência para os indicadores de "usada"
    private JLabel lblIconePular;
    private JLabel lblIconeEliminar;
    private JLabel lblIconeTempo;

    public TelaQuiz(Jogador jogador, int categoriaId, int qtd) {
        this.jogador = jogador;
        setTitle("Quiz Game — Painel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int larg = (int) (tela.width * 0.82);
        int alt  = (larg * 9) / 16;
        if (larg < 1152) { larg = 1152; alt = 648; }
        setSize(larg, alt);
        this.escala = (double) larg / 1344.0;
    }

    private int esc(int px) { return (int) Math.round(px * escala); }

    // ───────────────────────────── BUILD UI ───────────────────────────────
    private void construirUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);

        JPanel container = new JPanel(new BorderLayout(0, esc(18))) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(24),esc(24)));
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(24),esc(24)));
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(esc(28),esc(38),esc(24),esc(38)));
        container.setPreferredSize(new Dimension(esc(1200), esc(670)));

        container.add(buildHeader(),    BorderLayout.NORTH);
        container.add(buildCentro(),    BorderLayout.CENTER);
        container.add(buildFooter(),    BorderLayout.SOUTH);

        root.add(container);
        add(root);
    }

    // ──────────────────────────── HEADER ──────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(esc(12), 0));
        header.setOpaque(false);

        // Esquerda: título + placar
        JPanel esq = new JPanel();
        esq.setLayout(new BoxLayout(esq, BoxLayout.Y_AXIS));
        esq.setOpaque(false);

        lblTitulo = new JLabel("⚡ QUIZ SYSTEM");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, esc(26)));
        lblTitulo.setForeground(AZUL);

        lblPlacar = new JLabel("PLACAR: 0 KZ");
        lblPlacar.setFont(new Font("Segoe UI", Font.BOLD, esc(20)));
        lblPlacar.setForeground(AMARELO);

        esq.add(lblTitulo);
        esq.add(Box.createVerticalStrut(esc(4)));
        esq.add(lblPlacar);

        // Centro: Timer circular + Progress
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.CENTER;

        lblTempo = new JLabel("20s", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Arco de fundo
                g2.setColor(CARD2);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(getForeground().darker());
                g2.setStroke(new BasicStroke(esc(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(esc(4), esc(4), getWidth()-esc(8), getHeight()-esc(8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTempo.setFont(new Font("Segoe UI", Font.BOLD, esc(22)));
        lblTempo.setForeground(AZUL);
        lblTempo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTempo.setVerticalAlignment(SwingConstants.CENTER);
        int timerSz = esc(80);
        lblTempo.setPreferredSize(new Dimension(timerSz, timerSz));
        lblTempo.setOpaque(false);

        centro.add(lblTempo, g);
        g.gridy++;
        g.insets = new Insets(esc(6),0,0,0);
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(esc(280), esc(8)));
        progressBar.setBackground(new Color(40,40,60));
        progressBar.setForeground(AZUL);
        progressBar.setBorderPainted(false);
        centro.add(progressBar, g);

        // Direita: Status + botões de ajuda
        JPanel dir = new JPanel();
        dir.setLayout(new BoxLayout(dir, BoxLayout.Y_AXIS));
        dir.setOpaque(false);

        lblStatus = new JLabel("", SwingConstants.RIGHT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, esc(12)));
        lblStatus.setForeground(CINZA_SUB);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblAjudasTitulo = new JLabel("AJUDAS", SwingConstants.RIGHT);
        lblAjudasTitulo.setFont(new Font("Segoe UI", Font.BOLD, esc(11)));
        lblAjudasTitulo.setForeground(CINZA_SUB);
        lblAjudasTitulo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel painelAjudas = buildPainelAjudas();
        painelAjudas.setAlignmentX(Component.RIGHT_ALIGNMENT);

        dir.add(lblStatus);
        dir.add(Box.createVerticalStrut(esc(8)));
        dir.add(lblAjudasTitulo);
        dir.add(Box.createVerticalStrut(esc(4)));
        dir.add(painelAjudas);

        header.add(esq,    BorderLayout.WEST);
        header.add(centro, BorderLayout.CENTER);
        header.add(dir,    BorderLayout.EAST);

        return header;
    }

    // ───────────────────── PAINEL DE AJUDAS ───────────────────────────────
    private JPanel buildPainelAjudas() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, esc(8), 0));
        p.setOpaque(false);

        btnAjudaPular   = criarBotaoAjuda("⏭", "PULAR",       new Color(99, 102, 241));
        btnAjudaEliminar= criarBotaoAjuda("✂",  "ELIMINAR 2",  new Color(217, 70, 239));
        btnAjudaTempo   = criarBotaoAjuda("⏱",  "+TEMPO",      new Color(16, 185, 129));

        btnAjudaPular.addActionListener(e    -> usarAjudaPular());
        btnAjudaEliminar.addActionListener(e -> usarAjudaEliminar());
        btnAjudaTempo.addActionListener(e    -> usarAjudaTempo());

        p.add(btnAjudaPular);
        p.add(btnAjudaEliminar);
        p.add(btnAjudaTempo);
        return p;
    }

    private JButton criarBotaoAjuda(String emoji, String label, Color cor) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isEnabled() ? getBackground() : new Color(35,35,50);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(10),esc(10)));
                if (isEnabled()) {
                    g2.setColor(bg.brighter());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(10),esc(10)));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBackground(cor);
        btn.setBorder(BorderFactory.createEmptyBorder(esc(6), esc(10), esc(6), esc(10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lEmoji = new JLabel(emoji, SwingConstants.CENTER);
        lEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(18)));
        lEmoji.setForeground(Color.WHITE);
        lEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lLabel = new JLabel(label, SwingConstants.CENTER);
        lLabel.setFont(new Font("Segoe UI", Font.BOLD, esc(9)));
        lLabel.setForeground(Color.WHITE);
        lLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.add(lEmoji);
        btn.add(lLabel);
        btn.setPreferredSize(new Dimension(esc(72), esc(52)));
        return btn;
    }

    // ──────────────────────────── CENTRO ──────────────────────────────────
    private JPanel buildCentro() {
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Card da pergunta
        JPanel cardPergunta = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(35,35,58),
                        0, getHeight(), new Color(28,28,45));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(16),esc(16)));
                g2.setColor(new Color(70,70,100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(16),esc(16)));
                g2.dispose();
            }
        };
        cardPergunta.setOpaque(false);
        cardPergunta.setBorder(new EmptyBorder(esc(26), esc(28), esc(26), esc(28)));

        lblPergunta = new JLabel("", SwingConstants.CENTER);
        lblPergunta.setForeground(TEXTO);
        lblPergunta.setFont(new Font("Segoe UI", Font.BOLD, esc(21)));
        cardPergunta.add(lblPergunta, BorderLayout.CENTER);
        centro.add(cardPergunta, gbc);

        // Grid de alternativas 2x2
        gbc.gridy++;
        gbc.insets = new Insets(esc(20), 0, 0, 0);

        JPanel grid = new JPanel(new GridLayout(2, 2, esc(16), esc(16)));
        grid.setOpaque(false);

        botoes = new JButton[4];
        String[] letras = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            final String letra = letras[i];
            final Color corBase = CORES_OPCOES[i];

            JButton btn = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = isEnabled() ? getBackground() : new Color(30,30,45);
                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(12),esc(12)));
                    // Badge da letra
                    int bSz = esc(28);
                    g2.setColor(isEnabled() ? bg.darker() : new Color(25,25,40));
                    g2.fill(new RoundRectangle2D.Float(esc(12),getHeight()/2-bSz/2, bSz, bSz, esc(6),esc(6)));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, esc(14)));
            btn.setForeground(Color.WHITE);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(esc(16), esc(52), esc(16), esc(20)));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setPreferredSize(new Dimension(esc(480), esc(68)));
            btn.setBackground(corBase);
            btn.addActionListener(e -> responder(idx));
            botoes[i] = btn;
            grid.add(btn);
        }

        centro.add(grid, gbc);
        return centro;
    }

    // ──────────────────────────── FOOTER ──────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(esc(16), 0));
        footer.setOpaque(false);

        lblFeedback = new JLabel(" ", SwingConstants.LEFT);
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, esc(18)));

        btnProxima = new JButton("PRÓXIMA") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL, getWidth(), 0, new Color(139, 92, 246));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(10),esc(10)));
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

        footer.add(lblFeedback, BorderLayout.CENTER);
        footer.add(btnProxima,  BorderLayout.EAST);
        return footer;
    }

    // ───────────────────────── LÓGICA DA PERGUNTA ─────────────────────────
    private void exibirPergunta() {
        Pergunta p = perguntas.get(indice);
        lblStatus.setText("PERGUNTA " + (indice + 1) + " / " + perguntas.size() + "  •  ACERTOS: " + acertos);
        progressBar.setMaximum(perguntas.size());
        progressBar.setValue(indice + 1);

        lblPergunta.setText("<html><div style='text-align:center;width:" + esc(820) + "px;'>" + p.getEnunciado() + "</div></html>");

        String[] opcoes = {p.getOpa(), p.getOpb(), p.getOpc(), p.getOpd()};
        String[] letras = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            botoes[i].setEnabled(true);
            botoes[i].setBackground(CORES_OPCOES[i]);
            botoes[i].setVisible(true);
            botoes[i].setText(letras[i] + "    " + opcoes[i]);
        }

        lblFeedback.setText(" ");
        btnProxima.setVisible(false);
        atualizarLblPlacar();
        atualizarEstadoBotoesAjuda();
        iniciarTimer();
    }

    private void responder(int idx) {
        pararTimer();
        Pergunta p = perguntas.get(indice);
        char[] letras = {'a','b','c','d'};
        char resposta = letras[idx];
        boolean acertou;

        try {
            acertou = quizService.responder(partida.getIpartida(), p, resposta);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            return;
        }

        for (JButton b : botoes) b.setEnabled(false);
        desfocarBotoes();

        if (acertou) {
            acertos++;
            int pts = 100 + tempoRestante * 10;
            placar += pts;
            atualizarLblPlacar();
            botoes[idx].setBackground(VERDE);
            mostrarFeedback("✓  CORRETO  (+" + pts + " KZ)", VERDE);
        } else {
            botoes[idx].setBackground(VERMELHO);
            int correta = "abcd".indexOf(p.getRespostaCorreta());
            if (correta >= 0) botoes[correta].setBackground(VERDE);
            mostrarFeedback("✕  INCORRETO", VERMELHO);
        }

        try { quizService.atualizarKwanzasPartida(partida.getIpartida(), placar); }
        catch (SQLException ex) { /* silencioso */ }

        boolean ultima = (indice == perguntas.size() - 1);
        btnProxima.setText(ultima ? "VER RESULTADO  →" : "PRÓXIMA  →");
        btnProxima.setVisible(true);
    }

    private void proximaPergunta() {
        pararTimer();
        indice++;
        if (indice >= perguntas.size()) {
            new TelaResultado(jogador, acertos, perguntas.size(), placar).setVisible(true);
            dispose();
        } else {
            exibirPergunta();
        }
    }

    // ────────────────────────── TIMER ─────────────────────────────────────
    private void iniciarTimer() {
        tempoRestante = TEMPO_LIMITE;
        atualizarLblTempo();
        if (timerPergunta != null) timerPergunta.stop();
        timerPergunta = new Timer(1000, e -> {
            tempoRestante--;
            atualizarLblTempo();
            if (tempoRestante <= 0) {
                pararTimer();
                tempoEsgotado();
            }
        });
        timerPergunta.start();
    }

    private void pararTimer() {
        if (timerPergunta != null) timerPergunta.stop();
    }

    private void atualizarLblTempo() {
        if (tempoRestante <= 5) lblTempo.setForeground(VERMELHO);
        else if (tempoRestante <= 10) lblTempo.setForeground(AMARELO);
        else lblTempo.setForeground(AZUL);
        lblTempo.setText(tempoRestante + "s");
        lblTempo.repaint();
    }

    private void tempoEsgotado() {
        Pergunta p = perguntas.get(indice);
        try {
            quizService.registrarRespostaNula(partida.getIpartida(), p);
            quizService.atualizarKwanzasPartida(partida.getIpartida(), placar);
        } catch (SQLException e) { /* silencioso */ }

        for (JButton b : botoes) b.setEnabled(false);
        desfocarBotoes();
        int correta = "abcd".indexOf(p.getRespostaCorreta());
        if (correta >= 0) botoes[correta].setBackground(VERDE);
        mostrarFeedback("⏰  TEMPO ESGOTADO", VERMELHO);

        boolean ultima = (indice == perguntas.size() - 1);
        btnProxima.setText(ultima ? "VER RESULTADO  →" : "PRÓXIMA  →");
        btnProxima.setVisible(true);
    }

    // ─────────────────────────── AJUDAS ───────────────────────────────────

    /** AJUDA 1: Pula a pergunta atual sem penalidade */
    private void usarAjudaPular() {
        if (ajudaPularUsada) return;
        ajudaPularUsada = true;
        pararTimer();
        bloquearBotaoAjuda(btnAjudaPular, "USADA");

        // Registrar como resposta nula
        Pergunta p = perguntas.get(indice);
        try {
            quizService.registrarRespostaNula(partida.getIpartida(), p);
        } catch (SQLException ex) { /* silencioso */ }

        mostrarFeedback("⏭  PERGUNTA PULADA", new Color(99, 102, 241));

        boolean ultima = (indice == perguntas.size() - 1);
        for (JButton b : botoes) b.setEnabled(false);
        btnProxima.setText(ultima ? "VER RESULTADO  →" : "PRÓXIMA  →");
        btnProxima.setVisible(true);
    }

    /** AJUDA 2: Elimina 2 opções erradas, deixando só a correta + 1 errada */
    private void usarAjudaEliminar() {
        if (ajudaEliminarUsada) return;
        ajudaEliminarUsada = true;
        bloquearBotaoAjuda(btnAjudaEliminar, "USADA");

        Pergunta p = perguntas.get(indice);
        int corretaIdx = "abcd".indexOf(p.getRespostaCorreta());

        // Collect índices errados visíveis
        List<Integer> errados = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != corretaIdx && botoes[i].isVisible() && botoes[i].isEnabled()) {
                errados.add(i);
            }
        }

        // Escolhe 2 aleatórios para eliminar
        Random rnd = new Random();
        int qtdEliminar = Math.min(2, errados.size());
        for (int k = 0; k < qtdEliminar; k++) {
            int pos = rnd.nextInt(errados.size());
            int idxElim = errados.remove(pos);
            JButton bElim = botoes[idxElim];
            bElim.setEnabled(false);
            bElim.setBackground(new Color(30,30,45));
            bElim.setForeground(new Color(60,60,80));

            // Efeito "riscado" — adiciona X ao texto
            String textAtual = bElim.getText();
            bElim.setText("<html><s>" + textAtual.replace("<", "&lt;") + "</s></html>");
        }

        mostrarFeedback("✂  2 OPÇÕES ELIMINADAS", new Color(217, 70, 239));
    }

    /** AJUDA 3: Adiciona 15 segundos ao timer */
    private void usarAjudaTempo() {
        if (ajudaTempoUsada) return;
        ajudaTempoUsada = true;
        bloquearBotaoAjuda(btnAjudaTempo, "USADA");

        tempoRestante += BONUS_TEMPO;
        atualizarLblTempo();

        mostrarFeedback("⏱  +" + BONUS_TEMPO + " SEGUNDOS ADICIONADOS", new Color(16, 185, 129));

        // Limpa o feedback após 2s para não atrapalhar
        Timer t = new Timer(2000, e2 -> {
            if (btnProxima.isVisible()) return; // se já respondeu, não limpa
            lblFeedback.setText(" ");
        });
        t.setRepeats(false);
        t.start();
    }

    /** Bloqueia visualmente um botão de ajuda quando usada */
    private void bloquearBotaoAjuda(JButton btn, String labelUsada) {
        btn.setEnabled(false);
        btn.setBackground(new Color(35,35,50));
        // Redesenha os labels internos
        btn.removeAll();
        JLabel lEmoji = new JLabel("🔒", SwingConstants.CENTER);
        lEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(18)));
        lEmoji.setForeground(new Color(60,60,80));
        lEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lLabel = new JLabel(labelUsada, SwingConstants.CENTER);
        lLabel.setFont(new Font("Segoe UI", Font.BOLD, esc(9)));
        lLabel.setForeground(new Color(60,60,80));
        lLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
        btn.add(lEmoji);
        btn.add(lLabel);
        btn.revalidate();
        btn.repaint();
    }

    private void atualizarEstadoBotoesAjuda() {
        if (ajudaPularUsada)    btnAjudaPular.setEnabled(false);
        if (ajudaEliminarUsada) btnAjudaEliminar.setEnabled(false);
        if (ajudaTempoUsada)    btnAjudaTempo.setEnabled(false);
    }

    // ─────────────────────── UTILIDADES ───────────────────────────────────
    private void desfocarBotoes() {
        for (JButton b : botoes) {
            if (b.isEnabled()) b.setBackground(new Color(30,30,45));
        }
    }

    private void mostrarFeedback(String texto, Color cor) {
        lblFeedback.setForeground(cor);
        lblFeedback.setText(texto);
    }

    private void atualizarLblPlacar() {
        lblPlacar.setText("PLACAR: " + placar + " KZ");
    }
}