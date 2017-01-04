var app = angular.module('app');
app.controller('trainCtrl', function ($scope) {
    this.imageList=[];
    var me=this;
    handwrite.getTrainImageList(function (imageList) {
        $scope.$apply(function () {
            me.imageList = imageList;
        })
    }, function () {
        console.error(arguments);
    });
    this.getImgUrl = function (imgPath) {
        return imgPath + "?timestamp=" + new Date().getTime();
    };

    this.svmPath=null;
    this.startTrain=function () {
        handwrite.trainFromTrainImages(function (res) {
            $scope.$apply(function () {
                me.svmPath = res.svmPath;
            })
        }, function () {
            console.error(arguments);
        });
    }
});