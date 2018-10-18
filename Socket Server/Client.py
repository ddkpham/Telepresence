from socket import *
import time
import json

#set server IP and Port
serverName = "localhost"
#serverName = "10.0.0.3"
serverPort = 9050
#create socket
clientSocket = socket(AF_INET, SOCK_DGRAM)

#create JSON Packet
integer = 15
packet = '{"test": '+str(integer)+'}'
print("Packet to send contains: data = "+str(integer))

#send packet to server
clientSocket.sendto( packet.encode(), (serverName, serverPort))

#wait for receiver to send a response
receiverResponse, serverAddress = clientSocket.recvfrom(2048)

#on response decode and parse Json
decoded_packet = receiverResponse.decode()
parsed_json = json.loads(receiverResponse)

print(receiverResponse)

#close socket
clientSocket.close()
