from pysimplesoap.server import SoapDispatcher, SOAPHandler
from http.server import HTTPServer

def validate_cpf(cpf):
    if len(cpf) != 11:
        return False
    return True

dispatcher = SoapDispatcher(
    'cpf_validator',
    location='http://localhost:8080/',
    action='http://localhost:8080/',  # URL de ação
    namespace='http://example.com/cpf-validation',  # Namespace do serviço
    prefix='ns',
    trace=True,
    ns=False)

# Registrar a função de validação de CPF
dispatcher.register_function('ValidateCPF', validate_cpf,
                            returns={'valid': bool},
                            args={'cpf': str})

# Configurar o servidor HTTP
httpd = HTTPServer(('', 8080), SOAPHandler)
httpd.dispatcher = dispatcher

if __name__ == '__main__':
    print("Servidor SOAP de validação de CPF rodando em http://localhost:8080/")
    httpd.serve_forever()
