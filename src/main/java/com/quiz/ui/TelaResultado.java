package com.quiz.ui;

import com.quiz.model.Jogador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TelaResultado extends JFrame {

    private final Color BG      = new Color(10, 10, 20);
    private final Color CARD    = new Color(22, 22, 38);
    private final Color CARD2   = new Color(30, 30, 50);
    private final Color BORDA   = new Color(50, 50, 75);
    private final Color AZUL    = new Color(99, 102, 241);
    private final Color VERDE   = new Color(34, 197, 94);
    private final Color VERMELHO= new Color(239, 68, 68);
    private final Color AMARELO = new Color(251, 191, 36);
    private final Color LARANJA = new Color(251, 146, 60);
    private final Color TEXTO   = Color.WHITE;
    private final Color SUB     = new Color(100, 116, 139);

    private double escala = 1.0;

    public TelaResultado(Jogador jogador, int acertos, int total, int pontuacao) {
        setTitle("Quiz Game — Resultado");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        configurarTamanhoResponsivo();
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI(jogador, acertos, total, pontuacao);
    }

    private void configurarTamanhoResponsivo() {
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int larg = (int) (tela.width * 0.72);
        int alt  = (larg * 9) / 16;
        if (larg < 1024) { larg = 1024; alt = 576; }
        setSize(larg, alt);
        this.escala = (double) larg / 1344.0;
    }

    private int esc(int px) { return (int) Math.round(px * escala); }

    private void construirUI(Jogador jogador, int acertos, int total, int pontuacao) {
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0,0, BG, getWidth(), getHeight(), new Color(18,12,35));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };

        // Card central
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(22),esc(22)));
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(22),esc(22)));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(esc(36), esc(56), esc(36), esc(56)));
        card.setPreferredSize(new Dimension(esc(700), esc(530)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        double pct = (double) acertos / total;
        String emoji, mensagem;
        Color corMensagem;

        if (pct >= 0.8) {
            emoji = "🏆"; mensagem = "Excelente! Domínio Total!"; corMensagem = AMARELO;
        } else if (pct >= 0.6) {
            emoji = "⚡"; mensagem = "Muito bom! Continue assim!"; corMensagem = VERDE;
        } else if (pct >= 0.4) {
            emoji = "🎯"; mensagem = "Boa tentativa! Pode melhorar!"; corMensagem = LARANJA;
        } else {
            emoji = "💪"; mensagem = "Não desistas! Tenta de novo!"; corMensagem = VERMELHO;
        }

        // Emoji
        JLabel icone = new JLabel(emoji, SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(52)));
        gbc.gridy = 0; gbc.insets = new Insets(0,0,esc(8),0);
        card.add(icone, gbc);

        // Mensagem dinâmica
        JLabel lblMsg = new JLabel(mensagem, SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, esc(24)));
        lblMsg.setForeground(corMensagem);
        gbc.gridy = 1; gbc.insets = new Insets(0,0,esc(4),0);
        card.add(lblMsg, gbc);

        // Jogador
        JLabel lblJog = new JLabel("Jogador: " + jogador.getUsername().toUpperCase(), SwingConstants.CENTER);
        lblJog.setFont(new Font("Segoe UI", Font.PLAIN, esc(13)));
        lblJog.setForeground(SUB);
        gbc.gridy = 2; gbc.insets = new Insets(0,0,esc(20),0);
        card.add(lblJog, gbc);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDA);
        gbc.gridy = 3; gbc.insets = new Insets(0,esc(20),esc(20),esc(20));
        card.add(sep, gbc);

        // Bloco de estatísticas (3 cards lado a lado)
        gbc.gridy = 4; gbc.insets = new Insets(0,0,esc(20),0);
        JPanel stats = new JPanel(new GridLayout(1, 3, esc(14), 0));
        stats.setOpaque(false);
        stats.add(criarStatCard("✓ Acertos", acertos + " / " + total, VERDE));
        stats.add(criarStatCard("% Aproveitamento", (int)(pct*100) + "%", AZUL));
        stats.add(criarStatCard("💰 Kwanzas", pontuacao + " KZ", AMARELO));
        card.add(stats, gbc);

        // Barra de progresso visual
        gbc.gridy = 5; gbc.insets = new Insets(0,0,esc(28),0);
        JPanel barraWrap = new JPanel(new BorderLayout(0, esc(4)));
        barraWrap.setOpaque(false);
        JLabel lblBarra = new JLabel("Aproveitamento Visual", SwingConstants.CENTER);
        lblBarra.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
        lblBarra.setForeground(SUB);
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue((int)(pct * 100));
        barra.setStringPainted(false);
        barra.setBackground(new Color(35,35,55));
        barra.setForeground(pct >= 0.6 ? VERDE : pct >= 0.4 ? LARANJA : VERMELHO);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(0, esc(10)));
        barraWrap.add(lblBarra, BorderLayout.NORTH);
        barraWrap.add(barra, BorderLayout.CENTER);
        card.add(barraWrap, gbc);

        // Botões
        gbc.gridy = 6; gbc.insets = new Insets(0,0,0,0);
        JPanel btns = new JPanel(new GridLayout(1, 2, esc(14), 0));
        btns.setOpaque(false);

        JButton btnNova = criarBotao("🔄  NOVA PARTIDA", AZUL, TEXTO);
        btnNova.addActionListener(e -> {
            new TelaCategoria(jogador).setVisible(true);
            dispose();
        });

        JButton btnSair = criarBotao("✕  SAIR", new Color(35,20,20), VERMELHO);
        btnSair.addActionListener(e -> System.exit(0));

        btns.add(btnNova);
        btns.add(btnSair);
        card.add(btns, gbc);

        root.add(card);
        add(root);
    }

    private JPanel criarStatCard(String label, String valor, Color cor) {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD2);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(12),esc(12)));
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(12),esc(12)));
                // Linha colorida no topo
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(esc(16), 0, getWidth()-esc(16), 0);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(esc(14), esc(12), esc(14), esc(12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, esc(22)));
        lblValor.setForeground(cor);
        p.add(lblValor, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(esc(2),0,0,0);
        JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
        lblLabel.setForeground(SUB);
        p.add(lblLabel, gbc);

        return p;
    }

    private JButton criarBotao(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(10),esc(10)));
                g2.setColor(getForeground().darker());
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(10),esc(10)));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, esc(13)));
        btn.setBorder(BorderFactory.createEmptyBorder(esc(13), esc(24), esc(13), esc(24)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}