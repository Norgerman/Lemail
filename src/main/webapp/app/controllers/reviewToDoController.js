/**
 * Created by vvliebe on 15-7-8.
 */
LeMailModule.controller('reviewToDoController', ['$scope', '$http', '$location', '$routeParams',
    function($scope, $http, $location, $routeParams){
    $scope.onPageLoad = function() {
        var url ;
        if($routeParams.type == "unchecked"){
            $scope.isReviewed = false;
            url = "/api/reviewer/notcheck";
        }else{
            $scope.isReviewed = true;
            url = "/api/reviewer/checked";
        }
        $http({
            url: url,
            method: 'GET',
            params: { page : 0 }
        }).success(function(response, status, headers, config){
            console.log(response);
            if (response.status == 0){
                $scope.review_list = response.data.list;
                $scope.sum_review = $scope.review_list.length;
            }else{
                alert(response.message);
            }
        }).error(function(response, status, headers, config){
            console.log(response);
        });
    };

    $scope.isReviewed = true;

    $scope.review_list = [
        {
            "id": 2,
            "subject": "xx",
            "state": 3,
            "date": "2015-07-06 16:23:23",
            "to": "594254655@qq.com",
            "tag": null,
            "checker": {
                "id": 15,
                "name": "a"
            },
            "sender": {
                "id": 15,
                "name": "a"
            },
            "reply": {
                "id": 14,
                "subject": "xxxx"
            }
        },
        {
            "id": 2,
            "subject": "xx",
            "state": 4,
            "date": "2015-07-06 16:23:23",
            "to": "594254655@qq.com",
            "tag": null,
            "checker": {
                "id": 15,
                "name": "a"
            },
            "sender": {
                "id": 15,
                "name": "a"
            },
            "reply": {
                "id": 14,
                "subject": "xxxx"
            }
        },
        {
            "id": 2,
            "subject": "xx",
            "state": 7,
            "date": "2015-07-06 16:23:23",
            "to": "594254655@qq.com",
            "tag": null,
            "checker": {
                "id": 15,
                "name": "a"
            },
            "sender": {
                "id": 15,
                "name": "a"
            },
            "reply": {
                "id": 14,
                "subject": "xxxx"
            }
        }
    ];
}]);