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
        $scope.saved_user = {};

        $scope.clickToOpen = function () {
            ngDialog.open({
                template: '/template/signup.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        function cancel(line) {
            if (line != -1)
                $scope.users[line] = $scope.saved_user;
        }

        $scope.onSelectLine = function (line) {
            cancel($scope.edit_line);
            $scope.edit_line = line;
        };

        $scope.onCancel = function ($event) {
            cancel($scope.edit_line);
            $scope.edit_line = -1;
            $event.stopPropagation();
        };

        $scope.saveUser = function ($event, user) {
            $http({
                url: '/api/manager/change',
                method: 'POST',
                params: $scope.user
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.message = "保存成功";
                }else{
                    $scope.message = response.message;
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });
            $scope.edit_line = -1;
            $event.stopPropagation();
        };

        $scope.department = [];
        $scope.show_checker = {};
        $scope.select_checker = function (u, s) {
            u.checker = s;
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
                url: '/api/manager/checker',
                method: 'GET'
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.checkers = response.data.list;
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

        $scope.onSaveOld = function (user) {
            $scope.saved_user = user;
        };


        // 对话框使用
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
            $scope.user.default_checker = $scope.form_checker.id;
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

        $scope.selectedDepartment = {};
        $scope.roles = {};
        $scope.form_checker = {};

        $scope.select = function (selected) {
            $scope.selectedDepartment = selected;
        };

        $scope.form_select_checker = function (selected) {
            $scope.form_checker = selected;
        }
    }]);