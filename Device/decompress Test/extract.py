import zipfile

fantasy_zip = zipfile.ZipFile('sing/tes.zip')
fantasy_zip.extractall('/home/pi/project/sing')
fantasy_zip.close()