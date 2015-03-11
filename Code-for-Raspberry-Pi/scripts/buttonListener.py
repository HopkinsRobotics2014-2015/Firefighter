import RPi.GPIO as GPIO
import time
import atexit
import os

GPIO.setmode(GPIO.BCM)

button = 17
led_RED = 18
led_GREEN = 23

GPIO.setup(button,GPIO.IN)
GPIO.setup(led_RED,GPIO.OUT)
GPIO.setup(led_GREEN,GPIO.OUT)

def listen(pressed):
  count = 0
  while True:
    if (GPIO.input(button) == pressed):
      count += 1
      if count > 10:
        break
    else:
      count = 0

def on():
  GPIO.output(led_RED, 0)
  GPIO.output(led_GREEN, 1)
  os.system("./go &")
  
def off():
  GPIO.output(led_RED, 1)
  GPIO.output(led_GREEN, 0)
  os.system("sudo pkill java")
      
def clear():
  GPIO.cleanup()

atexit.register(clear)
      
off()

while True:
  listen(False)
  listen(True) # listen for the button to be pressed
  on()
  listen(False) # listen for the button to be released
  listen(True)
  off()
  