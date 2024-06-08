# Projeto A3 de Sistemas Distribuidos

## Integrantes do Grupo:

- João Otávio de Souza - RA: 125111350031
- Felipe Ferreira Marques da Silva - RA: 12523125161
- Emily Matos Vieira - RA: 12523140901
- Rodrigo Costa Sorge - RA: 12523127517

# Como rodar o projeto?

## Passos para Executar o Servidor e Clientes

Compile o Código do Servidor:
Salve o código do servidor em um arquivo chamado ServidorJokempo.java. Compile o código usando o seguinte comando no terminal: javac ServidorJokempo.java

Execute o Servidor:
Execute o servidor com o comando: java ServidorJokempo
O servidor começará a escutar na porta 12345 para conexões de clientes.

Compile o Código do Cliente:
Compile o código do cliente usando o seguinte comando no terminal: javac ClienteJokempo.java

Execute os Clientes:
Execute os clientes em máquinas diferentes ou em uma única máquina. Use o seguinte comando para executar o cliente, substituindo <endereço_do_servidor> pelo endereço IP ou nome do host da máquina onde o servidor está executando e <porta> pela porta 12345: java ClienteJokempo <endereço_do_servidor> 12345

#Nota Importante

- Certifique-se de que todos os jogadores e o servidor estão executando na mesma rede ou que as configurações de firewall e roteamento permitam a comunicação entre os clientes e o servidor.
- Verifique se a porta 12345 está aberta e disponível para uso no servidor e nos clientes.
