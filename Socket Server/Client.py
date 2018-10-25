from socket import *
import time
import json

#set server IP and Port
#serverName = "169.254.235.125"
serverName = "10.0.0.3"

serverPort = 9051
#create socket
clientSocket = socket(AF_INET, SOCK_DGRAM)

#create JSON Packet
packet = '{"msg": "no u"}'
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
