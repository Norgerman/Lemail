/**
 * Created by sxf on 15-7-7.
 */

LeMailModule.controller('usersController',
    ['$scope','$http','$location','ngDialog', function($scope, $http, $location, ngDialog) {
        $scope.message = '';
        $scope.user = {};
        $scope.users = [{
            "id": 1,
            "username": "asdd",
            "name": "dddd",
            "roles": { "manager": false, "dispatcher": false, "handler": false, "reviewer": true },
            "checker": null
        }];

        $scope.clickToOpen = function () {
            ngDialog.open({
                template: '/template/signup.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };
    }]);