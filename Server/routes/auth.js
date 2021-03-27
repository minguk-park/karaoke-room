var express = require('express');
var router = express.Router();
const db = require('../lib/mysql');

router.get('/',function(req,res){
    res.send('auth');
});
router.post('/register',function(req,res){
   req.on('data',function(data){
       inputData = JSON.parse(data);
       req.on('end',function(){
           name=inputData.name; // 변수 확인 후 수정 필요
           email = inputData.email; // 수정 필요
           db.query('insert into userInfo (email,username,point) values(?,?,?)',[email,name,0],function(err,result){
            if(err){
                if(err.code === "ER_DUP_ENTRY"){ //id 중복
                    res.write("이미 가입되어있는 email입니다."); 
                    res.end();
                }else{
                    console.log(err);
                    res.send('회원가입 도중 오류가 발생했습니다. 다시 시도해주세요');
                }
            }else{
                res.send('회원가입이 완료되었습니다.');
            }
           })
       })
   })
});

router.post('/exprire',function(req,res){
    req.on('data',function(data){
        inputData = JSON.parse(data);
        req.on('end',function(){
            email = inputData.email;
            db.query('delete from userInfo where email=?'[email],function(err,result){
                if(err){
                    console.log(err);
                    res.send('탈퇴 도중 오류가 발생했습니다. 다시 시도해주세요.')
                }else{
                    res.send('탈퇴가 완료되었습니다.');
                }
            })
        })
    })
});
module.exports = router;