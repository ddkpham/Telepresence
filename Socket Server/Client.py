from socket import *
import time
import json

#serverName = 'localhost'
serverName = "127.0.0.5"
serverPort = 50007
clientSocket = socket(AF_INET, SOCK_DGRAM)

integer = 15
packet = '{"test": '+str(integer)+'}'
print("Packet to send contains: data = "+str(integer))
#print("Packet thing = "+ packet[0])

clientSocket.sendto( packet.encode(), (serverName, serverPort))

receiverResponse, serverAddress = clientSocket.recvfrom(2048)
decoded_packet = receiverResponse.decode()
parsed_json = json.loads(receiverResponse)

print(receiverResponse)
#print (parsed_json)
#print ("Packet received contains: data = " + str(parsed_json['test']))

clientSocket.close()
