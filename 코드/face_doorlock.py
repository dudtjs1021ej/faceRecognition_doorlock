import os

import time

import RPi.GPIO as GPIO

from datetime import datetime

def main():
    lock =17
    GPIO.setwarnings(False)

    GPIO.setmode(GPIO.BCM)

    GPIO.setup(lock, GPIO.OUT)

    print("open")
    GPIO.output(lock, GPIO.HIGH)
    time.sleep(1)
    GPIO.output(lock,GPIO.LOW)
    time.sleep(2)

    time.sleep(3)

    print("close")
    GPIO.output(lock, GPIO.HIGH)
    time.sleep(1)
    GPIO.output(lock,GPIO.LOW)
    time.sleep(1)
    # time.sleep(3)
    #GPIO.output(lock, GPIO.LOW)
    GPIO.cleanup()