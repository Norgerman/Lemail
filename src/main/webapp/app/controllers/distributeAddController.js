/**
 * Created by vvliebe on 15-7-5.
 */
LeMailModule.controller('distributeListController',
    ['$scope','$http','$sce','$routeParams','$location',
    function($scope, $http, $sce, $routeParams, $location){
    $scope.mail_id = 0;
    $scope.mail = {};

    $scope.selectedHandler = null;
    $scope.selectedUsers = [];
    $scope.users = [];

    Array.prototype.removeAt=function(index){
        this.splice(index,1);
    };
    Array.prototype.remove=function(obj){
        var index=this.indexOf(obj);
        if (index>=0){
            this.removeAt(index);
        }
    };

    $scope.selectHandler = function(user){
        console.log(user);
        if (user) {
            $scope.selectedHandler = user;
            $scope.users.remove(user);
            console.log($scope.users);
        } else {
            $scope.users.push($scope.selectedHandler);
            $scope.selectedHandler = user;
        }
    };

    $scope.selectUser = function(user) {
        console.log('user',user);
        $scope.selectedUsers.push(user);
        $scope.users.remove(user);
        $scope.showClick = true;
    };

    $scope.deleteUser = function(index) {
        $scope.users.push($scope.selectedUsers[index]);
        $scope.selectedUsers.splice(index, 1);
        $scope.showClick = true;
    };

    $scope.onDistribute = function(){
        console.log($scope.selectedUsers);
        var readers = '';
        for (var i = 0; i <$scope.selectedUsers.length; ++i) {
            readers = readers.concat($scope.selectedUsers[i].id + '|');
        }
        var handler = null;
        if ($scope.selectedHandler) {
            handler = $scope.selectedHandler.id;
        }
        readers=readers.substring(0,readers.length-1);
        $http({
            url: '/api/dispatcher/dispatch',
            method: 'POST',
            params: {
                id: $scope.mail_id,
                handler : handler,
                review  : false,
                readers : readers
            }
        }).success(function(response){
            if (response.status == 0) {
                $location.path('/dispatcher');
            }
        }).error(function(response){
            console.log(response);
        });
    };

    $scope.addClick = function () {
        $scope.showClick = false;
    };

    $scope.showClick = true;
    $scope.selectedUser = 0;
    $scope.onPageLoad = function(){
        $scope.mail_id = $routeParams.mail_id;

        $http.get('/api/dispatcher/handlers'
        ).success(function(response){
            if (response.status == 0)
                $scope.users = response.data;
            else
                alert(response.message);
        }).error(function(response){
            console.log(response);
        });

        $http({
            url: '/api/dispatcher/detail',
            method: 'GET',
            params: { id: $scope.mail_id }
        }).success(function(response){
            if (response.status == 0) {
                $scope.mail = response.data;
                $scope.mail.content = $sce.trustAsHtml($scope.mail.content);
            } else alert(response.message);
        }).error(function(response){
            console.log(response);
        });
    }
}]);