from socket import *
import time
import json

#set server IP and Port
#serverName = "localhost"
serverName = "10.0.0.3"

serverPort = 9050
#create socket
clientSocket = socket(AF_INET, SOCK_DGRAM)

#create JSON Packet
packet = '{"msg": "good morning"}'
print("Packet to send contains:" + packet)

#send packet to server
clientSocket.sendto( packet.encode(), (serverName, serverPort))

#wait for receiver to send a response
receiverResponse, serverAddress = clientSocket.recvfrom(2048)

#on response decode and parse Json
decoded_packet = receiverResponse.decode()
parsed_json = json.loads(receiverResponse)

print("JSON received: " + str(receiverResponse))

#close socket
clientSocket.close()
