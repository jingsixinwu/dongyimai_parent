 //用户表控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={};//需要定义，要不然判断用户为空不能正常执行
	$scope.reg=function(){

		//判断用户名是否为空
		if($scope.entity.username==""||$scope.entity.username==null){
			alert("请输入注册的用户名");
			return;
		}
		//判断用户密码是否为空
		if($scope.entity.password==""||$scope.entity.password==null){
			alert("请输入注册的用户密码");
			return;
		}
		//判断用户确认密码是否为空
		if($scope.password==""||$scope.password==null){
			alert("请输入确认密码");
			return;
		}
		//判断确认密码和密码是否一致
		if($scope.entity.password!=$scope.password){
			alert("两次输入的密码不一致");
			return;
		}
		//判断手机号码是否为空
		if($scope.entity.phone==""||$scope.entity.phone==null){
			alert("请输入手机号码");
			return;
		}
		//判断手机号码是否为空
		if($scope.entity.code==""||$scope.entity.phone==null){
			alert("请输入手机号码");
			return;
		}


		userService.add($scope.entity,$scope.code).success(function(response){
			if(response.success){

				location.href="login.html";

			}else{
				alert(response.message);
			}


		});
   }


	/**
	 * 发送验证码
	 */
	$scope.sendCode=function(){
       if($scope.entity.phone==null){
       	  alert("请你输入手机号");
	   }

       userService.sendCode($scope.entity.phone).success(function(response){
       	     alert(response.message);

	   });

	}




	//读取列表数据绑定到表单中
	$scope.findAll=function(){
		userService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userService.update( $scope.entity ); //修改  
		}else{
			serviceObject=userService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		userService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}








    
});	