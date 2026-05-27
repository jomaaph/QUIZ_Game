package com.quiz.ui;

import com.quiz.model.Jogador;
import com.quiz.service.JogadorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class TelaLogin extends JFrame {

    private final JogadorService jogadorService = new JogadorService();
    private JTextField campoUsername;
    private JPasswordField campoSenha;
    private double escala = 1.0;

    public TelaLogin() {
        setTitle("Quiz Game - Autenticação Responsiva");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Configura as proporções da janela 16:9 dinamicamente
        configurarTamanhoResponsivo();

        setLocationRelativeTo(null);
        construirUI();
    }

    private void configurarTamanhoResponsivo() {
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        int larguraJanela = (int) (tamanhoTela.width * 0.70);
        int alturaJanela = (larguraJanela * 9) / 16;

        if (larguraJanela < 1024) {
            larguraJanela = 1024;
            alturaJanela = 576;
        }

        setSize(larguraJanela, alturaJanela);
        // Base de cálculo para escala (ajusta fontes e paddings)
        this.escala = (double) larguraJanela / 1344.0;
    }

    private int esc(int pixels) {
        return (int) Math.round(pixels * escala);
    }

    private void construirUI() {
        // Painel base que centraliza o card na tela
        JPanel fundoJanela = new JPanel(new GridBagLayout());
        fundoJanela.setBackground(Color.WHITE);

        // --- DESIGN DO CARD COM CANTOS ARREDONDADOS ---
        JPanel cardLogin = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundo Branco do Card
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(24), esc(24)));

                // Borda Azul sutil
                g2.setColor(new Color(224, 242, 254));
                g2.setStroke(new BasicStroke(Math.max(1.5f, (float)(1.5 * escala))));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, esc(24), esc(24)));
                g2.dispose();
            }
        };
        cardLogin.setOpaque(false);
        cardLogin.setBorder(new EmptyBorder(esc(35), esc(40), esc(35), esc(40)));

        // Força dimensões fixas proporcionais para o Card interno
        Dimension dimCard = new Dimension(esc(400), esc(530));
        cardLogin.setPreferredSize(dimCard);
        cardLogin.setMinimumSize(dimCard);
        cardLogin.setMaximumSize(dimCard);

        // --- CONFIGURAÇÃO DO GRID INTERNO DO CARD ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Ícone de Usuário
        JLabel lblIcone = new JLabel("👤", SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI", Font.PLAIN, esc(36)));
        lblIcone.setForeground(new Color(14, 165, 233));
        gbc.insets = new Insets(0, 0, esc(10), 0);
        cardLogin.add(lblIcone, gbc);

        // 2. Título Principal
        gbc.gridy++;
        JLabel titulo = new JLabel("Autenticação", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, esc(26)));
        titulo.setForeground(new Color(99, 102, 241));
        gbc.insets = new Insets(0, 0, esc(4), 0);
        cardLogin.add(titulo, gbc);

        // 3. Subtítulo
        gbc.gridy++;
        JLabel sub = new JLabel("Acesse sua conta neural", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, esc(14)));
        sub.setForeground(new Color(148, 163, 184));
        gbc.insets = new Insets(0, 0, esc(24), 0);
        cardLogin.add(sub, gbc);

        // 4. Label: Identificador
        gbc.gridy++;
        JLabel lblUser = criarLabelInput("IDENTIFICADOR");
        gbc.insets = new Insets(0, 0, esc(6), 0);
        cardLogin.add(lblUser, gbc);

        // 5. Input: Username
        gbc.gridy++;
        campoUsername = criarCampoTexto(false);
        gbc.insets = new Insets(0, 0, esc(16), 0);
        cardLogin.add(campoUsername, gbc);

        // 6. Label: Código de Acesso
        gbc.gridy++;
        JLabel lblSenha = criarLabelInput("CÓDIGO DE ACESSO");
        gbc.insets = new Insets(0, 0, esc(6), 0);
        cardLogin.add(lblSenha, gbc);

        // 7. Input: Senha
        gbc.gridy++;
        campoSenha = (JPasswordField) criarCampoTexto(true);
        gbc.insets = new Insets(0, 0, esc(28), 0);
        cardLogin.add(campoSenha, gbc);

        // 8. Botão Conectar com Gradiente
        gbc.gridy++;
        JButton btnConectar = new JButton("Conectar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradiente = new GradientPaint(
                        0, 0, new Color(6, 182, 212),
                        getWidth(), 0, new Color(59, 130, 246)
                );
                g2.setPaint(gradiente);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), esc(12), esc(12)));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConectar.setOpaque(false);
        btnConectar.setContentAreaFilled(false);
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFont(new Font("Segoe UI", Font.BOLD, esc(15)));
        btnConectar.setFocusPainted(false);
        btnConectar.setBorder(BorderFactory.createEmptyBorder(esc(12), 0, esc(12), 0));
        btnConectar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(0, 0, esc(16), 0);
        cardLogin.add(btnConectar, gbc);

        // 9. Botão Link: Criar Conta
        gbc.gridy++;
        JButton btnCriarConta = new JButton("Criar nova conta neural →");
        btnCriarConta.setFont(new Font("Segoe UI", Font.BOLD, esc(13)));
        btnCriarConta.setForeground(new Color(6, 182, 212));
        btnCriarConta.setBorderPainted(false);
        btnCriarConta.setContentAreaFilled(false);
        btnCriarConta.setFocusPainted(false);
        btnCriarConta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(0, 0, 0, 0);
        cardLogin.add(btnCriarConta, gbc);

        // Eventos
        btnConectar.addActionListener(e -> autenticar());
        campoSenha.addActionListener(e -> autenticar());
        btnCriarConta.addActionListener(e -> criarConta());

        fundoJanela.add(cardLogin);
        add(fundoJanela);
    }

    private JLabel criarLabelInput(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, esc(11)));
        label.setForeground(new Color(14, 165, 233));
        return label;
    }

    private JTextField criarCampoTexto(boolean ehSenha) {
        JTextField campo = ehSenha ? new JPasswordField() : new JTextField();
        campo.setBackground(new Color(241, 245, 249));
        campo.setForeground(new Color(51, 65, 85));
        campo.setCaretColor(new Color(51, 65, 85));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, esc(14)));

        // CORREÇÃO DA BORDA: Mantém a borda arredondada E o padding interno correto
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(esc(10), esc(14), esc(10), esc(14))
        ));

        campo.setPreferredSize(new Dimension(esc(200), esc(42)));
        return campo;
    }

    // --- MÉTODOS DE AUTENTICAÇÃO MANTIDOS ---
    private void autenticar() {
        String username = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        if (username.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Jogador jogador = jogadorService.autenticar(username, senha);
            new TelaCategoria(jogador).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarConta() {
        String username = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        if (username.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha os campos para cadastrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Jogador jogador = jogadorService.cadastrar(username, senha);
            JOptionPane.showMessageDialog(this, "Conta criada com sucesso!");
            new TelaCategoria(jogador).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Este username já está em uso.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}