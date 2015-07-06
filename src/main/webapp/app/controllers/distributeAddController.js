/**
 * Created by vvliebe on 15-7-5.
 */
LeMailModule.controller('distributeListController',  ['$scope','$http',function($scope, $http){
    $scope.mail = {
        id : 1,
        subject: "唐山铁魂有限公司：手机模型材料不足需延迟提交",
        state: 0,
        date: "2015-07-05 12:00:00",
        attachment: "这是附件",
        from: "lilelr@163.com",
        review: 0,
        tag: "延期",
        content : "小熊把您的邮件转交给徐宇楠",
        belong : {
            id : 1,
            name : "侠客"
        },
        readers : []
    };

    $scope.selectedHandler = null;
    $scope.selectedUsers = [];

    $scope.handler = null;
    $scope.users = [
        {id: 1, name:"侠客"},
        {id: 2, name:"熊"},
        {id: 3, name:"孙猴子"},
        {id: 4, name:"李很乐"},
        {id: 5, name:"马涛涛"},
        {id: 6, name:"大爽"},
        {id: 7, name:"monkey"},
        {id: 8, name:"孙芙媛"},
        {id: 9, name:"童××"}
    ];

    $scope.selectHandler = function(user){
        console.log(user);
        $scope.selectedHandler = user;
    };

    $scope.selectUser = function(user) {
        console.log(user);
        $scope.selectedUsers.push(user);
        $scope.showClick = true;
    };

    $scope.deleteUser = function(index) {
        $scope.selectedUsers.splice(index, 1);
        $scope.showClick = true;
    };

    $scope.onDistribute = function(){

    };

    $scope.addClick = function () {
        $scope.showClick = false;
    };

    $scope.showClick = true;

    $scope.onPageLoad = function(){
        $http.get('/api/dispatcher/handlers'
        ).success(function(response){
            if (response.status == 0)
                $scope.users = response.data;
        }).error(function(response){
            console.log(response);
        });
    }
}]);