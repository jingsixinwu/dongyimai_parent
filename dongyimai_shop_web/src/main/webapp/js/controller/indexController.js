//控制层
app.controller('indexController' ,function($scope,$controller   ,loginService){
   //获取当前登陆的用户名
    $scope.showLoginName=function () {
         loginService.loginName().success(function(response){
             $scope.loginName=response.loginName;

         })
    }

});