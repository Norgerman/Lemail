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
        $scope.saved_users = {}; // 备份模型
        $scope.edit_line = -1;

        function translate(roles) {
            var role_str = '';
            if (roles.manager == 1) role_str = role_str.concat('M');
            if (roles.dispatcher == 1) role_str = role_str.concat('D');
            if (roles.handler == 1) role_str = role_str.concat('H');
            if (roles.reviewer == 1) role_str = role_str.concat('R');
            return role_str;
        }

        function clone(myObj){
            if(typeof(myObj) != 'object') return myObj;
            if(myObj == null) return myObj;

            var myNewObj = {};

            for (var i in myObj)
                myNewObj[i] = clone(myObj[i]);
            return myNewObj;
        }

        function cancel(line) {
            if (line != -1) {
                $scope.users[line] = clone($scope.saved_users[line]);
            }
        }

        $scope.onSelectLine = function (line) {
            if (line != $scope.edit_line)
                cancel($scope.edit_line);
            $scope.edit_line = line;
        };

        $scope.onCancel = function ($event) {
            cancel($scope.edit_line);
            $scope.edit_line = -1;
            $event.stopPropagation();
        };

        $scope.saveUser = function ($event, user) {
            $scope.user.default_checker = $scope.form_checker.id;
            var temp = {
                id : user.id,
                name : user.name,
                role : translate(user.roles),
                default_checker : $scope.show_checker.id
            };
            if (user.show_password)
                temp.password = user.show_password;

            $http({
                url: '/api/manager/change',
                method: 'POST',
                params: temp
            }).success(function(response){
                console.log(response);
                if (response.status == 0){
                    $scope.message = "保存成功";
                    location.reload(true);
                }else{
                    $scope.message = response.message;
                }
            }).error(function(response){
                console.log(response);
            });
            $scope.edit_line = -1;
            $event.stopPropagation();
        };

        $scope.department = [];
        $scope.show_checker = {};
        $scope.select_checker = function (s) {
            $scope.show_checker = s;
        };

        $scope.getMore = function (more_page) {
            $http({
                url: '/api/manager/user',
                method: 'GET',
                params: {
                    page : more_page
                }
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.users = response.data.list;
                    $scope.pageSum = response.data.sum;
                    $scope.pageNum = response.data.page;
                    $scope.saved_users = clone($scope.users);
                }else{
                    alert(response.message);
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });
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
                    $scope.pageSum = response.data.sum;
                    $scope.pageNum = response.data.page;
                    $scope.saved_users = clone($scope.users);
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


        // 对话框使用

        $scope.clickToOpen = function () {
            ngDialog.open({
                template: '/template/signup.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        $scope.message = '';
        $scope.user = {};
        $scope.onSave = function () {
            $scope.user.department_id = $scope.selectedDepartment.id;
            $scope.user.role = translate($scope.roles);
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
        };

        $scope.range = function(min, max, step){
            step = step || 1;
            var input = [];
            for (var i = min; i <= max; i += step) input.push(i);
            return input;
        };
    }]);