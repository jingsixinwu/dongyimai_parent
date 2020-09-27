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

    //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
    //从集合中按照key查询对象
    //{"options":[{"id":118,"optionName":"16G","orders":1,"specId":32},
    // {"id":119,"optionName":"32G","orders":2,"specId":32},
    // {"id":120,"optionName":"64G","orders":3,"specId":32},
    // {"id":121,"optionName":"128G","orders":4,"specId":32}]
    // ,"id":32,"text":"机身内存"}
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }




})



