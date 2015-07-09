/**
 * Created by sxf on 15-7-5.
 */

LeMailModule.controller('newEmailController',
    ['$scope','$http','$location','$routeParams','ngDialog',
        function($scope, $http, $location, $routeParams, ngDialog){
        $scope.tolist = [ '' ];
        $scope.subject = '';
        $scope.content = '';
        $scope.mail_id = 0;
        $scope.addNew = function() {
            $scope.tolist.push('');
            console.log($scope.tolist);
        };

        $scope.Init = function () {
            $scope.tolist = [ '' ];
            $scope.subject = '';
            $scope.content = '';
            $scope.mail_id = $routeParams.mail_id;
            if ($scope.mail_id != 0) {
                $http({
                    url: '/api/handler/detail',
                    method: 'GET',
                    params: { id: $scope.mail_id }
                }).success(function(response){
                    if (response.status == 0) {
                        $scope.mail = response.data;
                        $scope.subject = '回复： '+$scope.mail.subject;
                        $scope.tolist[0] = $scope.mail.from;
                    } else alert(response.message);
                }).error(function(response){
                    console.log(response);
                });
            }
        };

        $scope.fliter = function (arr) {
            console.log(arr);
            var j = 0;
            for (var i = 0; i< arr.length; ++i) {
                if (arr[i]) {
                    arr[j] = arr[i];
                    ++j;
                }
            }
            arr.length = j;

            return arr;
        };

        function transFn(obj) {
            var str = [];
            for(var p in obj){
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }
            return str.join("&").toString();
        }

        function send_new () {
            $http.post(
                '/api/handler/postmail?' + transFn({
                    'to' : $scope.fliter($scope.tolist),
                    'subject' : $scope.subject,
                    'content' : $scope.content
                })
            ).success(function(response){
                    if (response.status == 0){
                        $scope.message = '发送成功';
                        ngDialog.openConfirm({
                            template: '/template/alert.html',
                            scope: $scope
                        }).then(function () {
                            window.location = '/#/handler/todo';
                        });
                        $scope.Init();
                    } else {
                        alert(response.message);
                    }
                }).error(function(response){
                    console.log(response);
                });
        }

        function send_reply() {
            $http.post(
                '/api/handler/handlemail?' + transFn({
                    'needreply' : true,
                    'id' : $scope.mail_id,
                    'to' : $scope.fliter($scope.tolist),
                    'subject' : $scope.subject,
                    'content' : $scope.content
                })
            ).success(function(response){
                if (response.status == 0){
                    $scope.message = '发送成功';
                    ngDialog.openConfirm({
                        template: '/template/alert.html',
                        scope: $scope
                    }).then(function () {
                        window.location = '/#/handler/todo';
                    });
                } else {
                    alert(response.message);
                }
            }).error(function(response){
                console.log(response);
            });
        }

        $scope.Send = function() {
            if ($scope.tolist.length == 0) { return alert('请填写收件人'); }
            if ($scope.subject.length == 0) { return alert('请填写主题'); }
            if ($scope.content.length == 0) {
                $scope.message = '你确定要发送空内容么？';
            } else {
                $scope.message = '确认发送？';
            }
            ngDialog.openConfirm({
                template: '/template/dialog.html',
                scope: $scope
            }).then(function () {
                if ($scope.mail_id == 0)
                    send_new();
                else send_reply();
            });
        };


}]);