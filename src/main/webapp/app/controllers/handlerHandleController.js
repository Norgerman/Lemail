/**
 * Created by sxf on 15-7-7.
 */
LeMailModule.controller('handlerHandleController',
    ['$scope','$http','$sce','$routeParams','$location','ngDialog',
    function($scope, $http, $sce, $routeParams, $location, ngDialog){

        $scope.mail_id = 0;
        $scope.mail = {};

        $scope.onPageLoad = function(){
            $scope.mail_id = $routeParams.mail_id;

            $http({
                url: '/api/handler/detail',
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
        };
    
        $scope.back = function () {

        };
        
        $scope.reply = function () {
            $location.path('/handler/new/'+$scope.mail_id);
        };

        $scope.done = function () {
            $scope.message = '您确认提交吗？';
            ngDialog.openConfirm({
                template: '/template/dialog.html',
                scope: $scope
            }).then(function (value) {
                $http({
                    url: '/api/handler/handlemail',
                    method: 'POST',
                    params: {
                        id: $scope.mail_id,
                        needreply : false
                    }
                }).success(function(response){
                    if (response.status == 0) {
                        window.location = '/#/handler/todo';
                    } else alert(response.message);
                }).error(function(response){
                    console.log(response);
                });
            });
        };

        $scope.transform = function () {

        };

}]);
