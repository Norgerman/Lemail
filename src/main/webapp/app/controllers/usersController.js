/**
 * Created by sxf on 15-7-7.
 */

LeMailModule.controller('usersController',
    ['$scope','$http','$location','ngDialog', function($scope, $http, $location, ngDialog) {

        $scope.users = [{
            "id": 1,
            "username": "asdd",
            "name": "dddd",
            "roles": { "manager": false, "dispatcher": false, "handler": false, "reviewer": true },
            "checker": null
        }];

        $scope.message = '';
        $scope.user = {};
        $scope.onSave = function () {
            $http({
                url: '/api/manager/signup',
                method: 'POST',
                params: $scope.user
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.message = "保存成功";
                    ngDialog.closeAll();
                }else{
                    $scope.message = response.message;
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });
            $scope.user = {};
        };

        $scope.clickToOpen = function () {
            ngDialog.open({
                template: '/template/signup.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        $scope.department = [];
        $scope.selectedDepartment = {};

        $scope.onPageLoad = function() {
            $scope.message = '';
            $http({
                url: '/api/manager/user',
                method: 'GET'
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.users = response.data.list;
                }else{
                    alert(response.message);
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });

            $http({
                url: '/api/manager/department',
                method: 'GET'
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.department = response.data;
                }else{
                    alert(response.message);
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });
        };
    }]);