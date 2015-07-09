/**
 * Created by yuxiao on 15/7/9.
 */
LeMailModule.controller('dispatchInfoHandleController',
    ['$scope','$http','$sce','$routeParams','$location',
        function($scope, $http, $sce, $routeParams, $location){
            console.log($scope.$parent.to_id);
             $scope.submit = function(){
                 if ($scope.msg != ""){
                     $scope.msginfo = {
                         to: $scope.$parent.to_id,
                         content:$scope.msg
                     };
                     $http({
                         url: '/api/message/send',
                         method: 'POST',
                         params: $scope.msginfo
                     }).success(function(response, status, headers, config){
                         console.log(response);
                         //console.log(status);
                         //console.log(headers);
                         //console.log(config);
                         if (response.status == 0){
                             // send success
                             alert("发送成功");
                             $scope.closeThisDialog();
                         }else if(response.status == 401){
                             //用户未登录
                             alert("发送失败，用户未登录！");
                             $scope.closeThisDialog();
                         }else if(response.status == 402){
                             //接受者不存在
                             alert("发送失败，接受者不存在！");
                             $scope.closeThisDialog();
                         }
                     }).error(function(response, status, headers, config){

                     });
                 }else{
                     alert("发送消息不能为空！");
                 }
             };

            $scope.cancel = function(){
                $scope.closeThisDialog();
            };

        }]);