/**
 * Created by sxf on 15-7-7.
 */
LeMailModule.controller('detailDoneController',
    ['$scope','$http','$sce','$routeParams','$location',
    function($scope, $http, $sce, $routeParams, $location){

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
        }
}]);
