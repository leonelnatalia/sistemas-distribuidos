import json
from confluent_kafka import Producer

# Configurações do Kafka
config = {
    'bootstrap.servers': 'localhost:9092',  # Endereço do broker Kafka
    'client.id': 'restaurant-producer'
}

def enviar_pedidos():
    producer = Producer(config)

    while True:
        pedido_id = input("Informe o ID do pedido (-1 para sair): ")
        
        # Verifique se o usuário deseja sair
        if pedido_id == '-1':
            break

        restaurante_id = input("Informe o ID do restaurante: ")
        itens = input("Informe os itens do pedido (separados por vírgula): ").split(',')
        total = float(input("Informe o valor total do pedido: "))

        pedido = {
            'pedido_id': pedido_id,
            'restaurante_id': restaurante_id,
            'itens': itens,
            'total': total
        }

        pedido_json = json.dumps(pedido)
        tópico_restaurante = f'pedidos-{restaurante_id}'
        producer.produce(tópico_restaurante, key=str(pedido['pedido_id']), value=pedido_json)
        producer.flush()
        print(f"Pedido {pedido['pedido_id']} enviado com sucesso para o tópico '{tópico_restaurante}'.")

if __name__ == "__main__":
    enviar_pedidos()
