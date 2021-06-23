from PyQt5 import uic  # UI불러오기
import threading
import dkdk_service
import pygame
import time
import serverConnection
from frontMenu import *
import frontMenu

test = None
back_class = uic.loadUiType("back.ui")[0]  # 배경 영상 UI
url2 = 'http://175.118.28.138/payment/decrease'


# 명령 입력 스레드
class KeySignal(QThread):
    signal10 = pyqtSignal()
    signal11 = pyqtSignal()
    signal12 = pyqtSignal(str)
    signal13 = pyqtSignal()
    signal14 = pyqtSignal()
    signal15 = pyqtSignal()
    signal16 = pyqtSignal()
    signal17 = pyqtSignal(str)
    signal18 = pyqtSignal()
    signal19 = pyqtSignal()
    signal20 = pyqtSignal()
    signal21 = pyqtSignal()
    signal22 = pyqtSignal()
    signal23 = pyqtSignal()
    signal24 = pyqtSignal()
    signal25 = pyqtSignal()
    signal26 = pyqtSignal()
    signal27 = pyqtSignal()
    signal28 = pyqtSignal()

    def run(self):
        message = ''
        check = 0
        video_check = 0
        reserve_check = 0
        while True:
            try:
                while dkdk_service.rcv != '':
                    message = dkdk_service.rcv
                    print(message)
                    break
            except:
                message = 'none'

            if message == '11':  # 11 : 메뉴
                check = 1
                reserve_check = 0
                self.signal11.emit()
            elif message == '12':  # 12 : 취소
                if check == 1 or reserve_check == 1:  # 취소(메뉴화면)
                    check = 0
                    reserve_check = 0
                    self.signal12.emit('menu')
                else:  # 취소(노래재생화면)
                    self.signal12.emit('video')
            elif message == '13':  # 13 : 아래 화살표
                self.signal13.emit()
            elif message == '14':  # 14 : 위 화살표
                self.signal14.emit()
            elif message == '15' and check == 1:  # 15 : 오른쪽 화살표
                self.signal15.emit()
            elif message == '16' and check == 1:  # 16 : 왼쪽 화살표
                self.signal16.emit()
            elif re.search('^17,', str(message)) is not None and check == 1:  # 17 : 노래 문자열 입력 (17,name) 형식
                self.signal17.emit(message)
            elif re.search('^18,', str(message)) is not None and check == 1:  # 18 : 예약하기 (선택된 행)
                temp = message.split(',')
                frontMenu.nickname = temp[1]
                self.signal18.emit()
            elif message == '19' and reserve_check == 0:  # 19 : 시작하기 (선택된 행 or 예약목록)
                check = 0
                self.signal19.emit()
            elif message == '20':  # 20 : 예약목록 불러오기
                reserve_check = 1
                check = 0
                self.signal20.emit()
            elif message == '21,1':  # 21,1 : 볼륨조절(up)
                self.signal21.emit()
            elif message == '21,0':  # 21,0 : 볼륨조절(down)
                self.signal26.emit()
            elif message == '22,1':  # 22,1 : 피치조절(up)
                self.signal22.emit()
            elif message == '22,0':  # 22,0 : 피치조절(down)
                self.signal27.emit()
            elif message == '23,1':  # 23,1 : 템포조절(up)
                self.signal23.emit()
            elif message == '23,0':  # 23,0 : 템포조절(down)
                self.signal28.emit()
            elif message == '24' and reserve_check == 1:  # 24 : 예약취소
                self.signal24.emit()
            elif re.search('^25,', str(message)) is not None and check == 1:  # 18 : 예약하기 (선택된 행)
                temp = message.split(',')
                frontMenu.nickname = temp[1]  # 25 : 우선 예약
                self.signal25.emit()

            message = ''
            dkdk_service.rcv = ''
            time.sleep(0.1)  # 0.1초 기다림


# 영상 신호 클래스
class VideoSignal(QObject):
    video_signal10 = pyqtSignal()
    video_signal11 = pyqtSignal(str)
    video_signal12 = pyqtSignal()

    def check_list(self, checkType):
        if checkType == '10':
            self.video_signal10.emit()

    def check_song(self, checkType, name):
        if checkType == '11':
            self.video_signal11.emit(name)
        elif checkType == '12':
            self.video_signal12.emit()


# 영상 재생 클래스
class BackWindow(QMainWindow, back_class):
    def __init__(self, videoObj, reserve_bar):
        super().__init__()
        self.setupUi(self)
        self.time = 0
        self.check = 0

        self.setWindowFlag(Qt.FramelessWindowHint)
        self.mediaPlayer = QMediaPlayer(None, QMediaPlayer.VideoSurface)  # 미디어 플레이어 설정
        self.mediaPlayer.setVideoOutput(self.widget)
        location = QDesktopWidget().availableGeometry()  # 메인화면 크기저장
        self.widget.resize(location.width(), location.height())  # 메인화면 크기 설정

        self.timerVar = QTimer()
        self.timerVar.timeout.connect(self.printTime)
        pygame.mixer.init()
        # test = None
        self.videoSig = videoObj
        self.videoSig.video_signal10.connect(lambda: self.video_signal10_emitted(reserve_bar))
        self.videoSig.video_signal11.connect(self.video_signal11_emitted)
        self.videoSig.video_signal12.connect(self.video_signal12_emitted)
        self.mainScreen()

        self.mediaPlayer.stateChanged.connect(lambda: self.mediaStateChanged(reserve_bar))

    def mainScreen(self):  # 메인화면 재생 함수
        self.mediaPlayer.setMedia(QMediaContent(QUrl.fromLocalFile("/home/pi/project/NBackVideo.mp4")))
        self.mediaPlayer.play()

    def printTime(self):  # 비디오 변화 감지 딜레이
        self.time += 1
        if self.time == 20:
            self.timerVar.stop()

    def mediaStateChanged(self, reserve_bar):  # 노래 종료시
        # 노래 정지시 & 타이머 6초이상 경과 & 중복실행 불가
        global test
        if self.mediaPlayer.state() == QMediaPlayer.StoppedState and self.time > 6 and self.check == 0:
            self.check = 1
            file_data = OrderedDict()
            if len(reserve_list) != 0:  # 남은 노래 있을시
                self.mediaPlayer.setMedia(
                    QMediaContent(QUrl.fromLocalFile("/home/pi/project/" + reserve_list[0][0] + ".mp4")))
                test = pygame.mixer.Sound(reserve_list[0][0] + ".wav")
                test.play()
                self.mediaPlayer.play()

                file_data["email"] = reserve_list[0][3]
                requests.post(url2, data=json.dumps(file_data))
                frontMenu.run_video = 1
                del reserve_list[0]  # 재생노래 삭제

                reserve_bar.reserveEdit.clear()
                for val in reserve_list:  # 상단 bar 재설정
                    reserve_bar.reserveEdit.setText(reserve_bar.reserveEdit.text() + val[0] + "\t")
                self.time = 0
                self.check = 0
                self.timerVar.start()
            else:  # 남은 노래 없을시
                self.time = 0
                self.check = 0
                frontMenu.run_video = 0
                self.mainScreen()

    @pyqtSlot()  # 전체 재생
    def video_signal10_emitted(self, reserve_bar):
        global test
        self.check = 0
        file_data = OrderedDict()

        self.mediaPlayer.setMedia(QMediaContent(QUrl.fromLocalFile("/home/pi/project/" + reserve_list[0][0] + ".mp4")))
        test = pygame.mixer.Sound(reserve_list[0][0] + ".wav")
        test.play()
        self.mediaPlayer.play()

        file_data["email"] = reserve_list[0][3]
        requests.post(url2, data=json.dumps(file_data))
        frontMenu.run_video = 1

        del reserve_list[0]  # 재생노래 삭제
        reserve_bar.reserveEdit.clear()
        for val in reserve_list:  # 상단 bar 재설정
            reserve_bar.reserveEdit.setText(reserve_bar.reserveEdit.text() + val[0] + "\t")

        self.timerVar.setInterval(500)
        self.timerVar.start()

    @pyqtSlot(str)  # 선택 노래 우선재생
    def video_signal11_emitted(self, arg):
        global test
        self.mediaPlayer.setMedia(QMediaContent(QUrl.fromLocalFile("/home/pi/project/" + arg + ".mp4")))
        self.mediaPlayer.play()
        frontMenu.run_video = 1
        self.timerVar.setInterval(500)
        self.timerVar.start()

        test = pygame.mixer.Sound(arg + ".wav")
        test.play()

    @pyqtSlot()  # 재생 중지
    def video_signal12_emitted(self):
        global test
        self.check = 1
        self.timerVar.stop()
        self.time = 0
        if self.mediaPlayer.state() == QtMultimedia.QMediaPlayer.PlayingState:
            self.mediaPlayer.stop()
            test.stop()
            frontMenu.run_video = 0
            self.mainScreen()


if __name__ == "__main__":
    app = QApplication(sys.argv)
    # 객체생성
    soundmenu = soundMenu()
    keySignal = KeySignal()
    videosignal = VideoSignal()
    topMenu = TopMenu()
    # topMenu.resize(1150, 40)  # 사이즈 조절
    mywindow = MyWindow(videosignal, keySignal, topMenu, soundmenu)
    # mywindow.resize(1000, 500)  # 사이즈 조절
    backwindow = BackWindow(videosignal, topMenu)

    t = threading.Thread(target=dkdk_service.main)
    t.start()
    scheduleTread = threading.Thread(target=serverConnection.scheduleDownload)
    scheduleTread.start()

    # 화면출력
    backwindow.show()
    # mywindow.show()
    topMenu.show()

    # mywindow.raise_()
    # mywindow.show()
    app.exec_()
