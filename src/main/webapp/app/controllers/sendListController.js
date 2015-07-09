/**
 * Created by sxf on 15-7-5.
 */

LeMailModule.controller('sendListController',
    ['$scope','$http','$location',function($scope, $http, $location){
    $scope.sum_mail = 0;
    $scope.messages = [];
    $scope.mail_list = [];
    $scope.onPageLoad = function () {
        $http({
            url: '/api/handler/outbox',
            method: 'GET',
            params: { page : 0 }
        }).success(function(response, status, headers, config){
            console.log(response);
            if (response.status == 0){
                $scope.mail_list = response.data.list;
                $scope.sum_mail = $scope.mail_list.length;
            }else{
                alert(response.message);
            }
        }).error(function(response, status, headers, config){
            console.log(response);
        });
    };

    $scope.onselect = function (mail_id) {
        $location.path('/handler/outboxdetail/'+mail_id)
    };
}]);