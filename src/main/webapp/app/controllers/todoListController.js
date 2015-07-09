/**
 * Created by sxf on 15-7-5.
 */

LeMailModule.controller('todoListController', ['$scope','$http','$location',function($scope, $http, $location){
    $scope.sum_mail = 0;
    $scope.messages = [];
    $scope.mail_list = [];
    $scope.onPageLoad = function () {
        $http({
            url: '/api/handler/nothandle',
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

        $http({
            url: '/api/message/getmsgs',
            method: 'GET'
        }).success(function (response) {
            console.log(response);
            if (response.status == 0) {
                $scope.messages = response.data;
                for (var i = 0; i < $scope.messages.length; ++i)
                {
                    var msg = $scope.messages[i];
                    var ss = msg.content.split('|');
                    msg.label = ss[0];
                    msg.content = ss[1];
                }
            } else {
                alert(response.message);
            }
        }).error(function (response, status, headers, config) {
            console.log(response);
        });

    };

    $scope.onSelect = function (mail_id) {
        $location.path('/handler/handle/'+mail_id);
    }
}]);