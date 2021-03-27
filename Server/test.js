    const pool = require('./lib/promise');

pool.query('select * from video').then(result=>{
    pool.query('select * from device').then(div =>{
        try{
            console.log(result[0][0]);
            console.log(div[0]);
        }catch(e){
            console.log(e)
        }
    })
})