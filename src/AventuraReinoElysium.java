import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

class Personagem {
    String nome;
    String arma;
    int hp;
    int ataque;
    int turnosParaCurar;
    int turnosParaDefender;

    public Personagem(String nome, String arma, int hp, int ataque) {
        this.nome = nome;
        this.arma = arma;
        this.hp = hp;
        this.ataque = ataque;
        this.turnosParaCurar = 0;
        this.turnosParaDefender = 0;
    }
}

class Monstro {
    String tipo;
    int hp;
    int ataque;

    public Monstro(String tipo, int hp, int ataque) {
        this.tipo = tipo;
        this.hp = hp;
        this.ataque = ataque;
    }
}

public class AventuraReinoElysium extends JFrame {
    private static final Random random = new Random();
    private static final List<String> armas = Arrays.asList("espada", "arco", "mace");
    private static final List<String> acoes = Arrays.asList("Atacar", "Defender", "Fugir", "Curar");

    private Personagem jogador;
    private Monstro monstro;
    private final JTextArea display;
    private final JButton[] botoesAcoes;
    private final JButton botaoContinuar;

    public AventuraReinoElysium() {
        setTitle("Aventura no Reino de Elysium");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextArea();
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(1, acoes.size() + 1));
        botoesAcoes = new JButton[acoes.size()];

        for (int i = 0; i < acoes.size(); i++) {
            botoesAcoes[i] = new JButton(acoes.get(i));
            botoesAcoes[i].addActionListener(new AcaoListener());
            painelBotoes.add(botoesAcoes[i]);
        }

        botaoContinuar = new JButton("Continuar");
        botaoContinuar.addActionListener(e -> explorar());
        painelBotoes.add(botaoContinuar);

        add(painelBotoes, BorderLayout.SOUTH);
        iniciarJogo();
    }

    private void iniciarJogo() {
        String nomeJogador = JOptionPane.showInputDialog("Digite seu nome:");

        String armaEscolhida = (String) JOptionPane.showInputDialog(
                this, "Escolha uma arma:", "Arma",
                JOptionPane.QUESTION_MESSAGE, null,
                armas.toArray(), armas.get(0)
        );

        jogador = new Personagem(nomeJogador, armaEscolhida, 100, calcularDano(armaEscolhida));
        display.append("Bem-vindo, " + jogador.nome + "! Sua aventura começa agora!\n");
        explorar();
    }

    private void explorar() {
        display.append("Você está explorando a floresta...\n");
        if (random.nextInt(100) < 50) {
            monstro = gerarMonstro();
            display.append("Você encontrou um " + monstro.tipo + "! Ele tem " + monstro.hp + " HP!\n");
            atualizarBotoes(true);
        } else {
            display.append("Nada aconteceu. Você continua sua jornada.\n");
            atualizarBotoes(false);
        }
        jogador.turnosParaCurar = Math.max(0, jogador.turnosParaCurar - 1);
        jogador.turnosParaDefender = Math.max(0, jogador.turnosParaDefender - 1);
    }

    private void batalha(String acao) {
        switch (acao) {
            case "Fugir":
                if (random.nextInt(100) < 70) {
                    display.append("Você fugiu! O monstro continua à solta...\n");
                    atualizarBotoes(false);
                } else {
                    display.append("A fuga falhou! O monstro ainda está aqui...\n");
                    int danoMonstro = calcularDanoMonstro(monstro.tipo); // Recalcula o dano do monstro
                    display.append("O monstro atacou e causou " + danoMonstro + " de dano!\n");
                    jogador.hp -= danoMonstro;
                }
                return;
            case "Defender":
                if (jogador.turnosParaDefender > 0) {
                    display.append("Você ainda está em cooldown para Defender! " + jogador.turnosParaDefender + " turnos restantes.\n");
                } else {
                    display.append("Você se defendeu! Reduziu o dano do monstro pela metade.\n");
                    int danoMonstro = calcularDanoMonstro(monstro.tipo) / 2; // Reduz dano do monstro pela metade
                    jogador.hp -= danoMonstro;
                    jogador.turnosParaDefender = 1; // cooldown de 1 turno para Defender
                }
                break;
            case "Curar":
                if (jogador.turnosParaCurar > 0) {
                    display.append("Você ainda está em cooldown para Curar! " + jogador.turnosParaCurar + " turnos restantes.\n");
                } else {
                    int cura = random.nextInt(21) + 10;
                    jogador.hp += cura;
                    display.append("Você se curou em " + cura + " pontos de vida!\n");
                    jogador.turnosParaCurar = 2; // cooldown de 2 turnos para Curar
                }
                break;
            default:
                int danoJogador = calcularDano(jogador.arma); // Recalcula o dano do jogador
                int danoMonstro = calcularDanoMonstro(monstro.tipo); // Recalcula o dano do monstro
                display.append("Você atacou com " + jogador.arma + " e causou " + danoJogador + " de dano!\n");
                monstro.hp -= danoJogador;
                if (monstro.hp <= 0) {
                    display.append("Você derrotou o monstro!\n");
                    atualizarBotoes(false);
                    return;
                }
                display.append("O monstro contra-atacou e causou " + danoMonstro + " de dano!\n");
                jogador.hp -= danoMonstro;
        }
        display.append("Seu HP: " + jogador.hp + " | HP do monstro: " + monstro.hp + "\n");

        if (jogador.hp <= 0) {
            display.append("Você foi derrotado! Fim da jornada...\n");
            atualizarBotoes(false);
        }
    }

    private void atacarMonstro() {
        int danoMonstro = calcularDanoMonstro(monstro.tipo); // Recalcula o dano do monstro
        display.append("O monstro atacou e causou " + danoMonstro + " de dano!\n");
        jogador.hp -= danoMonstro;
    }

    private static int calcularDano(String arma) {
        return switch (arma) {
            case "espada" -> random.nextInt(30) + 1;
            case "arco" -> random.nextInt(20) + 1;
            case "mace" -> random.nextInt(40) + 1;
            default -> 0;
        };
    }

    private static int calcularDanoMonstro(String tipo) {
        return switch (tipo) {
            case "Goblin" -> random.nextInt(5) + 5;
            case "Lobo" -> random.nextInt(10) + 10;
            case "Orc" -> random.nextInt(15) + 15;
            case "Dragão" -> random.nextInt(20) + 20;
            default -> 0;
        };
    }

    private Monstro gerarMonstro() {
        List<String> tipos = Arrays.asList("Goblin", "Lobo", "Orc", "Dragão");
        String tipo = tipos.get(random.nextInt(tipos.size()));
        int hp = random.nextInt(51) + 50;
        int ataque = calcularDanoMonstro(tipo);
        return new Monstro(tipo, hp, ataque);
    }

    private void atualizarBotoes(boolean emBatalha) {
        for (JButton botao : botoesAcoes) {
            botao.setEnabled(emBatalha);
        }
        botaoContinuar.setEnabled(!emBatalha);
    }

    private class AcaoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            batalha(e.getActionCommand());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AventuraReinoElysium().setVisible(true));
    }
}
