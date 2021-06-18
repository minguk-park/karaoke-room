var express = require('express');
var router = express.Router();
const db = require('../lib/mysql');
const pool = require('../lib/promise');
const zip = require('node-zip')();
const fs = require('fs');

router.get('/',function(req,res){
    res.send('welcome');
});
router.get('/d',function(req,res){
    console.log('down')
    res.download('videos.zip',async function(err){
        await fs.unlinkSync('videos.zip');
        console.log('삭제 완료');
    });
})
router.post('/invest',function(req,res){ //mac을 통해 업데이트가 필요한지 확인
    var post = req.body;
    var mac = post.mac;
    console.log(mac);
    db.query('select * from device where mac = ?',[mac],function(err,result){
        if(err){
            console.log(err);
        }else{
            console.log(result[0]);
            res.send(result[0]);
        }
    });
})
router.post('/download', function(req,res){ //라즈베리한테 추가된 곡 개수를 보내는 url
    var post = req.body;
    var mac = post.mac;
    console.log(mac)
    pool.query('select * from device where mac = ?',[mac]).then(devices =>{
        pool.query('select * from video order by id desc limit 1').then(videos =>{
            try{
                len = videos[0][0].id;
                sub = len-devices[0][0].saveNumber;
                console.log('보내야 할 곡 개수:', sub);
                pool.query(`select * from video where id >?`,len-sub).then(sendV => {
                    for(var i =0;i<sub;i++){
                        var dir = `C:\\Users\\82107\\Desktop\\fileEx\\${sendV[0][i].name}.mp4`;
                        zip.file(`${sendV[0][i].name}.mp4`,fs.readFileSync(dir));
                        console.log(dir);
                    }
                    var data = zip.generate({type:"uint8array"});
                    console.log("zip 생성")
                    fs.writeFile('videos.zip',data,'binary',async function(err){
                        if(err){
                            console.log("에러", err);
                            res.send(err);
                        }
                    })
                    pool.query('update device set saveNumber =?,changeValue=? where mac =?',[len,1,mac]).then(result =>{
                        try{
                            console.log("수정 완료");
                        }catch(e){
                            console.log(e)
                        }
                    })
                    res.send('create success');
                })
            }catch(e){
                console.log(e);
            }
        })
    })
})

module.exports = router; 