import socket

SERVER_IP = "127.0.0.1"  # Endereço IP do servidor
SERVER_PORT = 8888       # Porta do servidor

# Cria um socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    # Conecta-se ao servidor
    client_socket.connect((SERVER_IP, SERVER_PORT))
    print("*** CLIENT ***")

    doc_type = input("Digite o tipo de documento (0 para CPF, 1 para CNPJ): ")

    doc_number = input("Digite o número do documento: ")

    # Prepara a requisição
    request = f"TYPE:{doc_type};;DOC:{doc_number};;\n" # meu namorado inteligente que resolveu

    # Envia a requisição para o servidor
    client_socket.send(request.encode())

    while True:
        # Recebe a resposta do servidor
        response = client_socket.recv(1024).decode()

        if not response:
            break

        print(response)

except Exception as e:
    print("Erro:", e)

finally:
    # Fecha o socket do cliente
    client_socket.close()
