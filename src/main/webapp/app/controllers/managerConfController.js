/**
 * Created by sxf on 15-7-5.
 */



LeMailModule.controller('managerConfController',
    ['$scope','$http','$location', function($scope, $http, $location) {
        $scope.conf = {
            username : '',
            password : '',
            hostname_smtp : '',
            hostname_imap : ''
        };
        $scope.message = '';

        $scope.onSave = function () {
            $http({
                url: '/api/manager/setconf',
                method: 'POST',
                params: $scope.conf
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
        };

        $scope.onPageLoad = function() {
            $scope.message = '';
            $http({
                url: '/api/manager/getconf',
                method: 'GET',
                params: { page : 0 }
            }).success(function(response, status, headers, config){
                console.log(response);
                if (response.status == 0){
                    $scope.conf = response.data;
                }else{
                    alert(response.message);
                }
            }).error(function(response, status, headers, config){
                console.log(response);
            });
        };
}]);