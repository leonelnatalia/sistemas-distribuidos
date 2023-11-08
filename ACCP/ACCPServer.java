
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class ACCPServer {

    private static final int PORT = 8888; //define a porta
    private static ArrayList<Socket> clientSockets = new ArrayList<>(); // define a lista dse sockets
    private static final int MAX_CONNECTIONS = 10; //maximo de conexoes

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT); // inicia servidor
            System.out.println("Servidor ACCP iniciado na porta " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // novo cliente
                // Verifique o número máximo de conexões ativas
                if (clientSockets.size() >= MAX_CONNECTIONS) {
                    System.out.println("Número máximo de conexões atingido. Rejeitando conexão.");
                    clientSocket.close(); // fecha cliente
                } else {
                    clientSockets.add(clientSocket);
                    System.out.println("Nova conexão estabelecida.");
                    // Lê a requisição do cliente e cria uma thread para processá-la
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8")); // buffer de leitura UTF-8
                    String request = in.readLine(); // Lê a linha de requisição

                    //thread para lidar com o novo cliente com a requisição dele
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, request));
                    clientThread.start();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private String request;

        public ClientHandler(Socket socket, String request) {
            this.clientSocket = socket;
            this.request = request;
        }

        @Override
        public void run() {
            try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                // Processa a requisição e gera a resposta
                String response = processRequest(request);

                // Envia a resposta de volta ao cliente
                out.println(response);

                // Fecha o socket do cliente e remove-o da lista de sockets ativos
                clientSocket.close();
                clientSockets.remove(clientSocket);
                System.out.println("Conexão encerrada.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Função para processar a requisição e gerar a resposta
        private String processRequest(String request) {
            System.out.println(request);
            if (request.length() > 41) {
                return "TYPE:-1;;DOC:;;STATUS:202;;;"; // Erro de tamanho
            } else {
                try {
                    // Divide a requisição nos campos separados por ";;"
                    String[] fields = request.split(";;");

                    // Inicializa as variáveis para armazenar o tipo de documento e o número do documento
                    int type = -1;
                    String doc = "";

                    // Percorre os campos da requisição
                    for (String field : fields) {
                        String[] parts = field.split(":");//  guarda a informação dps do : em parts
                        if (parts.length == 2) {// verifica se foi divido em 2 partes
                            String key = parts[0].trim();
                            String value = parts[1].trim();

                            // Verifica o tipo de campo
                            if (key.equals("TYPE")) {
                                type = Integer.parseInt(value); // guarda o tipo
                            } else if (key.equals("DOC")) {
                                doc = value; // guarda o doc
                            }
                        }
                    }

                    // Realiza a validação do CPF ou CNPJ
                    boolean isValid = false;
                    if (type == 0) {
                        // Validação de CPF
                        isValid = validateCPF(doc);
                    } else if (type == 1) {
                        // Validação de CNPJ
                        isValid = validateCNPJ(doc);
                    } else {
                        return "TYPE:" + type + ";;DOC:" + doc + ";;STATUS:404;;;"; // TYPE inválido
                    }
                    // Gera a resposta com base na validação
                    if (isValid) {
                        return "TYPE:" + type + ";;DOC:" + doc + ";;STATUS:101;;;"; // CPF/CNPJ válido
                    } else {
                        return "TYPE:" + type + ";;DOC:" + doc + ";;STATUS:000;;;"; // CPF/CNPJ inválido
                    }
                } catch (Exception e) {
                    // Em caso de erro na requisição, retorna um status de erro
                    return "TYPE:-1;;DOC:;;STATUS:303;;;"; // Erro na requisição
                }
            }
        }

        // Função de exemplo para validação de CPF
        private boolean validateCPF(String CPF) {
            // considera-se erro CPF's formados por uma sequencia de numeros iguais
            if (CPF.equals("00000000000")
                    || CPF.equals("11111111111")
                    || CPF.equals("22222222222") || CPF.equals("33333333333")
                    || CPF.equals("44444444444") || CPF.equals("55555555555")
                    || CPF.equals("66666666666") || CPF.equals("77777777777")
                    || CPF.equals("88888888888") || CPF.equals("99999999999")
                    || (CPF.length() != 11)) {
                return (false);
            }

            char dig10, dig11;
            int sm, i, r, num, peso;

            try {
                // Calculo do 1o. Digito Verificador
                sm = 0;
                peso = 10;
                for (i = 0; i < 9; i++) {
                    // converte o i-esimo caractere do CPF em um numero:
                    // por exemplo, transforma o caractere '0' no inteiro 0
                    // (48 eh a posicao de '0' na tabela ASCII)
                    num = (int) (CPF.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso - 1;
                }

                r = 11 - (sm % 11);
                if ((r == 10) || (r == 11)) {
                    dig10 = '0';
                } else {
                    dig10 = (char) (r + 48); // converte no respectivo caractere numerico
                }
                // Calculo do 2o. Digito Verificador
                sm = 0;
                peso = 11;
                for (i = 0; i < 10; i++) {
                    num = (int) (CPF.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso - 1;
                }

                r = 11 - (sm % 11);
                if ((r == 10) || (r == 11)) {
                    dig11 = '0';
                } else {
                    dig11 = (char) (r + 48);
                }

                // Verifica se os digitos calculados conferem com os digitos informados.
                if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) {
                    return (true);
                } else {
                    return (false);
                }
            } catch (InputMismatchException erro) {
                return (false);
            }
        }

        private boolean validateCNPJ(String CNPJ) {
            if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111")
                    || CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333")
                    || CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555")
                    || CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777")
                    || CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999")
                    || (CNPJ.length() != 14)) {
                return (false);
            }

            char dig13, dig14;
            int sm, i, r, num, peso;

// "try" - protege o código para eventuais erros de conversao de tipo (int)
            try {
// Calculo do 1o. Digito Verificador
                sm = 0;
                peso = 2;
                for (i = 11; i >= 0; i--) {
// converte o i-ésimo caractere do CNPJ em um número:
// por exemplo, transforma o caractere '0' no inteiro 0
// (48 eh a posição de '0' na tabela ASCII)
                    num = (int) (CNPJ.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso + 1;
                    if (peso == 10) {
                        peso = 2;
                    }
                }

                r = sm % 11;
                if ((r == 0) || (r == 1)) {
                    dig13 = '0';
                } else {
                    dig13 = (char) ((11 - r) + 48);
                }

// Calculo do 2o. Digito Verificador
                sm = 0;
                peso = 2;
                for (i = 12; i >= 0; i--) {
                    num = (int) (CNPJ.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso + 1;
                    if (peso == 10) {
                        peso = 2;
                    }
                }

                r = sm % 11;
                if ((r == 0) || (r == 1)) {
                    dig14 = '0';
                } else {
                    dig14 = (char) ((11 - r) + 48);
                }

// Verifica se os dígitos calculados conferem com os dígitos informados.
                if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13))) {
                    return (true);
                } else {
                    return (false);
                }
            } catch (InputMismatchException erro) {
                return (false);
            }
        }
    }
}
