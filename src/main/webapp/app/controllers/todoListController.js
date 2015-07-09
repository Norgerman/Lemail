/**
 * Created by sxf on 15-7-5.
 */

LeMailModule.controller('todoListController', ['$scope','$http','$location',function($scope, $http, $location){
    $scope.sum_mail = 0;
    $scope.messages = [
        {
            id : 1,
            label : "李乐乐已退回了您的邮件",
            content : "我只负责发工资，交水电费不归我管。请找后勤部"
        },
        {
            id : 2,
            label : "马连韬将您分发的邮件转交给了马塔塔",
            content : "正在假期骑行旅游的路上，不方便处理"
        },
        {
            id : 3,
            label : "大熊：毛总的宴请帖已回复",
            content : "处理结束"
        },
        {
            id : 4,
            label : "小熊把您的邮件转交给徐宇楠",
            content : "处理进行中"
        }
    ];
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