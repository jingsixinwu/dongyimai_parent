app.controller('contentController',function($scope,contentService){
    //创建一个集合接收后台传递的广告
    $scope.contentList=[];
    /**
     * 根据分类查询广告
     */
    $scope.findByCategoryId=function(categoryId){
        contentService.findByCategoryId(categoryId).success(function(response){

            $scope.contentList=response;


        })





    }

//搜索跳转
    $scope.search=function(){
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }





});