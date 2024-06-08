import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteJokempo {
    public static void main(String[] args) {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Digite o IP do servidor: ");
            String servidorIP = consoleReader.readLine().trim();

            System.out.print("Digite a porta: ");
            String portaStr = consoleReader.readLine().trim();
            int porta = Integer.parseInt(portaStr); // Assume que o usuário irá digitar um número válido

            try (Socket socket = new Socket(servidorIP, porta);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println(in.readLine()); // Mensagem de boas-vindas do servidor
                System.out.println(in.readLine());
                System.out.println(in.readLine());

                String modalidade = scanner.readLine().trim();
                out.println(modalidade);

                if (modalidade.equals("1")) {
                    jogarPvE(scanner, in, out);
                } else if (modalidade.equals("2")) {
                    jogarPvP(scanner, in, out);
                } else {
                    System.out.println("Opção inválida. Conexão encerrada.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void jogarPvE(BufferedReader scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            System.out.println(in.readLine());
            String escolha = scanner.readLine().trim();
            out.println(escolha);

            if (escolha.equalsIgnoreCase("Sair")) {
                break;
            }

            System.out.println(in.readLine()); // CPU escolheu
            System.out.println(in.readLine()); // Resultado
        }

        System.out.println(in.readLine()); // Fim do jogo
    }

    private static void jogarPvP(BufferedReader scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println(in.readLine()); // Digite seu nome
        String nome = scanner.readLine();
        out.println(nome);

        while (true) {
            System.out.println(in.readLine());
            String escolha = scanner.readLine().trim();
            out.println(escolha);

            if (escolha.equalsIgnoreCase("Sair")) {
                break;
            }

            System.out.println(in.readLine()); // Aguardando outro jogador
            System.out.println(in.readLine()); // Resultado
        }

        System.out.println(in.readLine()); // Fim do jogo
    }
}