var app = angular.module('app');
app.controller('recognizeCtrl', function ($scope, $window) {
    this.eventDelegate = $({});
    this.options = {
        width: window.innerWidth,
        height: 300,
        backgroundColor: 'black',
        lineWidth: 5, //px
        undo: true,
        color: 'white'
    };
    this.version = 0;
    var me = this;
    this.recognizeImg = function () {
        var drawArea = document.querySelector('.recognize-draw-area');
        var url = drawArea.querySelector('#pwCanvasMain').toDataURL();
        handwrite.recognize(url, function (res) {
            document.querySelector('#res').innerHTML = "识别结果：" + res.recognizeResult;
            console.log(res.recognizeResult);
        }, function () {
            console.log(arguments);
        });
    };
    this.clear = function () {
        this.version = 0;
    };
});