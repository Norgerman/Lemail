/**
 * Created by vvliebe on 15-7-9.
 */
LeMailModule.controller('replyController', ['$scope', '$http', '$location', '$routeParams',
    function ($scope, $http, $location, $routeParams) {
        $scope.review_content = "";
        $scope.reply = {
                "id": 2,
                "subject": "xx",
                "content": "XX",
                "state": 3,
                "date": "2015-07-06 16:23:23",
                "attachment": null,
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
            };
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
            if($scope.review_content == ""){
                alert("审核意见不能为空");
                return
            }
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
                    if(isPass){
                        alert("邮件已通过审核");
                        window.location = "/#/reviewer/unchecked";
                    }
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
                console.log(response);
            });
            if(!isPass){
                $http({
                    url: '/api/message/send',
                    method: 'GET',
                    params: {
                        to: $scope.reply.sender.id,
                        content: "审核|"+$scope.review_content,
                        mail_checked_id: parseInt($scope.mail_id)
                    }
                }).success(function (response, status, headers, config) {
                    console.log(response);
                    if (response.status == 0) {
                        alert("成功退回邮件");
                        window.location = "/#/reviewer/unchecked";
                    } else {
                        alert(response.message);
                    }
                }).error(function (response, status, headers, config) {
                    console.log(response);
                });
            }
        };
    }]);