var express = require('express');
const db = require('../lib/mysql');
var router = express.Router();
router.post('/invest',function(req,res){
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            email = inputData.email; // 수정 필요
            db.query('select * from userInfo where email=?',[email],function(err,result){
                if(err){
                    console.log(err);
                }else{
                    res.send(result[0].point);
                }
            })
        })
    })
});
router.post('/decrease',function(req,res){
    console.log('decrease')
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            email = inputData.email; // 수정 필요
            console.log(email);
            db.query('select * from userInfo where email=?',[email],function(err,result){
                if(err){
                    console.log(err);
                }else{
                    console.log(result[0].point)
                    if(result[0].point==0){
                        res.send("No point");
                    }else{
                        db.query('update userInfo set point =? where email=?',[result[0].point-1,email],function(err,result2){
                            if(err){
                                console.log(err);
                            }else{
                                res.send("차감 완료");
                            }
                        })
                    }
                }
            })
        })
    })
});
router.post('/increase',function(req,res){
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            email = inputData.email; // 수정 필요
            addPoint = parseInt(inputData.point);
            console.log(email,addPoint)
            db.query('select * from userInfo where email=?',[email],function(err,result){
                if(err){
                    console.log(err);
                }else{
                    db.query('update userInfo set point =? where email=?',[result[0].point+addPoint,email],function(err,result2){
                        if(err){
                            console.log(err);
                        }else{
                            res.send("구매 완료");
                        }
                    })
                }
            })
        })
    })
});
module.exports = router; 