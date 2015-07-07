/**
 * Created by sxf on 15-7-5.
 */

LeMailModule.controller('newEmailController',
    ['$scope','$http','$location',function($scope, $http, $location){
        $scope.tolist = [ '' ];
        $scope.subject = '';
        $scope.content = '';

        $scope.addNew = function() {
            $scope.tolist.push('');
            console.log($scope.tolist);
        };

        $scope.Init = function () {
            $scope.tolist = [ '' ];
            $scope.subject = '';
            $scope.content = '';
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

        $scope.Send = function() {
            if ($scope.tolist.length == 0) { return alert('请填写收件人');  }
            if ($scope.subject.length == 0) { return alert('请填写主题'); }
            if ($scope.content.length == 0) {
                if (!confirm('你确定要发送空内容么?')) { return; }
            }
            $http.post(
                '/api/handler/postmail?' + transFn({
                    'to' : $scope.fliter($scope.tolist),
                    'subject' : $scope.subject,
                    'content' : $scope.content
                })
            ).success(function(response){
                if (response.status == 0){
                    alert('发送成功');
                    $scope.Init();
                } else {
                    alert(response.message);
                }
            }).error(function(response){
                console.log(response);
            });
        };
}]);