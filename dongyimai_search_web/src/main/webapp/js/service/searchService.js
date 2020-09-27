app.service("searchService",function($http){
    /**
     * 根据查询条件查询商品sku信息
     */
    this.search=function(searchMap){

       return $http.post('itemSearch/search.do',searchMap);

    }
})