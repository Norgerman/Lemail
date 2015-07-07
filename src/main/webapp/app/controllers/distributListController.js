/**
 * Created by sxf on 15-7-4.
 */

LeMailModule.controller('distributListController',
    ['$scope','$http','$location',function($scope, $http, $location){
    $scope.sum_mail = 3;
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
    $scope.mail_list = [
        {
            id : 1,
            subject: "唐山铁魂有限公司：手机模型材料不足需延迟提交",
            state: 0,
            date: "2015-07-05 12:00:00",
            attachment: "这是附件",
            from: "lilelr@163.com",
            review: 0,
            tag: "延期",
            belong_user_id : 0
        },
        {
            id : 2,
            subject: "北航本科生孙笑应聘总经理助手",
            state: 0,
            date: "2015-07-04 19:00:00",
            attachment: "这是附件",
            from: "sunxfancy@gmail.com",
            review: 0,
            tag: "招聘",
            belong_user_id : 0
        },
        {
            id : 3,
            subject: "交水电费,要不断水断电",
            state: 3,
            date: "2015-07-03 10:00:00",
            attachment: "这是附件",
            from: "xiong@gmail.com",
            review: 0,
            tag: "战书",
            belong_user_id : 0
        },
        {
            id : 4,
            subject: "广州大同塑胶有限公司：塑胶降价，是否需要进货",
            state: 3,
            date: "2015-07-02 10:23:50",
            attachment: "这是附件",
            from: "datong@126.com",
            review: 0,
            tag: "战书",
            belong_user_id : 0
        },
        {
            id : 5,
            subject: "深圳绝对值科技有限公司：贵公司网站项目已部署完成，请验收",
            state: 3,
            date: "2015-07-01 12:56:23",
            attachment: "这是附件",
            from: "ABS@gmail.com",
            review: 0,
            tag: "战书",
            belong_user_id : 0
        }
    ];
    $scope.mail_list = [];
    $scope.onPageLoad = function () {
        $http({
            url: '/api/dispatcher/getall',
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
    };

    $scope.$on('$viewContentLoaded', function() {
        alert('1');
    });

    $scope.distribute = function(mail_id){
      $location.path('/dispatcher/distribute/'+mail_id)
    };

    $scope.onWriteNew = function () {
        $location.path('/handler/new')
    };

    $scope.inform = function($event){
        alert("大圣，快去实现distributeController的inform方法");
        $event.stopPropagation();
    }
}]);