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

    // Paleta (igual ao jogo)
    private final Color BG       = new Color(10, 10, 20);
    private final Color CARD     = new Color(22, 22, 38);
    private final Color BORDA    = new Color(50, 50, 75);
    private final Color AZUL     = new Color(99, 102, 241);
    private final Color AZUL2    = new Color(14, 165, 233);
    private final Color TEXTO    = Color.WHITE;
    private final Color SUB      = new Color(100, 116, 139);
    private final Color CAMPO_BG = new Color(30, 30, 50);

    public TelaLogin() {
        setTitle("Quiz Game — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        configurarTamanhoResponsivo();
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
    }

    private void configurarTamanhoResponsivo() {
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int larg = (int) (tela.width * 0.70);
        int alt  = (larg * 9) / 16;
        if (larg < 1024) { larg = 1024; alt = 576; }
        setSize(larg, alt);
        this.escala = (double) larg / 1344.0;
    }

    private int esc(int px) { return (int) Math.round(px * escala); }

    private void construirUI() {
        // Fundo principal com gradiente
        JPanel fundo = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, BG,
                        getWidth(), getHeight(), new Color(20, 15, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Pontos decorativos
                g2.setColor(new Color(99, 102, 241, 25));
                for (int i = 0; i < 12; i++) {
                    int x = (i * 120) % getWidth();
                    int y = (i * 87)  % getHeight();
                    g2.fillOval(x, y, esc(200), esc(200));
                }
                g2.dispose();
            }
        };

        // Card de login
        JPanel card = getJPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Ícone
        JLabel icone = new JLabel("🎮", SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esc(42)));
        gbc.insets = new Insets(0, 0, esc(8), 0);
        card.add(icone, gbc);

        // Título
        gbc.gridy++;
        JLabel titulo = new JLabel("QUIZ GAME", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, esc(28)));
        titulo.setForeground(AZUL);
        gbc.insets = new Insets(0, 0, esc(4), 0);
        card.add(titulo, gbc);

        // Sub
        gbc.gridy++;
        JLabel sub = new JLabel("Acesse sua conta para jogar", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, esc(13)));
        sub.setForeground(SUB);
        gbc.insets = new Insets(0, 0, esc(28), 0);
        card.add(sub, gbc);

        // Label usuário
        gbc.gridy++;
        JLabel lblUser = criarLabel("UTILIZADOR");
        gbc.insets = new Insets(0, 0, esc(6), 0);
        card.add(lblUser, gbc);

        // Campo usuário
        gbc.gridy++;
        campoUsername = new JTextField();
        estilizarCampo(campoUsername);
        gbc.insets = new Insets(0, 0, esc(14), 0);
        card.add(campoUsername, gbc);

        // Label senha
        gbc.gridy++;
        JLabel lblSenha = criarLabel("SENHA");
        gbc.insets = new Insets(0, 0, esc(6), 0);
        card.add(lblSenha, gbc);

        // Campo senha
        gbc.gridy++;
        campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);
        gbc.insets = new Insets(0, 0, esc(24), 0);
        card.add(campoSenha, gbc);

        // Botão entrar
        gbc.gridy++;
        JButton btnEntrar = criarBotao("ENTRAR", AZUL);
        gbc.insets = new Insets(0, 0, esc(10), 0);
        card.add(btnEntrar, gbc);

        // Botão criar conta
        gbc.gridy++;
        JButton btnCriar = criarBotao("CRIAR CONTA", new Color(30,30,55));
        btnCriar.setForeground(AZUL2);
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnCriar, gbc);

        // Eventos
        btnEntrar.addActionListener(e -> autenticar());
        btnCriar.addActionListener(e -> criarConta());
        campoSenha.addActionListener(e -> autenticar());

        fundo.add(card);
        add(fundo);
    }

    private JPanel getJPanel() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(20),esc(20)));
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(20),esc(20)));
                // linha decorativa topo
                GradientPaint gp = new GradientPaint(esc(40),0,AZUL,getWidth()-esc(40),0,AZUL2);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(esc(40), 0, getWidth()-esc(40), 0);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(esc(38), esc(44), esc(38), esc(44)));

        Dimension dimCard = new Dimension(esc(420), esc(520));
        card.setPreferredSize(dimCard);
        return card;
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, esc(11)));
        l.setForeground(new Color(14, 165, 233));
        return l;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(CAMPO_BG);
        campo.setForeground(TEXTO);
        campo.setCaretColor(AZUL);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, esc(14)));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1, true),
                BorderFactory.createEmptyBorder(esc(10), esc(14), esc(10), esc(14))
        ));
        campo.setPreferredSize(new Dimension(esc(200), esc(44)));
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),esc(10),esc(10)));
                if (isEnabled()) {
                    g2.setColor(getBackground().brighter());
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(1,1,getWidth()-2,getHeight()-2,esc(10),esc(10)));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBackground(cor);
        btn.setForeground(TEXTO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, esc(14)));
        btn.setBorder(BorderFactory.createEmptyBorder(esc(12), 0, esc(12), 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void autenticar() {
        String user = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        if (user.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha todos os campos.");
            return;
        }
        try {
            Jogador j = jogadorService.autenticar(user, senha);
            new TelaCategoria(j).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarErro("Utilizador ou senha incorretos.");
        } catch (SQLException ex) {
            mostrarErro("Erro no banco: " + ex.getMessage());
        }
    }

    private void criarConta() {
        String user = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        if (user.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha os campos para cadastrar.");
            return;
        }
        try {
            Jogador j = jogadorService.cadastrar(user, senha);
            JOptionPane.showMessageDialog(this, "✓  Conta criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            new TelaCategoria(j).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarErro("Este username já está em uso.");
        } catch (SQLException ex) {
            mostrarErro("Erro no banco: " + ex.getMessage());
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}