package com.quiz.ui;

import com.quiz.model.Categoria;
import com.quiz.model.Jogador;
import com.quiz.service.QuizService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

public class TelaCategoria extends JFrame {

    private final Jogador jogador;
    private final QuizService quizService = new QuizService();
    private final JPanel painelCategorias = new JPanel();
    private CardCategoria cardSelecionado = null;
    private double escala = 1.0;

    // Paleta Neo-Dark
    private final Color BG    = new Color(10, 10, 20);
    private final Color CARD  = new Color(22, 22, 38);
    private final Color BORDA = new Color(50, 50, 75);
    private final Color AZUL  = new Color(99, 102, 241);
    private final Color TEXTO = Color.WHITE;
    private final Color SUB   = new Color(100, 116, 139);

    public TelaCategoria(Jogador jogador) {
        this.jogador = jogador;
        setTitle("Quiz Game — Selecione a Categoria");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        configurarTamanhoResponsivo();
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
    }

    private void configurarTamanhoResponsivo() {
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int larg = (int) (tela.width * 0.78);
        int alt  = (larg * 9) / 16;
        if (larg < 1024) { larg = 1024; alt = 576; }
        setSize(larg, alt);
        this.escala = (double) larg / 1344.0;
    }

    private int esc(int px) { return (int) Math.round(px * escala); }

    private void construirUI() {
        JPanel fundo = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, BG, getWidth(), getHeight(), new Color(18, 12, 35));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                // Círculos decorativos
                g2.setColor(new Color(99,102,241,15));
                g2.fillOval(-esc(100), -esc(100), esc(400), esc(400));
                g2.fillOval(getWidth()-esc(200), getHeight()-esc(200), esc(400), esc(400));
                g2.dispose();
            }
        };
        fundo.setBorder(new EmptyBorder(esc(32), esc(50), esc(32), esc(50)));

        // ── CABEÇALHO ──
        JPanel cab = new JPanel();
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        cab.setOpaque(false);

        // Tag do usuário
        JLabel tagUser = new JLabel("👤  " + jogador.getUsername().toUpperCase(), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99,102,241,35));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,20,20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tagUser.setFont(new Font("Segoe UI", Font.BOLD, esc(12)));
        tagUser.setForeground(AZUL);
        tagUser.setBorder(BorderFactory.createEmptyBorder(esc(6), esc(20), esc(6), esc(20)));
        tagUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tagWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tagWrap.setOpaque(false);
        tagWrap.add(tagUser);

        JLabel titulo = new JLabel("Escolha o Módulo", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, esc(32)));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instr = new JLabel("Selecione uma categoria de conhecimento para iniciar", SwingConstants.CENTER);
        instr.setFont(new Font("Segoe UI", Font.PLAIN, esc(14)));
        instr.setForeground(SUB);
        instr.setAlignmentX(Component.CENTER_ALIGNMENT);

        cab.add(tagWrap);
        cab.add(Box.createVerticalStrut(esc(10)));
        cab.add(titulo);
        cab.add(Box.createVerticalStrut(esc(4)));
        cab.add(instr);

        // ── GRID DE CATEGORIAS ──
        painelCategorias.setLayout(new GridLayout(1, 3, esc(22), 0));
        painelCategorias.setOpaque(false);
        carregarCategorias();

        // ── RODAPÉ ──
        JPanel rodape = new JPanel(new GridBagLayout());
        rodape.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.insets = new Insets(0, 0, esc(12), 0);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.CENTER, esc(12), 0));
        linha.setOpaque(false);

        JLabel lblQtd = new JLabel("Nº de Perguntas:");
        lblQtd.setFont(new Font("Segoe UI", Font.BOLD, esc(13)));
        lblQtd.setForeground(SUB);

        JComboBox<String> comboQtd = new JComboBox<>(new String[]{"5","10","15","20"});
        comboQtd.setSelectedItem("10");
        comboQtd.setFont(new Font("Segoe UI", Font.PLAIN, esc(13)));
        comboQtd.setBackground(CARD);
        comboQtd.setForeground(TEXTO);
        comboQtd.setPreferredSize(new Dimension(esc(80), esc(34)));

        linha.add(lblQtd);
        linha.add(comboQtd);
        rodape.add(linha, g);

        g.gridy++;
        g.insets = new Insets(0, 0, 0, 0);

        JButton btnJogar = new JButton("INICIAR DESAFIO  ⚡") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, AZUL, getWidth(), 0, new Color(139, 92, 246));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(12),esc(12)));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnJogar.setOpaque(false);
        btnJogar.setContentAreaFilled(false);
        btnJogar.setForeground(TEXTO);
        btnJogar.setFont(new Font("Segoe UI", Font.BOLD, esc(15)));
        btnJogar.setFocusPainted(false);
        btnJogar.setBorder(BorderFactory.createEmptyBorder(esc(14), esc(52), esc(14), esc(52)));
        btnJogar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnJogar.addActionListener(e -> {
            if (cardSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria antes de continuar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int catId = cardSelecionado.getCategoriaId();
            int qtd = Integer.parseInt((String) comboQtd.getSelectedItem());
            new TelaQuiz(jogador, catId, qtd).setVisible(true);
            dispose();
        });
        rodape.add(btnJogar, g);

        fundo.add(cab, BorderLayout.NORTH);
        fundo.add(painelCategorias, BorderLayout.CENTER);
        fundo.add(rodape, BorderLayout.SOUTH);
        add(fundo);
    }

    private void carregarCategorias() {
        try {
            List<Categoria> cats = quizService.listarCategorias();
            String[] emojis = {"💻", "💰", "🧠"};
            String[] niveis = {"TECNOLOGIA", "FINANÇAS", "LÓGICA"};
            String[] descs  = {"Programação, IA & Hardware", "Investimentos & Economia", "Raciocínio & Algoritmos"};
            Color[] cores   = {
                    new Color(14, 165, 233),
                    new Color(16, 185, 129),
                    new Color(217, 70, 239)
            };

            for (int i = 0; i < cats.size(); i++) {
                Categoria c = cats.get(i);
                Color cor = cores[i % cores.length];
                String emoji = emojis[i % emojis.length];
                String nivel = niveis[i % niveis.length];
                String desc  = descs[i % descs.length];

                CardCategoria card = new CardCategoria(c.getCategoriaId(), c.getCategoria(), nivel, emoji, desc, cor);
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mousePressed(java.awt.event.MouseEvent evt) {
                        if (cardSelecionado != null) cardSelecionado.setSelecionado(false);
                        cardSelecionado = card;
                        cardSelecionado.setSelecionado(true);
                    }
                });
                painelCategorias.add(card);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage());
        }
    }

    // ── CARD INTERNO ──
    private class CardCategoria extends JPanel {
        private final int categoriaId;
        private boolean selecionado = false;
        private final Color corTema;

        public CardCategoria(int id, String titulo, String nivel, String emoji, String desc, Color cor) {
            this.categoriaId = id;
            this.corTema = cor;
            setLayout(new GridBagLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(esc(24), esc(18), esc(24), esc(18)));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            // Ícone
            JPanel painelIcone = new JPanel(new GridBagLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                            0,0, cor.darker(),
                            getWidth(), getHeight(), cor);
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(14),esc(14)));
                    g2.dispose();
                }
            };
            painelIcone.setOpaque(false);
            Dimension dimI = new Dimension(esc(68), esc(68));
            painelIcone.setPreferredSize(dimI);

            JLabel lblEmoji = new JLabel(emoji, SwingConstants.CENTER);
            lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(28)));
            painelIcone.add(lblEmoji);

            gbc.insets = new Insets(0,0,esc(14),0);
            add(painelIcone, gbc);

            // Título
            gbc.gridy++;
            JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
            lblTit.setFont(new Font("Segoe UI", Font.BOLD, esc(17)));
            lblTit.setForeground(TEXTO);
            gbc.insets = new Insets(0,0,esc(4),0);
            add(lblTit, gbc);

            // Descrição
            gbc.gridy++;
            JLabel lblDesc = new JLabel(desc, SwingConstants.CENTER);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
            lblDesc.setForeground(SUB);
            gbc.insets = new Insets(0,0,esc(20),0);
            add(lblDesc, gbc);

            // Separador
            gbc.gridy++;
            JSeparator sep = new JSeparator();
            sep.setForeground(BORDA);
            gbc.insets = new Insets(0,esc(6),esc(12),esc(6));
            add(sep, gbc);

            // Rodapé do card
            gbc.gridy++;
            JPanel linha = new JPanel(new BorderLayout());
            linha.setOpaque(false);
            JLabel lblNivel = new JLabel(nivel);
            lblNivel.setFont(new Font("Segoe UI", Font.BOLD, esc(11)));
            lblNivel.setForeground(cor);
            JLabel lblQtd2 = new JLabel("50 questões");
            lblQtd2.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
            lblQtd2.setForeground(SUB);
            linha.add(lblNivel, BorderLayout.WEST);
            linha.add(lblQtd2, BorderLayout.EAST);
            gbc.insets = new Insets(0,esc(6),0,esc(6));
            add(linha, gbc);
        }

        public int getCategoriaId() { return categoriaId; }

        public void setSelecionado(boolean sel) {
            this.selecionado = sel;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(18),esc(18)));
            if (selecionado) {
                // Brilho colorido ao selecionar
                g2.setColor(corTema);
                g2.setStroke(new BasicStroke(2.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(18),esc(18)));
                // Glow interno
                g2.setColor(new Color(corTema.getRed(), corTema.getGreen(), corTema.getBlue(), 25));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(18),esc(18)));
            } else {
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(18),esc(18)));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}