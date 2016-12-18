var app=angular.module('app',['pw.canvas-painter']);
app.controller('mainCtrl',function(){
   this.version=0;
})
app.run(function($window){
    var doc=$window.document;
    $window.document.addEventListener('deviceready',function(){
        doc.querySelector('#pwCanvasTmp').onclick=function(){
         var url=  doc.querySelector('#pwCanvasMain').toDataURL();
         handwrite.recognize(url,function(){
            console.log(arguments);
         },function(){
                       console.log(arguments);
                    })
        }
    })
});