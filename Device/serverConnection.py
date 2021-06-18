import requests
import time
import json
import zipfile
import os
import schedule
import threading

class server_download:
    investURL = "http://175.118.28.138/invest"
    zipURL = "http://175.118.28.138/download"
    downloadURL = "http://175.118.28.138/d"
    zipName = "sing/singZip.zip"
    singLocation = '/home/pi/project/sing'
    
    def download(self, url, file_name):
        with open(file_name, "wb") as file:   # open in binary mode
            response = requests.get(url) # get request
            file.write(response.content)
            
    def invest_download(self):
        res = requests.post(self.investURL,data = {'mac':2})
        print(res.text)
        dict = json.loads(res.text)
        print(dict['changeValue'])
        
        return dict['changeValue']
    
    def mkZip(self):
        requests.post(self.zipURL,data = {'mac':2})
        print("make zip")
    
    def download_file(self):
        self.download(self.downloadURL, self.zipName)
        print("down load")
        
    def zipExtract(self):
        fantasy_zip = zipfile.ZipFile(self.zipName)
        fantasy_zip.extractall(self.singLocation)
        fantasy_zip.close()
        
    def removeFile(self):
        if os.path.isfile(self.zipName):
            os.remove(self.zipName)

def downloadSchedul():
    serverDownload = server_download()
    bool = serverDownload.invest_download()
    if bool == 0:
        print("bool 0")
        serverDownload.mkZip()
        time.sleep(7)
        serverDownload.download_file()
        serverDownload.zipExtract()
        serverDownload.removeFile()
    else:
        print("bool 1")
        
def scheduleDownload():
    schedule.every().day.at("05:00").do(downloadSchedul)
    while True:
        schedule.run_pending()
        time.sleep(1)

if __name__ == '__main__':
    #scheduleDownload()
    t = threading.Thread(target=scheduleDownload)
    t.start()
