const fs = require('fs');
const db = require('./mysql');

const timeId = setInterval(function(){
    var dir = 'C:\\Users\\82107\\Desktop\\fileEx';// 음원 하드
    fs.readdir(dir,function(err,list){
        if(err){
            console.log(err);
        }else{
            var len = list.length;
            db.query('select * from device',function(err1,result){
                console.log(result);
                if(err1){
                    console.log(err1);
                }else{
                    for(var i =0; i<result.length;i++){
                        if(len !== result[i].saveNumber){
                            console.log(result[i].mac)
                            db.query('update device set changeValue = ? where mac =?',[false,result[i].mac],function(err2,updateResult){
                                if(err2){
                                    console.log(err2);
                                }else{
                                    console.log(updateResult,'변경완료')
                                }
                            });
                        }
                    }
                }
            });
        }
    });
},600000);

// setTimeout(function(){
//     clearInterval(timeId);
//     console.log('stop')
// }, 30000);

module.exports = timeId;