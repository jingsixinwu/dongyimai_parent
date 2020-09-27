app.controller("searchController",function($scope,searchService,$location) {
    //构建一个搜索对象
    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '', 'spec': {}, 'price':'',pageNo:1,pageSize:10,sort:'',sortField:''};

    //根据条件查询商品sku信息

    $scope.search = function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (response) {
            //接收查询的结果
            $scope.resultMap = response;
            buildPageLabel();//调用生成页码标签的方法

        })


    }


    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        //初始化当前页为1
        $scope.searchMap.pageNo=1;

        if (key == 'category' || key == 'brand' || key=='price') {

            $scope.searchMap[key] = value;

        } else {
            $scope.searchMap.spec[key] = value;


        }

        $scope.search();//执行搜索
    }
    //移除搜索项
    //添加搜索项
    $scope.removeSearchItem = function (key) {
        //初始化当前页为1
        $scope.searchMap.pageNo=1;

        if (key == 'category' || key == 'brand' ||key== 'price') {//如果是分类和品牌

            $scope.searchMap[key] = "";

        } else {//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性  delete 操作符用于删除对象的某个属性。


        }
        $scope.search();//执行搜索
    }

    //构建一个分页的标签(totalPages为总页数),最多只显示5页，其余用 ... 来表示
    buildPageLabel=function(){
        //创建一个存储分页标签的数组
        $scope.pageLabel=[];
       //获取最大页面数据
        var maxPageNo=$scope.resultMap.totalPages;
        var firstLabel=1//开始页码
        var lastLabel=maxPageNo;//结束页面
        $scope.firstDot=true;//在前面添加省略号
        $scope.lastDot=true;//在后面添加省略号


        if($scope.resultMap.totalPages>5){
              if($scope.searchMap.pageNo<=3){

                  lastLabel=5;//显示前五页的标签
                  $scope.firstDot=false;//前面不加

              }else if($scope.searchMap.pageNo>=lastLabel-2){
                  firstLabel=maxPageNo-4;
                  $scope.lastDot=false;//后面不加

              }else{//显示当前页为中心的5页
                  firstLabel=$scope.searchMap.pageNo-2;
                  lastLabel=$scope.searchMap.pageNo+2;
              }
       }else{//小于5页时，前后都不加
            $scope.firstDot=false;
            $scope.lastDot=false;


        }
        //循环产生页码标签
        for(var i=firstLabel;i<=lastLabel;i++){

            $scope.pageLabel.push(i);
        }
    }

    //根据页面进行查询
    $scope.queryByPage=function(pageNo){
        //验证页码
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){
             return;
        }

        //将页码更新到 searchMap中
        $scope.searchMap.pageNo=pageNo;

        $scope.search();


    }

    //判断页面是不是第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }


    }

    //判断指定页码是否是当前页
    $scope.isPage=function(p){
        if(parseInt(p)==parseInt($scope.searchMap.pageNo)){

            return true;

        }else{
            return false;
        }

    }

    //设置排序规则
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function() {


        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {//如果包含
                return true;
            }
        }
        return false;
    }

    //接收首页传递的搜索数据进行搜索
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();

    }
})






