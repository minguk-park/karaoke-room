var express = require('express');
const db = require('../lib/mysql');
var router = express.Router();
router.post('/search', function(req,res){
    console.log("search")
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            type = inputData.type; // 수정 필요
            text = inputData.text;
            console.log(type,text);
            if (type === "제목"){
                db.query('select * from video where UPPER(name) like ?', [`%${text}%`],function(err,result){
                    if(err){
                        console.log(err);
                    }else{
                        sendResult = [];
                        temp=[];
                        var index = 0
                        while(index<=result.length){
                            if(index != 0 & (index % 20==0 || index == result.length)){                                 
                                sendResult.push(temp);
                                temp=[];
                            } 
                            temp.push(result[index]);
                            index++;
                        }
                        console.log(sendResult)
                        res.send(sendResult);
                    }
                })
            }else if (type === "가수"){
                db.query('select * from video where singer like ?', [`%${text}%`],function(err,result){
                    if(err){
                        console.log(err);
                    }else{
                        sendResult = [];
                        temp=[];
                        var index = 0
                        while(index<=result.length){
                            if(index != 0 & (index % 20==0 || index == result.length)){                                 
                                sendResult.push(temp);
                                temp=[];
                            } 
                            temp.push(result[index]);
                            index++;
                        }
                        res.send(sendResult);
                    }
                })
            }else{
                res.send('형식이 잘못 되었습니다.')
            }
        })
    })
});
module.exports = router; 