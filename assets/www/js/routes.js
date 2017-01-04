var app = angular.module('app');
app.config(['$stateProvider', '$locationProvider',function ($stateProvider,$locationProvider) {
    $locationProvider.html5Mode(false);
    $stateProvider.state('main', { //作业评价
        url: '/main',
        templateUrl:"templates/main.html",
        // controller: 'workPraiseCtrl as ctrl'
    }).state('draw', { //作业评价
        url: '/draw',
        templateUrl:"templates/draw.html",
        controller: 'drawCtrl as ctrl'
    }).state('train', { //作业评价
        url: '/train',
        templateUrl:"templates/train.html",
        controller: 'trainCtrl as ctrl'
    }).state('recognize', { //作业评价
        url: '/recognize',
        templateUrl:"templates/recognize.html",
         controller: 'recognizeCtrl as ctrl'
    })
}]);
app.run(['$state',function ($state) {
    $state.go('draw')
}]);