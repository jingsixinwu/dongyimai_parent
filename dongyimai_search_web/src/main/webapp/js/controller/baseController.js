//将controller层公共的代码提出出来
app.controller("baseController",function($scope){

//定义分页配置
    $scope.paginationConf={
        currentPage:1,//当前页码
        totalItems:10,//总记录数
        itemsPerPage:10,//每页显示的记录数
        perPageOptions:[10,15,20,30,40,50],//选择每页显示记录数据下拉菜单选项
        onChange:function () {
            //从后台来读取分页的数据
            $scope.reloadList();
        }
    }
//重新加载列表数据
    $scope.reloadList=function(){

        //切换页面
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.selectIds=[];
    }


//创建一个存储要删除的id的数组
    $scope.selectIds=[];

//当选择与否的时候，进行移除和添加到数组中
    $scope.updateSelection=function($event,id){
        //通过事件对象判断选中的状态
        if($event.target.checked){//被选中
            //添加的数组中
            $scope.selectIds.push(id);
        }else{//未被选中
            //移除
            //找到id在数组中的位置
            var index=$scope.selectIds.indexOf(id);
            //移除
            $scope.selectIds.splice(index,1);

        }
    }

    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔

    //	[{"id":2,"text":"华为"},{"id":5,"text":"OPPO"}]
    // text  --->  华为 ,   oppo

    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将json字符串转换为json对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }




})



