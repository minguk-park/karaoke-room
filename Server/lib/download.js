module.exports = {
    download:function(dir,res){
        res.download(dir,function(err){
            if(err){
                console.log(err);
                res.json(err);
            }
        });
    }
}