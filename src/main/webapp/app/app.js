/**
 * Created by vvliebe on 6/29/15.
 */

var LeMailModule = angular.module('LeMailModule', ['ngRoute','ngSanitize', 'ui.select', 'textAngular', 'ui.bootstrap']);

LeMailModule.config(['$routeProvider', "$httpProvider",  function($routeProvider, $httpProvider){
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
     //Enables Request.IsAjaxRequest() in ASP.NET MVC
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    // Disable IE ajax request caching
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
    $routeProvider.when('/login',{
        templateUrl: '/template/login.html'
    }).when('/home',{
        templateUrl: '/template/home.html'
    }).when('/dispatcher',{
        templateUrl: '/template/dispatcher.html'
    }).when('/reviewer',{
        templateUrl: '/template/reviewer.html'
    }).when('/handler/todo',{
        templateUrl: '/template/handler/todo.html'
    }).when('/handler/done',{
        templateUrl: '/template/handler/done.html'
    }).when('/handler/send',{
        templateUrl: '/template/handler/send.html'
    }).when('/handler/new',{
        templateUrl: '/template/handler/new.html'
    }).when('/manager',{
        templateUrl: '/template/manager.html'
    }).when('/dispatcher/distribute/:mail_id',{
        templateUrl: '/template/dispatcher/distribute.html'
    }).otherwise({
        templateUrl: '/template/login.html'
    });
}]);


LeMailModule.controller('LeMailController',
    ['$scope', '$http', '$location', '$templateCache',
        function($scope, $http, $location, $templateCache){
    $scope.title = "登陆";

    $scope.user = {
        id: 0,
        name: '',
        username: '',
        roles:{
            dispatcher: 0,
            reviewer: 0,
            handler: 0,
            manager: 0
        },
        checker: null
    };

    $scope.default_role_url = "";

    $scope.sidebarItems = {
        dispatcher: {
            title: '分发',
            item : [
                { name : '所有邮件', url: '/#/dispatcher', icon: 'fa fa-envelope' }
            ]
        },
        handler: {
            title: '处理',
            item : [
                { name : '未处理', url: '/#/handler/todo', icon: 'fa fa-bookmark' },
                { name : '写新邮件', url: '/#/handler/new', icon: 'fa fa-pencil-square-o' },
                { name : '收件箱', url: '/#/handler/done', icon: 'fa fa-inbox' },
                { name : '发件箱', url: '/#/handler/send', icon: 'fa fa-share-square' }
            ]
        },
        manager: {
            title: '管理',
            item : [
                { name : '设置', url: '/#/manager', icon: 'fa fa-cog' }
            ]
        },
        reviewer: {
            title: '审核',
            item : [
                { name : '审核列表', url: '/#/reviewer', icon: 'fa fa-check-circle' }
            ]
        }
    };


    $scope.$on('login', function(event,data){
        $scope.user = data;
        $scope.title = "欢迎使用Lemail";
    });

    $scope.signout = function(){
        $http({
            url: '/api/user/logout',
            method: 'POST'
        }).success(function(response){
            console.log(response);
            if(response.status == 0){
                $location.path("/login");
                $scope.title = "登陆";
            }
        }).error(function(response){

        });
    };

    $scope.load = function(){
        $http({
            url: '/api/user/getuser',
            method: 'POST'
        }).success(function(response, status, headers, config){
            console.log(response);
            if (response.status == 0){
                $scope.user = response.data;
                $scope.title = "欢迎使用Lemail";
                var firstRole = "";
                if ($scope.user.roles["dispatcher"] == 1){
                    firstRole = "dispatcher";
                } else if ($scope.user.roles["handler"] == 1){
                    firstRole = "handler";
                } else if ($scope.user.roles["reviewer"] == 1){
                    firstRole = "reviewer";
                } else if ($scope.user.roles["manager"] == 1){
                    firstRole = "manager";
                }
                $scope.default_role_url = $scope.sidebarItems[firstRole]['url'][0];
                $scope.$broadcast('changeMainContent', $scope.default_role_url);
            }else if(response.status == 401){
                $location.path("/login");
                $scope.title = "登陆";
            }
        }).error(function(response, status, headers, config){

        });
    }

}]);


