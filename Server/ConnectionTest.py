# -*- encoding: UTF-8 -*-
from naoqi import ALProxy
tts = ALProxy("ALTextToSpeech", "10.0.0.7", 9559)
tts.say("Hello Guys! I am Pepper! Nice to Meet you!")
