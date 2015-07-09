/**
 * Created by sxf on 15-7-4.
 */

LeMailModule.controller('distributListController',
    ['$scope', '$http', '$location','ngDialog', function ($scope, $http, $location, ngDialog) {
        $scope.sum_mail = 3;
        $scope.messages = [];
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