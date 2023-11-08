# Copyright 2015 gRPC authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""The Python implementation of the GRPC helloworld.Greeter server."""

from concurrent import futures
import logging

import grpc
import helloworld_pb2
import helloworld_pb2_grpc


class Greeter(helloworld_pb2_grpc.GreeterServicer):
    
    def SayHello(self, request, context):
        if(len(request.request)>18):
            return helloworld_pb2.Rep(response="TYPE:0; DOC:0; STATUS: 202;")
        separa = request.request.split(';') #separa a requisição onde tiver espaço
        tipo = int(separa[0])
        doc = separa[1]
        status=''
        if tipo == 0:
            status= valida_cpf(doc)
            return helloworld_pb2.Rep(response="TYPE: %d; DOC: %s; STATUS: %s;" % (tipo, doc, status))
        if tipo == 1: 
            status= valida_cnpj(doc)
            return helloworld_pb2.Rep(response="TYPE: %d; DOC: %s; STATUS: %s;" % (tipo, doc, status))
        else:
            return helloworld_pb2.Rep(response="TYPE: %d; DOC: %s; STATUS: 404;" % (tipo, doc))
       


def valida_cpf(cpf):
    cpf = ''.join(filter(str.isdigit, cpf))
    
    if len(cpf) != 11:
        return '000'

    soma = 0
    for i in range(9):
        soma += int(cpf[i]) * (10 - i)
    resto = soma % 11
    if resto < 2:
        digito_verificador1 = 0
    else:
        digito_verificador1 = 11 - resto

    soma = 0
    for i in range(10):
        soma += int(cpf[i]) * (11 - i)
    resto = soma % 11
    if resto < 2:
        digito_verificador2 = 0
    else:
        digito_verificador2 = 11 - resto

    if int(cpf[9]) == digito_verificador1 and int(cpf[10]) == digito_verificador2:
        return '101'
    else:
        return '000'


def valida_cnpj(cnpj):
    cnpj = ''.join(filter(str.isdigit, cnpj))

    if len(cnpj) != 14:
        return '000'

    peso = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
    soma = 0
    for i in range(12):
        soma += int(cnpj[i]) * peso[i]
    resto = soma % 11
    if resto < 2:
        digito_verificador1 = 0
    else:
        digito_verificador1 = 11 - resto

    peso = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
    soma = 0
    for i in range(13):
        soma += int(cnpj[i]) * peso[i]
    resto = soma % 11
    if resto < 2:
        digito_verificador2 = 0
    else:
        digito_verificador2 = 11 - resto

    if int(cnpj[12]) == digito_verificador1 and int(cnpj[13]) == digito_verificador2:
        return '101'
    else:
        return '000'
    

def serve():
    port = "50051"
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    helloworld_pb2_grpc.add_GreeterServicer_to_server(Greeter(), server)
    server.add_insecure_port("[::]:" + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == "__main__":
    logging.basicConfig()
    serve()


