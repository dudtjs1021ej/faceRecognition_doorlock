import RPi.GPIO as GPIO
import time
import sys
import signal
import v3_faceDetection_live_test

TRIG = 23
ECHO = 24

MAX_DISTANCE_CM = 300
MAX_DURATION_TIMEOUT = (MAX_DISTANCE_CM * 2 * 29) #17460 # 17460us = 300cm

def signal_handler(signal, frame):
        print('프로그램을 종료합니다.')
        GPIO.cleanup()
        sys.exit(0)
signal.signal(signal.SIGINT, signal_handler)

def distanceInCm(duration):

    return (duration/2)/29

def print_distance(distance):
    if distance == 0:
        distanceMsg = 'Distance : out of range\r'
    else:
        distanceMsg = 'Distance : ' + str(distance) + '\r'
    sys.stdout.write(distanceMsg)
    sys.stdout.flush()

def main():
    GPIO.setwarnings(False)
    GPIO.setmode(GPIO.BCM)

    # 핀 설정
    GPIO.setup(TRIG, GPIO.OUT) # 트리거 출력
    GPIO.setup(ECHO, GPIO.IN)  # 에코 입력

    print('프로그램을 종료하려면 CTRL + C 눌러주세요')

    GPIO.output(TRIG, False)
    time.sleep(1)

    #시작
    print('거리측정을 시작합니다....')
    while True:
        fail = False
        time.sleep(0.1)
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)

        # ECHO로 신호가 들어 올때까지 대기
        timeout = time.time()
        while GPIO.input(ECHO) == 0:
            #들어왔으면 시작 시간을 변수에 저장
            pulse_start = time.time()
            if ((pulse_start - timeout)*1000000) >= MAX_DURATION_TIMEOUT:
                break
                
        #ECHO로 인식 종료 시점까지 대기
        timeout = time.time()
        while GPIO.input(ECHO) == 1:
            #종료 시간 변수에 저장
            pulse_end = time.time()
            if ((pulse_end - pulse_start)*1000000) >= MAX_DURATION_TIMEOUT:
                print_distance(0) 
                break
            
        #인식 시작부터 종료까지의 차가 바로 거리 인식 시간
        pulse_duration = (pulse_end - pulse_start) * 1000000

        # 시간을 cm로 환산
        distance = distanceInCm(pulse_duration)
        distance = round(distance, 2)

        #표시
        print_distance(distance)
        if(distance <= 50):
            print('움직임을 감지하였습니다.')
            v3_faceDetection_live_test.main()   
    GPIO.cleanup()
main()

