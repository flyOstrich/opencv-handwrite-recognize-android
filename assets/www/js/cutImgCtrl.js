var app = angular.module('app');
app.controller('cutImgCtrl', function ($scope, $window) {
     this.lastRecognizeInfo={};
     this.labelCharacterList=[];
     var me=this;
     function getCutImages(){
      handwrite.getCutImages(function (res) {
                   $scope.$apply(function(){
                       var recognizeImgList=[];
                       res.cut_images.forEach(function(imgUrl){
                           recognizeImgList.push({url:imgUrl+"?timeStamp="+Math.random(),originUrl:imgUrl});
                       });
                       me.lastRecognizeInfo.recognizeImgList=recognizeImgList;
                       me.lastRecognizeInfo.lastRecognizeImg=res.lastRecognizeImg+"?timeStamp="+Math.random();
                   });
                }, function () {
                    console.log(arguments);
          });
     }
     getCutImages();

     handwrite.getLabelCharacterMap(function (res) {
                 $scope.$apply(function () {
                     me.labelCharacterList = res.labelCharacterList;
                 })
             }, function () {
                 console.error(arguments);
     });
     this.addToTrainSet=function(img){
        if(!img.selectedLabelCharacterMap)alert('请选择该图片代表的文字！');
        handwrite.setTrainImage(img.selectedLabelCharacterMap.label, null,img.originUrl,function (imageList) {
            $scope.$apply(function () {
                getCutImages();
            })
        }, function () {
            console.error(arguments);
        })
     }
});