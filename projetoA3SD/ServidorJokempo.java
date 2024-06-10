import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServidorJokempo {
    private static final int PORTA = 12345;
    private static final Map<String, JogadorHandler> jogadores = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor Jokempô iniciado na porta " + PORTA);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket);

                JogadorHandler jogadorHandler = new JogadorHandler(clientSocket);
                Thread t = new Thread(jogadorHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class JogadorHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nome;

        public JogadorHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("Bem-vindo ao Jokempô! Escolha a modalidade de jogo:");
                out.println("1 - Jogador vs CPU");
                out.println("2 - Jogador vs Jogador");

                int modalidade = Integer.parseInt(in.readLine().trim());
                if (modalidade == 1) {
                    jogarPvE();
                } else if (modalidade == 2) {
                    jogarPvP();
                } else {
                    out.println("Opção inválida. Conexão encerrada.");
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void jogarPvE() throws IOException {
            String[] opcoes = {"Pedra", "Papel", "Tesoura"};
            Random random = new Random();
            int vitorias = 0;
            int derrotas = 0;
            int empates = 0;
            int erros = 0;

            while (true) {
                out.println("Escolha uma opção (Pedra, Papel, Tesoura) ou 'Sair' para sair:");
                String escolhaUsuario = in.readLine().trim();
                if (escolhaUsuario.equalsIgnoreCase("Sair")) {
                    break;
                }

                int escolhaCPU = random.nextInt(3);
                String escolhaCPUStr = opcoes[escolhaCPU];

                out.println("CPU escolheu: " + escolhaCPUStr);

                String resultado = determinarResultado(escolhaUsuario, escolhaCPUStr);
                out.println(resultado);

                if (resultado.equals("Empate")) {
                    empates++;
                } else if (resultado.equals("Você venceu!")) {
                    vitorias++;
                } else if (resultado.equals("Resposta errada! Por favor, escolha 'Pedra', 'Papel' ou 'Tesoura'.")) {
                    erros++;
                } else {
                    derrotas++;
                }
            }

            out.println("Fim do jogo. Vitórias: " + vitorias + ", Derrotas: " + derrotas + ", Empates: " + empates + ", Erros de Digitação: " + erros);
        }

        private void jogarPvP() throws IOException {
            out.println("Digite seu nome de jogador:");
            nome = in.readLine().trim();
            JogadorHandler adversario = null;

            synchronized (jogadores) {
                jogadores.put(nome, this);
                if (jogadores.size() % 2 != 0) {
                    out.println("Aguardando outro jogador...");
                    try {
                        jogadores.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    for (Map.Entry<String, JogadorHandler> entry : jogadores.entrySet()) {
                        if (!entry.getKey().equals(nome)) {
                            adversario = entry.getValue();
                            break;
                        }
                    }
                    jogadores.notifyAll();
                }
            }

            if (adversario != null) {
                int vitoriasJogUm = 0;
                int vitoriasJogDois = 0;
                int derrotasJogUm = 0;
                int derrotasJogDois = 0;
                int empates = 0;
                int erros = 0;

                while (true) {
                    out.println("Escolha uma opção (Pedra, Papel, Tesoura) ou 'Sair' para sair:");
                    String escolhaPlayer1 = in.readLine().trim();
                    if (escolhaPlayer1.equalsIgnoreCase("Sair")) {
                        break;
                    }

                    adversario.out.println(nome + " fez a escolha. Faça a sua escolha (Pedra, Papel, Tesoura): ");
                    String escolhaPlayer2 = adversario.in.readLine().trim();

                    out.println(adversario.nome + " escolheu: " + escolhaPlayer2);
                    adversario.out.println(nome + " escolheu: " + escolhaPlayer1);

                    String resultado = determinarResultado2(escolhaPlayer1, escolhaPlayer2);
                    out.println(resultado);
                    adversario.out.println(resultado);

                    if (resultado.equals("Empate")) {
                        empates++;
                    } else if (resultado.contains("Você venceu Jogador 1!")) {
                        vitoriasJogUm++;
                        derrotasJogDois++;
                    }else if (resultado.equals("Resposta errada! Por favor, escolha 'Pedra', 'Papel' ou 'Tesoura'.")) {
                        erros++;
                    } else {
                        vitoriasJogDois++;
                        derrotasJogUm++;
                    }
                }

                out.println("Fim do jogo. Potuação Jogador 1 -> Vitórias: " + vitoriasJogUm + ", Derrotas: " + derrotasJogUm + ", Empates: " + empates + ", Erros de Digitação: " + erros);
                adversario.out.println("Fim do jogo. Potuação Jogador 2 -> Vitórias: " + vitoriasJogDois + ", Derrotas: " + derrotasJogDois + ", Empates: " + empates + ", Erros de Digitação: " + erros);
            }
            synchronized (jogadores) {
                jogadores.remove(nome);
                jogadores.notifyAll();
            }
        }

        private String determinarResultado(String escolhaUsuario, String escolhaCPU) {
            if (escolhaUsuario.equalsIgnoreCase(escolhaCPU)) {
                return "Empate";
            }  else if ((escolhaUsuario.equalsIgnoreCase("Pedra") && escolhaCPU.equalsIgnoreCase("Tesoura")) ||
                    (escolhaUsuario.equalsIgnoreCase("Papel") && escolhaCPU.equalsIgnoreCase("Pedra")) ||
                    (escolhaUsuario.equalsIgnoreCase("Tesoura") && escolhaCPU.equalsIgnoreCase("Papel"))) {
                return "Você venceu!";
            } if (!escolhaUsuario.equalsIgnoreCase("Pedra") &&
                    !escolhaUsuario.equalsIgnoreCase("Papel") &&
                    !escolhaUsuario.equalsIgnoreCase("Tesoura")) {
                return "Resposta errada! Por favor, escolha 'Pedra', 'Papel' ou 'Tesoura'.";
            } else {
                return "Você perdeu!";
            }
        }

        private String determinarResultado2(String escolha1, String escolha2) {
            if (escolha1.equalsIgnoreCase(escolha2)) {
                return "Empate";
            } else if ((escolha1.equalsIgnoreCase("Pedra") && escolha2.equalsIgnoreCase("Tesoura")) ||
                    (escolha1.equalsIgnoreCase("Papel") && escolha2.equalsIgnoreCase("Pedra")) ||
                    (escolha1.equalsIgnoreCase("Tesoura") && escolha2.equalsIgnoreCase("Papel"))) {
                return "Você venceu Jogador 1! Você perdeu Jogador 2!";
            } if (!escolha1.equalsIgnoreCase("Pedra") &&
                    !escolha1.equalsIgnoreCase("Papel") &&
                    !escolha1.equalsIgnoreCase("Tesoura") ||
                    !escolha2.equalsIgnoreCase("Pedra") &&
                    !escolha2.equalsIgnoreCase("Papel") &&
                    !escolha2.equalsIgnoreCase("Tesoura")){
                return "Resposta errada! Por favor, escolha 'Pedra', 'Papel' ou 'Tesoura'.";
            } else {
                return "Você venceu Jogador 2! Você perdeu Jogador 1!";
            }
        }
    }
}
