const mysql = require('mysql2/promise');
const pool = mysql.createPool({
    host:'localhost',
    user:'root',
    password :'passwd',
    database:'serverdb'
})

module.exports = pool;