var app = angular.module('app', ['pw.canvas-painter']);
app.controller('mainCtrl', function () {
    this.width=window.innerWidth;
    this.height=window.innerHeight;
    this.eventDelegate=$({});
    this.options={
        width:this.width,
        height:this.height,
        color:"black"
    };
    this.version = 0;
    this.timer=null;
    this.startTimer=function(){
        return setTimeout(function(){
            var url = document.querySelector('#pwCanvasMain').toDataURL();
            handwrite.recognize(url, function () {
                console.log(arguments);
            }, function () {
                console.log(arguments);
            })
        },1000);
    };
    var me=this;
    this.eventDelegate.on('paintStart',function(){
        console.log('paintStart');
        clearTimeout(me.timer);
    });
    this.eventDelegate.on('paintEnd',function(){
        console.log('paintEnd!');
        me.timer=me.startTimer();
    })
    this.reg=function(){
       var url = document.querySelector('#pwCanvasMain').toDataURL();
                  handwrite.recognize(url, function () {
                      console.log(arguments);
                  }, function () {
                      console.log(arguments);
       })
    }
});
app.run(function ($window) {
    $window.document.addEventListener('deviceready', function () {
        console.log('device ready!!!');
    })
});