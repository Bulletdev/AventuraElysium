import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class AventuraReinoElysium extends Application {
    private static final Random random = new Random();
    private Personagem jogador;
    private Monstro monstro;
    private Label statusLabel;
    private Label hpLabel;
    private Label logLabel;
    private Button explorarBtn;
    private Button atacarBtn;
    private Button defenderBtn;
    private Button fugirBtn;
    private Button curarBtn;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aventura no Reino de Elysium");

        VBox root = new VBox();
        statusLabel = new Label("Bem-vindo ao Reino de Elysium!");
        hpLabel = new Label();
        logLabel = new Label();

        explorarBtn = new Button("Explorar");
        explorarBtn.setOnAction(event -> explorar());

        atacarBtn = new Button("Atacar");
        atacarBtn.setOnAction(event -> atacarMonstro());
        atacarBtn.setVisible(false);

        defenderBtn = new Button("Defender");
        defenderBtn.setOnAction(event -> defender());
        defenderBtn.setVisible(false);

        fugirBtn = new Button("Fugir");
        fugirBtn.setOnAction(event -> fugir());
        fugirBtn.setVisible(false);

        curarBtn = new Button("Curar");
        curarBtn.setOnAction(event -> curar());
        curarBtn.setVisible(false);

        root.getChildren().addAll(statusLabel, hpLabel, logLabel, explorarBtn, atacarBtn, defenderBtn, fugirBtn, curarBtn);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        iniciarJogo();
    }

    private void iniciarJogo() {
        jogador = new Personagem("Aventureiro", "espada", 100, 10);
        statusLabel.setText("Bem-vindo, " + jogador.nome + "! Sua aventura começa agora!");
        atualizarHpLabel();
    }

    private void explorar() {
        statusLabel.setText("Você está explorando a floresta...");
        if (random.nextInt(100) < 50) {
            monstro = gerarMonstro();
            statusLabel.setText("Você encontrou um " + monstro.tipo + "! Ele tem " + monstro.hp + " HP!");
            alternarBotoes(true);
        } else {
            statusLabel.setText("Nada aconteceu. Você continua sua jornada.");
        }
        jogador.turnosParaCurar = Math.max(0, jogador.turnosParaCurar - 1);
        jogador.turnosParaDefender = Math.max(0, jogador.turnosParaDefender - 1);
        atualizarHpLabel();
    }

    private void atacarMonstro() {
        if (monstro == null) {
            logLabel.setText("Não há monstros para atacar.");
            return;
        }

        int danoJogador = calcularDano(jogador.arma);
        monstro.hp -= danoJogador;
        logLabel.setText("Você atacou com " + jogador.arma + " e causou " + danoJogador + " de dano!");

        if (monstro.hp <= 0) {
            logLabel.setText(logLabel.getText() + "\nVocê derrotou o monstro!");
            ganharExperiencia();
            monstro = null;
            alternarBotoes(false);
        } else {
            int danoMonstro = calcularDanoMonstro(monstro.tipo);
            jogador.hp -= danoMonstro;
            logLabel.setText(logLabel.getText() + "\nO monstro contra-atacou e causou " + danoMonstro + " de dano!");

            if (jogador.hp <= 0) {
                logLabel.setText(logLabel.getText() + "\nVocê foi derrotado! Fim da jornada...");
                mostrarTelaDerrota();
                monstro = null;
                alternarBotoes(false);
            }
        }
        atualizarHpLabel();
    }

    private void defender() {
        if (monstro == null) {
            logLabel.setText("Não há monstros para se defender.");
            return;
        }

        if (jogador.turnosParaDefender > 0) {
            logLabel.setText("Você ainda está em cooldown para Defender! " + jogador.turnosParaDefender + " turnos restantes.");
        } else {
            logLabel.setText("Você se defendeu! Reduziu o dano do monstro pela metade.");
            int danoMonstro = calcularDanoMonstro(monstro.tipo) / 2;
            jogador.hp -= danoMonstro;
            jogador.turnosParaDefender = 1;
            atualizarHpLabel();
        }
    }

    private void fugir() {
        if (monstro == null) {
            logLabel.setText("Não há monstros para fugir.");
            return;
        }

        if (random.nextInt(100) < 70) {
            logLabel.setText("Você fugiu! O monstro continua à solta...");
            monstro = null;
            alternarBotoes(false);
        } else {
            logLabel.setText("A fuga falhou! O monstro ainda está aqui...");
            int danoMonstro = calcularDanoMonstro(monstro.tipo);
            jogador.hp -= danoMonstro;
            logLabel.setText(logLabel.getText() + "\nO monstro atacou e causou " + danoMonstro + " de dano!");
            atualizarHpLabel();
        }
    }

    private void curar() {
        if (jogador.turnosParaCurar > 0) {
            logLabel.setText("Você ainda está em cooldown para Curar! " + jogador.turnosParaCurar + " turnos restantes.");
        } else {
            int cura = random.nextInt(21) + 10;
            jogador.hp += cura;
            logLabel.setText("Você se curou em " + cura + " pontos de vida!");
            jogador.turnosParaCurar = 2;
            atualizarHpLabel();
        }
    }

    private void ganharExperiencia() {
        int xp = random.nextInt(50) + 50;
        jogador.ganharExperiencia(xp);
        statusLabel.setText("Você ganhou " + xp + " de experiência!");
        atualizarHpLabel();
    }

    private void atualizarHpLabel() {
        hpLabel.setText("HP do jogador: " + jogador.hp);
        if (monstro != null) {
            hpLabel.setText(hpLabel.getText() + " | HP do monstro: " + monstro.hp);
        }
    }

    private int calcularDano(String arma) {
        return switch (arma) {
            case "espada" -> random.nextInt(30) + 1;
            case "arco" -> random.nextInt(20) + 1;
            case "mace" -> random.nextInt(40) + 1;
            default -> 0;
        };
    }

    private int calcularDanoMonstro(String tipo) {
        return switch (tipo) {
            case "Goblin" -> random.nextInt(10) + 5;
            case "Lobo" -> random.nextInt(15) + 10;
            case "Orc" -> random.nextInt(20) + 15;
            case "Dragão" -> random.nextInt(25) + 20;
            case "Hydra" -> random.nextInt(30) + 25;
            default -> 0;
        };
    }

    private Monstro gerarMonstro() {
        String[] tipos = {"Goblin", "Lobo", "Orc", "Dragão", "Hydra"};
        String tipo = tipos[random.nextInt(tipos.length)];
        int hp = random.nextInt(51) + 50;
        int ataque = calcularDanoMonstro(tipo);
        return new Monstro(tipo, hp, ataque);
    }

    private void alternarBotoes(boolean emBatalha) {
        explorarBtn.setVisible(!emBatalha);
        atacarBtn.setVisible(emBatalha);
        defenderBtn.setVisible(emBatalha);
        fugirBtn.setVisible(emBatalha);
        curarBtn.setVisible(emBatalha);
    }

    private void mostrarTelaDerrota() {
        VBox derrotaRoot = new VBox();
        Label derrotaLabel = new Label("Você foi derrotado!");
        ImageView espadaQuebradaView = new ImageView(new Image("file:src/end.png")); // Substitua pelo caminho da sua imagem
        derrotaRoot.getChildren().addAll(derrotaLabel, espadaQuebradaView);

        Scene derrotaScene = new Scene(derrotaRoot, 600, 400);
        Stage derrotaStage = new Stage();
        derrotaStage.setScene(derrotaScene);
        derrotaStage.setTitle("Derrota");
        derrotaStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Personagem {
    String nome;
    String arma;
    int hp;
    int ataque;
    int experiencia;
    int turnosParaCurar;
    int turnosParaDefender;

    public Personagem(String nome, String arma, int hp, int ataque) {
        this.nome = nome;
        this.arma = arma;
        this.hp = hp;
        this.ataque = ataque;
        this.experiencia = 0;
        this.turnosParaCurar = 0;

        this.turnosParaDefender = 0;
    }

    public void ganharExperiencia(int xp) {
        this.experiencia += xp;
        this.hp += xp / 10;
        this.ataque += xp / 20;
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
