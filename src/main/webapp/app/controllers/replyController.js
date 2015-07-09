/**
 * Created by vvliebe on 15-7-9.
 */
LeMailModule.controller('replyController', ['$scope', '$http', '$location', '$routeParams',
    function ($scope, $http, $location, $routeParams) {
        $scope.reply = {};
        $scope.mail_id = 0;

        $scope.onPageLoad = function() {
            $scope.mail_id = $routeParams.mail_id;
            $http({
                url: '/api/reviewer/detail',
                method: 'GET',
                params: {id: $scope.mail_id}
            }).success(function (response, status, headers, config) {
                console.log(response);
                if (response.status == 0) {
                    $scope.reply = response.data;
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
                console.log(response);
            });
        };

        $scope.review_mail = function(isPass){
            $http({
                url: '/api/reviewer/checkmail',
                method: 'GET',
                params: {
                    id: $scope.mail_id,
                    pass: isPass
                }
            }).success(function (response, status, headers, config) {
                console.log(response);
                if (response.status == 0) {
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
                console.log(response);
            });
        };
    }]);