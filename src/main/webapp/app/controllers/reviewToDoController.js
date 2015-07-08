/**
 * Created by vvliebe on 15-7-8.
 */
LeMailModule.controller('reviewToDoController', ['$scope','$http','$location',function($scope, $http, $location){
    $scope.onPageLoad = function() {

    };

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