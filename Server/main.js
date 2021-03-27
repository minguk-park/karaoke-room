const express = require('express');
const db = require('./lib/mysql');  
const timeId = require('./lib/test');
const app = express();
const bodyParser = require('body-parser');

var indexRouter = require('./routes/index');
var authRouter = require('./routes/auth');
var paymentRouter = require('./routes/payment');

app.use(bodyParser.urlencoded({
  extended: false
}));  
app.use('/',indexRouter);
app.use('/auth',authRouter);
app.use('/payment',paymentRouter);

app.use(function (req, res, next) {
    res.status(404).send('Sorry cant find that!');
  });

  
app.listen(80, function () {
    console.log('Example app listening on port 3000!')
  });