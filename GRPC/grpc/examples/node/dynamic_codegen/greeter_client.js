var PROTO_PATH = __dirname + '/../../protos/helloworld.proto';

var parseArgs = require('minimist');
var grpc = require('@grpc/grpc-js');
var protoLoader = require('@grpc/proto-loader');
var packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {
        keepCase: true,
        longs: String,
        enums: String,
        defaults: true,
        oneofs: true
    }
);
var hello_proto = grpc.loadPackageDefinition(packageDefinition).helloworld;

const readline = require('readline');

function run() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    rl.question('Insira o tipo (0-CPF, 1-CNPJ): ', (tipo) => {
        rl.question('Insira o documento: ', (doc) => {
            const requisicao = tipo + ';' + doc + ';';

            const client = new hello_proto.Greeter('localhost:50051', grpc.credentials.createInsecure());

            client.SayHello({ request: requisicao }, (error, response) => {
                if (!error) {
                    console.log(response.response);
                } else {
                    console.error('Erro ao chamar o serviço:', error);
                }

                rl.close();
            });
        });
    });
}

run();
