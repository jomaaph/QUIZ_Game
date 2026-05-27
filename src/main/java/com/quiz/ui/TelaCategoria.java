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

    public TelaCategoria(Jogador jogador) {
        this.jogador = jogador;
        setTitle("Quiz Game - Selecione Seu Módulo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Configura tamanho da tela 16:9 dinamicamente com base no monitor
        configurarTamanhoResponsivo();

        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
    }

    private void configurarTamanhoResponsivo() {
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        int larguraJanela = (int) (tamanhoTela.width * 0.75);
        int alturaJanela = (larguraJanela * 9) / 16;

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

    private void construirUI() {
        JPanel base = new JPanel(new BorderLayout(0, esc(24)));
        base.setBackground(Color.WHITE);
        base.setBorder(new EmptyBorder(esc(32), esc(50), esc(32), esc(50)));

        // --- CABEÇALHO ---
        JPanel cabecalho = new JPanel(new GridBagLayout());
        cabecalho.setOpaque(false);
        GridBagConstraints gbcCab = new GridBagConstraints();
        gbcCab.gridx = 0; gbcCab.gridy = 0; gbcCab.fill = GridBagConstraints.HORIZONTAL;

        // Tag do Usuário (topo)
        JLabel tagUsuario = new JLabel("USUÁRIO: " + jogador.getUsername().toUpperCase(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(224, 242, 254));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tagUsuario.setFont(new Font("Segoe UI", Font.BOLD, esc(12)));
        tagUsuario.setForeground(new Color(14, 165, 233));
        tagUsuario.setBorder(BorderFactory.createEmptyBorder(esc(6), esc(16), esc(6), esc(16)));

        JPanel containerTag = new JPanel(new FlowLayout(FlowLayout.CENTER));
        containerTag.setOpaque(false);
        containerTag.add(tagUsuario);
        cabecalho.add(containerTag, gbcCab);

        // Título Principal
        gbcCab.gridy++;
        gbcCab.insets = new Insets(esc(12), 0, 0, 0);
        JLabel titulo = new JLabel("Selecione Seu Módulo", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, esc(32)));
        titulo.setForeground(new Color(99, 102, 241));
        cabecalho.add(titulo, gbcCab);

        // Subtítulo
        gbcCab.gridy++;
        gbcCab.insets = new Insets(esc(4), 0, 0, 0);
        JLabel instrucao = new JLabel("Escolha uma área de conhecimento para iniciar o desafio", SwingConstants.CENTER);
        instrucao.setFont(new Font("Segoe UI", Font.PLAIN, esc(15)));
        instrucao.setForeground(new Color(148, 163, 184));
        cabecalho.add(instrucao, gbcCab);

        // --- GRID DE CATEGORIAS ---
        painelCategorias.setLayout(new GridLayout(1, 3, esc(24), 0));
        painelCategorias.setOpaque(false);
        carregarCategorias();

        // --- RODAPÉ CONTROLES ---
        JPanel rodape = new JPanel(new GridBagLayout());
        rodape.setOpaque(false);
        GridBagConstraints gbcRod = new GridBagConstraints();
        gbcRod.gridx = 0; gbcRod.gridy = 0; gbcRod.insets = new Insets(0, 0, esc(16), 0);

        // Seletor de Perguntas
        JPanel opcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, esc(8), 0));
        opcoes.setOpaque(false);
        JLabel lblQtd = new JLabel("Quantidade de Perguntas:");
        lblQtd.setFont(new Font("Segoe UI", Font.BOLD, esc(13)));
        lblQtd.setForeground(new Color(71, 85, 105));

        String[] qtds = {"5", "10", "15", "20"};
        JComboBox<String> comboQtd = new JComboBox<>(qtds);
        comboQtd.setSelectedItem("10");
        comboQtd.setFont(new Font("Segoe UI", Font.PLAIN, esc(13)));
        comboQtd.setBackground(Color.WHITE);
        opcoes.add(lblQtd);
        opcoes.add(comboQtd);
        rodape.add(opcoes, gbcRod);

        // Botão de Iniciar Desafio
        gbcRod.gridy++;
        gbcRod.insets = new Insets(0, 0, 0, 0);
        JButton btnJogar = new JButton("INICIAR DESAFIO") {
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
        btnJogar.setOpaque(false);
        btnJogar.setContentAreaFilled(false);
        btnJogar.setBackground(new Color(99, 102, 241));
        btnJogar.setForeground(Color.WHITE);
        btnJogar.setFont(new Font("Segoe UI", Font.BOLD, esc(15)));
        btnJogar.setFocusPainted(false);
        btnJogar.setBorder(BorderFactory.createEmptyBorder(esc(14), esc(48), esc(14), esc(48)));
        btnJogar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnJogar.addActionListener(e -> {
            if (cardSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um módulo antes de continuar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int categoriaId = cardSelecionado.getCategoriaId();
            Object item = comboQtd.getSelectedItem();
            int qtd = (item != null) ? Integer.parseInt(item.toString()) : 10;
            new TelaQuiz(jogador, categoriaId, qtd).setVisible(true);
            dispose();
        });
        rodape.add(btnJogar, gbcRod);

        base.add(cabecalho, BorderLayout.NORTH);
        base.add(painelCategorias, BorderLayout.CENTER);
        base.add(rodape, BorderLayout.SOUTH);
        add(base);
    }

    private void carregarCategorias() {
        try {
            List<Categoria> cats = quizService.listarCategorias();
            String[][] metadados = {
                    {"AVANÇADO", "↗", "Investimentos & Economia", "new Color(16, 185, 129)"},
                    {"DESAFIADOR", "⚛", "Raciocínio & Algoritmos", "new Color(217, 70, 239)"},
                    {"INTERMEDIÁRIO", "⚙", "Programação, IA & Hardware", "new Color(14, 165, 233)"}
            };

            for (int i = 0; i < cats.size(); i++) {
                Categoria c = cats.get(i);
                String[] meta = metadados[i % metadados.length];

                Color corTema = (i == 0) ? new Color(14, 165, 233) : (i == 1) ? new Color(16, 185, 129) : new Color(217, 70, 239);

                CardCategoria card = new CardCategoria(c.getCategoriaId(), c.getCategoria(), meta[0], meta[1], meta[2], corTema);

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent evt) {
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

    // --- CARD CORRIGIDO COM GRIDBAGLAYOUT INTERNO ---
    private class CardCategoria extends JPanel {
        private final int categoriaId;
        private boolean selecionado = false;
        private final Color corTema;

        public CardCategoria(int id, String titulo, String nivel, String icone, String subDesc, Color corTema) {
            this.categoriaId = id;
            this.corTema = corTema;

            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(esc(24), esc(16), esc(24), esc(16)));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;

            // 1. Bloco Quadrado do Ícone (Respeitando proporção)
            JPanel painelIcone = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(corTema);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(16), esc(16)));
                    g2.dispose();
                }
            };
            painelIcone.setOpaque(false);
            Dimension dimIcone = new Dimension(esc(64), esc(64));
            painelIcone.setPreferredSize(dimIcone);
            painelIcone.setMinimumSize(dimIcone);

            JLabel lblIcone = new JLabel(icone, SwingConstants.CENTER);
            lblIcone.setFont(new Font("Segoe UI", Font.BOLD, esc(24)));
            lblIcone.setForeground(Color.WHITE);
            painelIcone.add(lblIcone);

            gbc.insets = new Insets(0, 0, esc(16), 0);
            add(painelIcone, gbc);

            // 2. Título do Card
            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, esc(18)));
            lblTitulo.setForeground(new Color(30, 41, 59));
            gbc.insets = new Insets(0, 0, esc(4), 0);
            add(lblTitulo, gbc);

            // 3. Descrição sutil
            gbc.gridy++;
            JLabel lblDesc = new JLabel(subDesc, SwingConstants.CENTER);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
            lblDesc.setForeground(new Color(148, 163, 184));
            gbc.insets = new Insets(0, 0, esc(24), 0);
            add(lblDesc, gbc);

            // 4. Separador Fino
            gbc.gridy++;
            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(241, 245, 249));
            gbc.insets = new Insets(0, esc(8), esc(14), esc(8));
            add(sep, gbc);

            // 5. Linha de Rodapé do Card (Nível e contagem de questões fictícia)
            gbc.gridy++;
            JPanel containerLinha = new JPanel(new BorderLayout());
            containerLinha.setOpaque(false);

            JLabel lblNivel = new JLabel(nivel);
            lblNivel.setFont(new Font("Segoe UI", Font.BOLD, esc(11)));
            lblNivel.setForeground(corTema);

            JLabel lblQtdQuestoes = new JLabel("10 Questões");
            lblQtdQuestoes.setFont(new Font("Segoe UI", Font.PLAIN, esc(11)));
            lblQtdQuestoes.setForeground(new Color(203, 213, 225));

            containerLinha.add(lblNivel, BorderLayout.WEST);
            containerLinha.add(lblQtdQuestoes, BorderLayout.EAST);

            gbc.insets = new Insets(0, esc(8), 0, esc(8));
            add(containerLinha, gbc);
        }

        public int getCategoriaId() { return categoriaId; }

        public void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(20), esc(20)));

            if (selecionado) {
                g2.setColor(corTema);
                g2.setStroke(new BasicStroke(3f));
            } else {
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.5f));
            }
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
            g2.dispose();
        }
    }
}