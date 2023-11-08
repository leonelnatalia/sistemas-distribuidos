from zeep import Client

# URL do serviço de validação de CPF
url = "http://localhost:8080/"

# Crie um cliente SOAP
client = Client(url)

# CPF a ser validado
cpf = input("Digite o CPF para validação: ")

# Chame o método de validação de CPF
result = client.service.ValidateCPF(cpf)

if result:
    print(f"O CPF {cpf} é válido.")
else:
    print(f"O CPF {cpf} é inválido.")
