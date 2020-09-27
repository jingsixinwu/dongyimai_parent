app.controller('payController',function($scope,payService){
   //生成本地的二维码
    $scope.createNative=function(){

        payService.createNative().success(function(response){
            $scope.money=  (response.total_fee/100).toFixed(2) ;	//金额
            $scope.out_trade_no= response.out_trade_no;//订单号
            //二维码
            var qr = new QRious({
                element: document.getElementById('qrious'),
                size:250,
                level: 'H',
                value: response.qrcode
            });


            queryPayStatus(response.out_trade_no);//查询支付状态
          });

    }


    //查询支付状态
    queryPayStatus=function(){

        payService.queryPayStatus( $scope.out_trade_no).success(function (response) {
             if(response.success){//支付成功

                 location.href="paysuccess.html#?money="+$scope.money;


             }else{//支付失败
                 if(response.message=='二维码超时'){
                     //document.getElementById('timeout').innerHTML='二维码已过期，刷新页面重新获取二维码。';
                     $scope.createNative();//重新生成二维码
                  }else{

                     location.href="payfail.html";
                  }

             }



        })


    }

    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }





})