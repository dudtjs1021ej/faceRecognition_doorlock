import RPi.GPIO as GPIO
import time
import sys
import signal
import v3_faceDetection_live

#GPIO 핀
TRIG = 23 # 트리거
ECHO = 24 # 에코

#거리 타임 아웃 용 => 상의 후 설정
MAX_DISTANCE_CM = 300
MAX_DURATION_TIMEOUT = (MAX_DISTANCE_CM * 2 * 29.1) #17460 # 17460us = 300cm

# 키보드 CTRL + C 누르면 종료 되게 처리
def signal_handler(signal, frame):
        print('You pressed Ctrl+C!')
        GPIO.cleanup()
        sys.exit(0)
signal.signal(signal.SIGINT, signal_handler)

# cm 환산 함수
def distanceInCm(duration):
    
    return (duration/2)/29.1

# 거리 표시
def print_distance(distance):
    if distance == 0:
        distanceMsg = 'Distance : out of range\r'
    else:
        distanceMsg = 'Distance : ' + str(distance) + 'cm' + '\r'
    sys.stdout.write(distanceMsg)
    sys.stdout.flush()


def main():
    # 파이썬 GPIO 모드
    GPIO.setmode(GPIO.BCM)

    # 핀 설정
    GPIO.setup(TRIG, GPIO.OUT) # 트리거 출력
    GPIO.setup(ECHO, GPIO.IN)  # 에코 입력

    print('To Exit, Press the CTRL+C Keys')
        
    GPIO.output(TRIG, False)
    print('Waiting For Sensor To Ready')
    time.sleep(1) # 1초

    print('Start!!')
    while True:
       
        fail = False
        time.sleep(0.1)
        
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)

       
        timeout = time.time()
        while GPIO.input(ECHO) == 0:
           
            pulse_start = time.time()
            if ((pulse_start - timeout)*1000000) >= MAX_DURATION_TIMEOUT:
                
                fail = True
                break
                
            
        if fail:
            continue
        
        timeout = time.time()
        while GPIO.input(ECHO) == 1:
            #종료 시간 변수에 저장
            pulse_end = time.time()
            if ((pulse_end - pulse_start)*1000000) >= MAX_DURATION_TIMEOUT:
                print_distance(0) 
               
                fail = True
                break
     
        if fail:
            continue

        pulse_duration = (pulse_end - pulse_start) * 1000000

        distance = distanceInCm(pulse_duration)

        distance = round(distance, 2)

        print_distance(distance)

        if(distance <=50):
            v3_faceDetection_live.main()
    GPIO.cleanup()

main()
