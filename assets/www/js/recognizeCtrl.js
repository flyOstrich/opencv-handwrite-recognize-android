var app = angular.module('app');
app.controller('recognizeCtrl', function ($scope, $window) {
    var me = this;
    this.eventDelegate = $({});
    this.dataUrlList=[];
    this.eventDelegate.on('paintEnd',function(){
            console.log('paintEnd!');
            me.dataUrlList.push(document.querySelector('#pwCanvasTmp').toDataURL("image/bmp"));
            var res="";
            me.dataUrlList.forEach(function(item){
                 res+=item+"\n";
            });
            console.log(res);
    })
    this.options = {
        width: window.innerWidth,
        height: window.innerHeight*0.5,
        backgroundColor: 'white',
        lineWidth: 1, //px
        undo: true,
        color: 'black'
    };
    this.version = 0;

    this.recognizeImg = function () {
        var drawArea = document.querySelector('.recognize-draw-area');
        var url = drawArea.querySelector('#pwCanvasMain').toDataURL();
        handwrite.recognize(url, function (res) {
            var recognizeResult="";
            res.forEach(function(row){
               row.forEach(function(rowItem){
                   recognizeResult+=rowItem;
               });
               recognizeResult+="<br/>";
            });
            document.querySelector('#res').innerHTML = "识别结果：<br/>" + recognizeResult;
            console.log(res.recognizeResult);
        }, function () {
            console.log(arguments);
        });
    };
    this.clear = function () {
        this.version = 0;
    };
    this.undo=function(){
       if(this.version!=0){
          this.version--;
       }
    }
});