var express = require('express');
var router = express.Router();

console.log("index");
router.post('/payPoint',function(req,res){
    res.send('pay');
});
router.post('/payCoin',function(req,res){
    res.send('pay');
});
module.exports = router; 