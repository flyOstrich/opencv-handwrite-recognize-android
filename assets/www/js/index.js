var app = angular.module('app', ['pw.canvas-painter']);
app.controller('mainCtrl', function () {
    this.width=window.innerWidth;
    this.height=window.innerHeight;
    this.eventDelegate=$({});
    this.options={
        width:28*10,
        height:28*10,
        backgroundColor: 'black',
        lineWidth: 15, //px
        color: 'white',
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
                      document.querySelector('#res').innerHTML="识别结果："+arguments[0].recognizeResult
                      console.log(arguments[0].recognizeResult);
                  }, function () {
                      console.log(arguments);
       })
    }
    this.refresh=function(){
       window.location.reload();
    }
});
app.run(function ($window) {
    $window.document.addEventListener('deviceready', function () {
        console.log('device ready!!!');
    })
});