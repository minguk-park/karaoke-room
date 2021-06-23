import sys
from PyQt5.QtGui import *
from PyQt5 import uic, QtMultimedia  # UI불러오기
import re  # 문자열 패턴
import time
import requests  # 노래 리스트 서버로 부터 전달
import json
from collections import OrderedDict
from PyQt5.QtMultimedia import QMediaPlayer, QMediaContent
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5 import uic  # UI불러오기

front_class = uic.loadUiType("mainTest.ui")[0]  # 메뉴창 UI
front_class2 = uic.loadUiType("top.ui")[0]  # 메뉴창 UI
front_class3 = uic.loadUiType("volumebar.ui")[0]  # 메뉴창 UI
# = uic.loadUiType("back.ui")[0]  # 배경 영상 UI
current_row = None  # 행 위치 파악 변수
current_page = 0  # 페이지 위치 파악 변수
reserve_list = []  # 에약 리스트 저장
# message = None  # 입력명령
run_video = 0
nickname = None
url = 'http://175.118.28.138/music/search'  # 노래 검색 서버 주소


class TopMenu(QWidget, front_class2):
    def __init__(self):
        super().__init__()
        self.setupUi(self)
        self.setWindowFlag(Qt.FramelessWindowHint)
        self.move(0, 5)


class soundMenu(QWidget, front_class3):
    def __init__(self):
        super().__init__()
        self.setupUi(self)
        self.setWindowFlag(Qt.FramelessWindowHint)
        self.move(30, 100)


# 메인 메뉴 클래스
class MyWindow(QDialog, front_class):
    def __init__(self, videoObj, keySig, reserve_bar, sound_bar):
        super().__init__()
        self.setupUi(self)
        total_song = []
        self.volume_value = 10
        self.tempo_value = 10
        self.peach_value = 10
        self.mainHeight = 0
        self.tableHeight = 0
        # 기본설정
        lo = self.frameGeometry()
        ctr = QDesktopWidget().availableGeometry().center()
        ctr.setY(ctr.y() - 50)
        lo.moveCenter(ctr)  # 메뉴위치 화면 중앙
        self.move(lo.topLeft())

        self.songList.hide()
        self.searchEdit.hide()
        self.typeEdit.hide()
        self.textEdit.hide()
        self.pageEdit.hide()
        # self.volumeEdit.hide()
        # self.volumeBar.hide()
        # self.volumeNumEdit.hide()
        self.raise_()
        # 위젯 투명도 설정
        # self.setStyleSheet("background:transparent")
        self.setWindowFlag(Qt.FramelessWindowHint)
        # self.setAttribute(Qt.WA_TranslucentBackground)

        # 서버로 부터 파일 가져와 노래 리스트 저장
        file_data = OrderedDict()
        file_data["type"] = "제목"
        file_data["text"] = ""
        res = requests.post(url, data=json.dumps(file_data))
        for file in res.json():
            for val in file:
                total_song.append([val['id'], val['name'], val['singer']])

        # 타이머 객체 생성
        self.timerVar = QTimer()
        self.timerVar.timeout.connect(lambda: self.timeout(sound_bar))
        # 비디오 객체 생성
        self.videoSig = videoObj
        # 키보드 스레드 생성
        self.keySignal = keySig
        # 이벤트 설정
        self.keySignal.signal11.connect(lambda: self.signal11_emitted(total_song))
        self.keySignal.signal12.connect(self.signal12_emitted)
        self.keySignal.signal13.connect(self.signal13_emitted)
        self.keySignal.signal14.connect(self.signal14_emitted)
        self.keySignal.signal15.connect(self.signal15_emitted)
        self.keySignal.signal16.connect(self.signal16_emitted)
        self.keySignal.signal17.connect(self.signal17_emitted)
        self.keySignal.signal18.connect(lambda: self.signal18_emitted(reserve_bar))
        self.keySignal.signal19.connect(lambda: self.signal19_emitted(reserve_bar))
        self.keySignal.signal20.connect(self.signal20_emitted)
        self.keySignal.signal21.connect(lambda: self.signal21_emitted(sound_bar))
        self.keySignal.signal22.connect(lambda: self.signal22_emitted(sound_bar))
        self.keySignal.signal23.connect(lambda: self.signal23_emitted(sound_bar))
        self.keySignal.signal24.connect(lambda: self.signal24_emitted(reserve_bar))
        self.keySignal.signal25.connect(lambda: self.signal25_emitted(reserve_bar))
        self.keySignal.signal26.connect(lambda: self.signal26_emitted(sound_bar))
        self.keySignal.signal27.connect(lambda: self.signal27_emitted(sound_bar))
        self.keySignal.signal28.connect(lambda: self.signal28_emitted(sound_bar))
        self.keySignal.start()

    def timeout(self, sound_bar):  # 위젯 삭제
        self.timerVar.stop()
        sound_bar.hide()

    @pyqtSlot()  # 노래리스트 불러오기 (11)
    def signal11_emitted(self, songAll):
        global current_row
        global current_page
        global run_video
        song_list = songAll

        # 화면출력
        self.show()
        self.songList.show()
        self.typeEdit.show()
        self.searchEdit.show()
        self.searchEdit.clear()
        self.textEdit.show()
        self.pageEdit.show()
        # self.songList.resize(720,480)
        # 기본설정
        self.typeEdit.setText('제목')
        self.textEdit.setText('검색 :')
        self.textEdit.setAlignment(Qt.AlignHCenter)  # textEdit 가운데정렬
        self.songList.setSelectionBehavior(QAbstractItemView.SelectRows)  # 행단위 선택
        self.searchEdit.textChanged.connect(self.line_editTextFunction)  # 검색창 연결
        self.typeEdit.textChanged.connect(self.line_editTextFunction)  # 제목/가수별 연결
        column_headers = ['예약번호', '노래제목', '가수']
        self.songList.setHorizontalHeaderLabels(column_headers)
        stylesheet = "QHeaderView::section{Background-color:rgb(126,126,126)}"  # 헤더 스타일 시트
        self.songList.horizontalHeader().setStyleSheet(stylesheet)
        self.songList.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.songList.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)

        # 위젯 크기 저장
        if self.mainHeight == 0 and self.tableHeight == 0:
            self.mainHeight = self.size()
            self.tableHeight = self.songList.size()

        if run_video == 1:
            # self.mainHeight.setHeight(300)
            # self.resize(self.mainHeight)
            self.resize(1000, 300)
            self.songList.resize(961, 150)
            # self.tableHeight.setHeight(150)
        else:
            self.resize(self.mainHeight)
            self.songList.resize(self.tableHeight)

        # 노래 리스트 출력
        for row, val in enumerate(song_list):
            col = 0
            for a in val:
                item = QTableWidgetItem(str(a))  # 아이템 설정
                item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)  # 아이템 가운데 정렬
                self.songList.setItem(row, col, item)
                col = col + 1
                if col == 3:
                    break
            if row == 19:
                break

        # 페이지수 체크
        if len(song_list) % 20 != 0:
            max_page = len(song_list) // 20 + 1
        else:
            max_page = len(song_list) // 20

        current_page = 1  # 현재 페이지 설정
        self.pageEdit.setText("< {0} / {1} >".format(current_page, max_page))  # 페이지 표시

        # 리스트 첫줄 선택표시
        # if current_row is None:
        current_row = 0
        item = self.songList.item(current_row, 1)
        self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
        self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
        self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
        self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

    # 노래검색
    def line_editTextFunction(self):
        global current_row
        global current_page
        temp_title = ['id', 'name', 'singer']  # json 추출 목록

        search = self.searchEdit.text()
        division = self.typeEdit.text()

        # 노래 리스트 출력
        file_data = OrderedDict()
        file_data["type"] = division
        file_data["text"] = search
        res = requests.post(url, data=json.dumps(file_data))  # 서버로 부터 리스트 불러오기
        # 검새결과 0일시
        self.songList.clearContents()  # 테이블초기화
        if len(res.json()) == 0:
            return 0

        for row, dicts in enumerate((res.json())[0]):
            col = 0
            for key, val in dicts.items():
                if temp_title[col] == key:
                    item = QTableWidgetItem(str(val))
                    item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                    self.songList.setItem(row, col, item)
                    col = col + 1
            if row == 19:
                break

        current_page = 1
        max_page = len(res.json())
        self.pageEdit.setText("< {0} / {1} >".format(current_page, max_page))  # 페이지 표시

        # 리스트 정렬
        if division == '가수':
            self.songList.sortByColumn(2, Qt.AscendingOrder)  # 가수 이름순 정렬
        elif division == '제목':
            self.songList.sortByColumn(1, Qt.AscendingOrder)  # 제목 이름순 정렬

        # 리스트 첫줄 선택표시
        current_row = 0
        item = self.songList.item(current_row, 1)
        self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
        self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
        self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
        self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

    @pyqtSlot(str)  # 취소 (12)
    def signal12_emitted(self, arg):
        # 노래리스트 메뉴 초기화
        global current_row
        # mywindow.hide()
        if arg == 'menu':
            self.searchEdit.clear()
            current_row = None  # 메뉴 초기화
            self.songList.hide()
            self.searchEdit.hide()
            self.typeEdit.hide()
            self.textEdit.hide()
            self.pageEdit.hide()
            self.hide()
        elif arg == 'video':
            self.videoSig.check_song('12', None)

    @pyqtSlot()  # 아래 화살표 (13)
    def signal13_emitted(self):
        # 리스트 선택 행 아래이동
        global current_row

        try:
            # 페이지 넘기기
            if current_row == 19:
                self.songList.clearContents()  # 테이블초기화
                self.signal15_emitted()
            # 홀수번째 줄
            elif 0 <= current_row < 19 and current_row % 2 == 0:
                item = self.songList.item(current_row + 1, 1)
                self.songList.scrollToItem(item)
                self.songList.item(current_row + 1, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row + 1, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row + 1, 2).setBackground(QColor(255, 255, 210))

                self.songList.item(current_row, 0).setBackground(QColor(255, 255, 255))
                self.songList.item(current_row, 1).setBackground(QColor(255, 255, 255))
                self.songList.item(current_row, 2).setBackground(QColor(255, 255, 255))
                current_row = current_row + 1
            # 짝수번째 줄
            elif 0 <= current_row < 19 and current_row % 2 == 1:
                item = self.songList.item(current_row + 1, 1)
                self.songList.scrollToItem(item)
                self.songList.item(current_row + 1, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row + 1, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row + 1, 2).setBackground(QColor(255, 255, 210))

                self.songList.item(current_row, 0).setBackground(QColor(255, 162, 168))
                self.songList.item(current_row, 1).setBackground(QColor(255, 162, 168))
                self.songList.item(current_row, 2).setBackground(QColor(255, 162, 168))
                current_row = current_row + 1

        except Exception as ex:
            print("아래화살표(13번) error", ex)

    @pyqtSlot()  # 위 화살표 (14)
    def signal14_emitted(self):
        # 리스트 선택 행 위이동
        global current_row
        global current_page

        try:
            # 페이지 넘기기
            if current_row == 0 and current_page != 1:
                self.songList.clearContents()  # 테이블초기화
                self.signal16_emitted()
            # 짝수번째 줄
            if 0 < current_row < 20 and current_row % 2 == 1:
                self.songList.item(current_row, 0).setBackground(QColor(255, 162, 168))
                self.songList.item(current_row, 1).setBackground(QColor(255, 162, 168))
                self.songList.item(current_row, 2).setBackground(QColor(255, 162, 168))

                item = self.songList.item(current_row - 1, 1)
                self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
                self.songList.item(current_row - 1, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row - 1, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row - 1, 2).setBackground(QColor(255, 255, 210))
                current_row = current_row - 1
            # 홀수번째 줄
            elif 0 < current_row < 20 and current_row % 2 == 0:
                self.songList.item(current_row, 0).setBackground(QColor(255, 255, 255))
                self.songList.item(current_row, 1).setBackground(QColor(255, 255, 255))
                self.songList.item(current_row, 2).setBackground(QColor(255, 255, 255))

                item = self.songList.item(current_row - 1, 1)
                self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
                self.songList.item(current_row - 1, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row - 1, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row - 1, 2).setBackground(QColor(255, 255, 210))
                current_row = current_row - 1

        except Exception as ex:
            print("위화살표(14번) error", ex)

    @pyqtSlot()  # 오른쪽 화살표(15번)
    def signal15_emitted(self):
        # 노래리스트 다음 페이지로
        global current_page
        global current_row
        temp_title = ['id', 'name', 'singer']  # json 추출 목록

        try:
            search = self.searchEdit.text()
            division = self.typeEdit.text()
            file_data = OrderedDict()
            file_data["type"] = division
            file_data["text"] = search
            res = requests.post(url, data=json.dumps(file_data))  # 서버로 부터 리스트 불러오기
            max_page = len(res.json())  # 최대페이지수

            if current_page < max_page:
                self.songList.clearContents()  # 테이블초기화
                # 노래 리스트 출력
                for row, dicts in enumerate((res.json())[current_page]):
                    col = 0
                    for key, val in dicts.items():
                        if temp_title[col] == key:
                            item = QTableWidgetItem(str(val))
                            item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                            self.songList.setItem(row, col, item)
                            col = col + 1
                    if row == 20:
                        break
                current_page = current_page + 1
                self.pageEdit.setText("< {0} / {1} >".format(current_page, max_page))  # 페이지 표시

                if division == '가수별':
                    self.songList.sortByColumn(2, Qt.AscendingOrder)  # 가수 이름순 정렬
                elif division == '제목별':
                    self.songList.sortByColumn(1, Qt.AscendingOrder)  # 제목 이름순 정렬

                # 리스트 첫줄 선택표시
                current_row = 0
                item = self.songList.item(current_row, 1)
                self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
                self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

        except Exception as ex:
            print("오른쪽화살표(15번) error", ex)

    @pyqtSlot()  # 왼쪽 화살표(16번)
    def signal16_emitted(self):
        # 노래리스트 이전 페이지로
        global current_page
        global current_row
        temp_title = ['id', 'name', 'singer']  # json 추출 목록

        try:
            search = self.searchEdit.text()
            division = self.typeEdit.text()
            file_data = OrderedDict()
            file_data["type"] = division
            file_data["text"] = search
            res = requests.post(url, data=json.dumps(file_data))  # 서버로 부터 리스트 불러오기
            max_page = len(res.json())  # 최대페이지수

            if 1 < current_page:
                self.songList.clearContents()  # 테이블초기화
                # 노래 리스트 출력
                for row, dicts in enumerate((res.json())[current_page - 2]):
                    col = 0
                    for key, val in dicts.items():
                        if temp_title[col] == key:
                            item = QTableWidgetItem(str(val))
                            item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                            self.songList.setItem(row, col, item)
                            col = col + 1
                    if row == 20:
                        break
                current_page = current_page - 1
                self.pageEdit.setText("< {0} / {1} >".format(current_page, max_page))  # 페이지 표시

                if division == '가수별':
                    self.songList.sortByColumn(2, Qt.AscendingOrder)  # 가수 이름순 정렬
                elif division == '제목별':
                    self.songList.sortByColumn(1, Qt.AscendingOrder)  # 제목 이름순 정렬

                # 리스트 첫줄 선택표시
                current_row = 0
                item = self.songList.item(current_row, 1)
                self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
                self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
                self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

        except Exception as ex:
            print("왼쪽화살표(16번) error", ex)

    @pyqtSlot(str)  # 노래 문자열 입력(17번)
    def signal17_emitted(self, arg):
        # 노래 검색
        self.searchEdit.clear
        output = arg.split(',')
        self.searchEdit.setText(output[1])
        self.typeEdit.setText(output[2])

    @pyqtSlot()  # 노래 예약하기(18번)
    def signal18_emitted(self, reserve_bar):
        # 노래 예약하기
        global current_row
        global reserve_list
        temp = []
        col = 0

        # 선택행 리스트로 묶음
        while col < 3:
            temp.append(self.songList.item(current_row, col).text())
            col = col + 1
        temp.append(nickname)  # 유저 아이디 저장

        # 중복예약 검사
        col = 0
        for val in reserve_list:  # 예약리스트 조회
            if val == temp:
                temp.clear()
                break
            col = col + 1

        # 중복이 없을경우
        if len(temp) != 0:
            reserve_list.append(temp)  # 예약리스트에 추가
            # 예약 Bar 반영
            if reserve_bar.reserveEdit.text() == "":
                reserve_bar.reserveEdit.setText(temp[0] + "\t")
            else:
                reserve_bar.reserveEdit.setText(reserve_bar.reserveEdit.text() + temp[0] + "\t")

    @pyqtSlot()  # 노래 시작하기(19번)
    def signal19_emitted(self, reserve_bar):
        # 노래 예약하기
        global current_row

        if len(reserve_bar.reserveEdit.text()) != 0:
            temp = (reserve_bar.reserveEdit.text()).split('\t')

        if current_row is None and len(temp) != 0:  # 메뉴 창이 없는경우
            self.videoSig.check_list('10')
        elif current_row is not None:
            song_number = self.songList.item(current_row, 0)
            self.videoSig.check_song('11', song_number.text())
            # 메뉴화면 제거
            self.searchEdit.clear()
            current_row = None
            self.hide()
            self.songList.hide()
            self.searchEdit.hide()
            self.typeEdit.hide()
            self.textEdit.hide()
            self.pageEdit.hide()

    @pyqtSlot()  # 예약목록 부르기(20번)
    def signal20_emitted(self):
        # 노래 예약하기
        global current_row
        global reserve_list
        global run_video

        self.show()
        self.pageEdit.hide()
        self.textEdit.show()
        self.typeEdit.show()
        self.searchEdit.show()
        self.searchEdit.clear()
        self.textEdit.setText('예약목록')
        self.textEdit.setAlignment(Qt.AlignHCenter)  # textEdit 가운데정렬
        self.songList.show()
        self.songList.clearContents()

        # 위젯 크기 저장
        if self.mainHeight == 0 and self.tableHeight == 0:
            self.mainHeight = self.size()
            self.tableHeight = self.songList.size()

        if run_video == 1:
            # self.mainHeight.setHeight(300)
            # self.resize(self.mainHeight)
            self.resize(1000, 300)
            self.songList.resize(961, 150)
            # self.tableHeight.setHeight(150)
        else:
            self.resize(self.mainHeight)
            self.songList.resize(self.tableHeight)

        try:
            for row, val in enumerate(reserve_list):  # 예약리스트 출력
                col = 0
                for a in val:
                    item = QTableWidgetItem(a)
                    item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                    self.songList.setItem(row, col, item)
                    col = col + 1
                    if col == 3:
                        break
                if row == 19:
                    break

            # if current_row is None:  # 첫줄 표시
            current_row = 0
            item = self.songList.item(current_row, 1)
            self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
            self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
            self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
            self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

        except Exception as ex:
            print("예약목록(20번) error", ex)

    @pyqtSlot()  # 볼륨조절(21번)
    def signal21_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Volume")
        self.timerVar.start(3000)

        if self.volume_value < 20:
            self.volume_value = self.volume_value + 1
            sound_bar.volumeBar.setValue(self.volume_value)
            sound_bar.volumeNumEdit.setText(str(self.volume_value))

    @pyqtSlot()  # 음정조절(22번)
    def signal22_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Peach")
        self.timerVar.start(3000)

        if self.peach_value < 20:
            self.peach_value = self.peach_value + 1
            sound_bar.volumeBar.setValue(self.peach_value)
            sound_bar.volumeNumEdit.setText(str(self.peach_value))

    @pyqtSlot()  # 탬포조절(23번)
    def signal23_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Tempo")
        self.timerVar.start(3000)

        if self.tempo_value < 20:
            self.tempo_value = self.tempo_value + 1
            sound_bar.volumeBar.setValue(self.tempo_value)
            sound_bar.volumeNumEdit.setText(str(self.tempo_value))

    @pyqtSlot()  # 예약 취소(24번)
    def signal24_emitted(self, reserve_bar):
        global reserve_list
        global current_row

        col = 0
        reserve_bar.reserveEdit.clear()  # 상단바 초기화
        del_song = self.songList.item(current_row, 0)  # 선택행 삭제
        while col < 3:
            self.songList.takeItem(current_row, col)
            col = col + 1

        col = 0
        for val in reserve_list:  # 예약 리스트 수정
            if del_song.text() in val:
                del reserve_list[col]
            col = col + 1

        for val in reserve_list:
            reserve_bar.reserveEdit.setText(reserve_bar.reserveEdit.text() + val[0] + "\t")

        self.songList.clearContents()  # 예약리스트 재출력
        for row, val in enumerate(reserve_list):
            col = 0
            for a in val:
                item = QTableWidgetItem(a)
                item.setTextAlignment(Qt.AlignVCenter | Qt.AlignCenter)
                self.songList.setItem(row, col, item)
                col = col + 1
                if col == 3:
                    break
            if row == 19:
                break

        current_row = 0  # 첫행 표시
        item = self.songList.item(current_row, 1)
        if item is not None:
            self.songList.scrollToItem(item)  # QAbstractItemView.PositionAtCenter)
            self.songList.item(current_row, 0).setBackground(QColor(255, 255, 210))
            self.songList.item(current_row, 1).setBackground(QColor(255, 255, 210))
            self.songList.item(current_row, 2).setBackground(QColor(255, 255, 210))

    @pyqtSlot()  # 우선 예약(25번)
    def signal25_emitted(self, reserve_bar):
        global reserve_list
        global current_row
        temp = []
        col = 0
        while col < 3:
            temp.append(self.songList.item(current_row, col).text())
            col = col + 1
        temp.append(nickname)

        # 중복예약 검사
        col = 0
        for val in reserve_list:  # 예약리스트 조회
            if val == temp:
                temp.clear()
                break
            col = col + 1

        # 중복이 없을경우
        if len(temp) != 0:
            reserve_bar.reserveEdit.clear()  # 상단바 초기화
            reserve_list.insert(0, temp)
            # 예약 Bar 반영
            for val in reserve_list:
                reserve_bar.reserveEdit.setText(reserve_bar.reserveEdit.text() + val[0] + "\t")

    @pyqtSlot()  # 볼륨 내리기(26번)
    def signal26_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Volume")
        self.timerVar.start(3000)

        if self.volume_value > 0:
            self.volume_value = self.volume_value - 1
            sound_bar.volumeBar.setValue(self.volume_value)
            sound_bar.volumeNumEdit.setText(str(self.volume_value))

    @pyqtSlot()  # 음정 내리기(27번)
    def signal27_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Peach")
        self.timerVar.start(3000)

        if self.peach_value > 0:
            self.peach_value = self.peach_value - 1
            sound_bar.volumeBar.setValue(self.peach_value)
            sound_bar.volumeNumEdit.setText(str(self.peach_value))

    @pyqtSlot()  # 탬포 내리기(28번)
    def signal28_emitted(self, sound_bar):
        sound_bar.show()
        sound_bar.volumeEdit.setText("Tempo")
        self.timerVar.start(3000)

        if self.tempo_value > 0:
            self.tempo_value = self.tempo_value - 1
            sound_bar.volumeBar.setValue(self.tempo_value)
            sound_bar.volumeNumEdit.setText(str(self.tempo_value))

