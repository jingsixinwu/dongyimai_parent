//用户表服务层
app.service('loginService',function($http){
//读取列表数据绑定到表单中
   this.loginName=function(){
      return $http.get("../login/name.do");
   }

});