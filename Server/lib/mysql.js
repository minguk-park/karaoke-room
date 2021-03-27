const mysql = require('mysql');
const db = mysql.createConnection({
    host:'localhost',
    user:'root',
    password :'passwd',
    database:'serverdb'
})
db.connect();
module.exports = db;