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
        $scope.edit_line = -1;

        $scope.message = '';
        $scope.user = {};
        $scope.onSave = function () {
            $scope.user.department_id = $scope.selectedDepartment.id;
            var role_str = '';
            if ($scope.roles.manager == 1) role_str = role_str.concat('M');
            if ($scope.roles.dispatcher == 1) role_str = role_str.concat('D');
            if ($scope.roles.handler == 1) role_str = role_str.concat('H');
            if ($scope.roles.reviewer == 1) role_str = role_str.concat('R');
            $scope.user.role = role_str;
            console.log($scope.selectedDepartment);
            console.log($scope.user);
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

        $scope.saveUsers = function () {
            $http({
                url: '/api/manager/change',
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
        };

        $scope.department = [];
        $scope.selectedDepartment = {};
        $scope.roles = {};
        $scope.select = function (selected) {
            $scope.selectedDepartment = selected;
        };

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