var express = require('express');
var router = express.Router();
const db = require('../lib/mysql');
const pool = require('../lib/promise');

console.log("index");
router.get('/',function(req,res){
    res.send('welcome');
});
router.get('/test',function(req,res){
    var html = `
    <!doctype html>
    <html>
    <head>
      <meta charset="utf-8">
    </head>
    <body>
      <form  action="/down" method="post">
        <p><input type="text" name ="mac" placeholder="mac"/></p> 
        <p><input type="submit"/></p>
      </form>
    </body>
    </html>
    `
    res.send(html);
});
router.post('/invest',function(req,res){
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            mac = inputData.mac;
            db.query('select * from devise where mac = ?',[mac],function(err,result){
                if(err){
                    console.log(err);
                }else{
                   res.send(result[0].changeValue);
                }
            });
        })
    }) 
})
router.post('/down',function(req,res){
    var post = req.body;
    console.log(req.body);
    var mac = post.mac;
    pool.query('select * from device where mac = ?',[mac]).then(devices =>{
        pool.query('select * from video order by id desc limit 1').then(videos =>{
            try{
                len = videos[0][0].id;
                sub = len-devices[0][0].saveNumber;
                console.log('보내야 할 곡 개수:', sub);
                res.send(sub);
                
            }catch(e){
                console.log(e);
            }
        })
    })
})
router.post('/sendVideo',function(req,res){
    var post = req.body;
    id = post.id;
    console.log('전송');
    pool.query('select * from video where id = ?',[len-id]).then(sendVideo=>{
        var dir = `C:\\Users\\82107\\Desktop\\fileEx\\${sendVideo[0][0].name}.mp4`;
        console.log("이름", sendVideo[0][0].name);
        // pool.query('update device set changeValue = ?, saveNumber = ? where mac =?',[true,len,devices[0][0].mac]).then(updateResult=>{
        //     try{
        //         res.send('Send Complete');
        //     }catch(e){
        //         console.log(e);
        //     }
        // });
        res.download(dir,function(err){
            if(err){
                console.log(err);
            }
        });
    })
});
router.post('/d', function(req,res){
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            mac = inputData.mac;
            pool.query('select * from device where mac =?', [mac]).then(result => {
                pool.query('select * from video order by id desc limit 1').then(videos => {
                    pool.query('update device set changeValue = ? where mac =?',[true,result[0][0].mac]).then(updateResult=>{
                        try{
                            console.log(videos, result)
                            len = videos[0][0].id;
                            sub = len - result[0][0].saveNumber;
                            for(var i =0;i<sub;i++){
                                console.log(i,"전송");//파일 전송하는 코드 작성
                            }
                        }catch(e){
                            console.log(e)
                        }
                    });
                });
            });
        })
    }) 
});
router.get('/download',function(req,res){
    var dir = 'C:\\Users\\82107\\Desktop\\Ocean.mp4'
    res.download(dir,function(err){
        if(err){
            console.log(err);
            res.json(err);
        }
    });
});

module.exports = router; 