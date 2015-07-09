/**
 * Created by sxf on 15-7-4.
 */

LeMailModule.controller('distributListController',
    ['$scope', '$http', '$location','ngDialog', function ($scope, $http, $location, ngDialog) {
        $scope.sum_mail = 3;
        $scope.messages = [
            {
                id: 1,
                label: "李乐乐已退回了您的邮件",
                content: "我只负责发工资，交水电费不归我管。请找后勤部"
            },
            {
                id: 2,
                label: "马连韬将您分发的邮件转交给了马塔塔",
                content: "正在假期骑行旅游的路上，不方便处理"
            },
            {
                id: 3,
                label: "大熊：毛总的宴请帖已回复",
                content: "处理结束"
            },
            {
                id: 4,
                label: "小熊把您的邮件转交给徐宇楠",
                content: "处理进行中"
            }
        ];
        $scope.mail_list = [];
        $scope.pageSum = 0;
        $scope.pageNum = 0;
        $scope.onPageLoad = function () {
            $http({
                url: '/api/dispatcher/getall',
                method: 'GET',
                params: {page: 0}
            }).success(function (response) {
                console.log(response);
                if (response.status == 0) {
                    $scope.mail_list = response.data.list;
                    $scope.pageSum = response.data.sum;
                    $scope.pageNum = response.data.page;
                    $scope.sum_mail = $scope.mail_list.length;
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
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

        $scope.getMore = function(pageNum) {
            $http({
                url: '/api/dispatcher/getall',
                method: 'GET',
                params: {page: pageNum}
            }).success(function (response, status, headers, config) {
                console.log(response);
                if (response.status == 0) {
                    $scope.mail_list = response.data.list;
                    $scope.pageSum = response.data.sum;
                    $scope.pageNum = response.data.page;
                    $scope.sum_mail = $scope.mail_list.length;
                } else {
                    alert(response.message);
                }
            }).error(function (response, status, headers, config) {
                console.log(response);
            });
        };

        $scope.$on('$viewContentLoaded', function () {
            alert('1');
        });

        $scope.distribute = function (mail_id) {
            $location.path('/dispatcher/distribute/' + mail_id)
        };

        $scope.onWriteNew = function () {
            $location.path('/handler/new')
        };

        $scope.inform = function ($event, id) {
            $scope.to_id = id;
            ngDialog.open({
                template : '/template/dispatchinfohandle.html',
                controller : 'dispatchInfoHandleController',
                scope: $scope
            });

            $event.stopPropagation();
        };

        $scope.range = function(min, max, step){
            step = step || 1;
            var input = [];
            for (var i = min; i <= max; i += step) input.push(i);
            return input;
        };
    }]);