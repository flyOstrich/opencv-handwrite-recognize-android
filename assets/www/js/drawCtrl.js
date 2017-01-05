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
    this.labelCharacterList = [];
    this.selectedLabelCharacterMap = {};
    var me = this;
    $window.document.addEventListener('deviceready', function () {
        handwrite.getTrainImageList(function (imageList) {
            $scope.$apply(function () {
                me.imageList = imageList;
            })
        }, function () {
            console.error(arguments);
        });
        handwrite.getLabelCharacterMap(function (res) {
            $scope.$apply(function () {
                me.labelCharacterList = res.labelCharacterList;
                me.selectedLabelCharacterMap = res.labelCharacterList[0];
            })
        }, function () {
            console.error(arguments);
        });
    });
    this.saveImg = function () {
        var me = this;
        var url = document.querySelector('#pwCanvasMain').toDataURL();
        handwrite.setTrainImage(this.selectedLabelCharacterMap.label, url, function (imageList) {
            $scope.$apply(function () {
                me.imageList =  me.addTimeStamp(imageList);
                me.clear();
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
        imgPath=imgPath.substr(0,imgPath.indexOf('?'));
        handwrite.deleteTrainImage(imgPath, function (imageList) {
            $scope.$apply(function () {
                me.imageList =   me.addTimeStamp(imageList);;
            })
        }, function () {
            console.error(arguments);
        })
    };
    this.addTimeStamp = function (imageList) {
        var list=[];
        imageList.forEach(function (image) {
            var timeStampImage=image + "?timestamp=" + new Date().getTime();
            list.push(timeStampImage);
        });
        return list;
    }
});