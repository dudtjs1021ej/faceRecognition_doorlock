import os
import time
import RPi.GPIO as GPIO
from datetime import datetime

import firebase_admin
from firebase_admin import credentials,db

cred = credentials.Certificate("fir-connjava-firebase-adminsdk-qnktd-3592c424ff.json")

default_app = firebase_admin.initialize_app(cred, {
    'storageBucket': 'fir-connjava.appspot.com',
    'databaseURL':'https://fir-connjava-default-rtdb.firebaseio.com/'
})

ref_door=db.reference('door')

ref=db.reference()

# open_door()

lock =11

GPIO.setwarnings(False)

GPIO.setmode(GPIO.BOARD)

GPIO.setup(lock, GPIO.OUT)

while True:

    door=ref_door.get()
    
    if door=="open" or door=="close":

        print("문이 열렸습니다.")

        GPIO.output(lock, GPIO.HIGH)

        time.sleep(1)

        GPIO.output(lock, GPIO.LOW)

        time.sleep(1)

        ref.update({'door':'wait'})

GPIO.cleanup()
