const { Kafka } = require('kafkajs');

// Configurações do Kafka
const kafka = new Kafka({
  clientId: 'order-processor',
  brokers: ['localhost:9092'], // Endereço do broker Kafka
});

const consumer = kafka.consumer({ groupId: 'order-processor' });

// Função para processar pedidos
const processarPedido = async (pedido) => {
  console.log(`Processando pedido ${pedido.pedido_id} do restaurante ${pedido.restaurante_id}`);
  console.log(`Itens: ${pedido.itens}`);
};

// Função principal 
const run = async () => {
  console.log("Pedidos do Restaurante ");
  await consumer.connect();
  await consumer.subscribe({ topic: 'pedidos-restaurante1', fromBeginning: true }); // Assina o tópico 

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const pedido = JSON.parse(message.value.toString());
      await processarPedido(pedido);
    },
  });
};

run().catch(console.error);
