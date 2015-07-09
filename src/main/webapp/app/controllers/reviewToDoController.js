/**
 * Created by vvliebe on 15-7-8.
 */
LeMailModule.controller('reviewToDoController', ['$scope', '$http', '$location', '$routeParams',
    function ($scope, $http, $location, $routeParams) {
        $scope.onPageLoad = function () {
            var url;
            if ($routeParams.type == "unchecked") {
                $scope.isReviewed = false;
                url = "/api/reviewer/notcheck";
            } else {
                $scope.isReviewed = true;
                url = "/api/reviewer/checked";
            }
            $http({
                url: url,
                method: 'GET',
                params: {page: 0}
            }).success(function (response, status, headers, config) {
                console.log(response);
                if (response.status == 0) {
                    $scope.review_list = response.data.list;
                    $scope.sum_review = $scope.review_list.length;
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
                console.log(response);
            });
        };

        $scope.isReviewed = true;

        $scope.review_list = [];
        $scope.gotoReply = function(id){
            if($scope.isReviewed){
                return;
            }
            window.location = "/#/reviewer/reply/"+id;
        }
    }]);