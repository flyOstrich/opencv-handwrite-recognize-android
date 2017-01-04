var app = angular.module('app');
app.controller('drawCtrl', function ($scope, $window) {
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
    this.trainVal = 0;
    this.imageList = [];
    var me = this;
    $window.document.addEventListener('deviceready', function () {
        handwrite.getTrainImageList(function (imageList) {
            $scope.$apply(function () {
                me.imageList = imageList;
            })
        }, function () {
            console.error(arguments);
        });
    });
    this.saveImg = function () {
        var me = this;
        var url = document.querySelector('#pwCanvasMain').toDataURL();
        handwrite.setTrainImage(this.trainVal, url, function (imageList) {
            $scope.$apply(function () {
                me.imageList = imageList;
            })
        }, function () {
            console.error(arguments);
        })
    };
    this.clear = function () {
        this.version = 0;
    };
    this.deleteTrainImage = function (imgPath) {
        var me = this;
        handwrite.deleteTrainImage(imgPath, function (imageList) {
            $scope.$apply(function () {
                me.imageList = imageList;
            })
        }, function () {
            console.error(arguments);
        })
    }
    this.getImgUrl = function (imgPath) {
        return imgPath + "?timestamp=" + new Date().getTime();
    }
});