/**
 * Created by vvliebe on 15-7-4.
 */
LeMailModule.controller('loginController', ['$scope','$http','$location', '$templateCache',function($scope, $http, $location, $templateCache){
    $scope.text = "LEMAIL";
    $scope.userinfo = {
        username: '',
        password:''
    };

    $scope.submitForm = function(isValid){
        $scope.submitted = true;
        if (isValid){
             $http({
                 url: '/api/user/login',
                 method: 'POST',
                 params: $scope.userinfo
             }).success(function(response, status, headers, config){
                 console.log(response);
                 //console.log(status);
                 //console.log(headers);
                 //console.log(config);
                 if (response.status == 0){
                     // login success
                     console.log(response);
                     var role = "";
                     if(response.data.roles["dispatcher"]){
                        role = "dispatcher";
                     }else if(response.data.roles["handler"]){
                         role = "handler";
                     }else if(response.data.roles["reviewer"]){
                         role = "reviewer";
                     }else if(response.data.roles["manager"]){
                         role = "manager";
                     }
                     //$location.path("/dispatcher");
                     $scope.$emit('login', response.data, role);
                 }else if(response.status == 1000){

                 }else if(response.status == 1001){

                 }
             }).error(function(response, status, headers, config){

             });
        }
    }
}]);