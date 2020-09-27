package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        Map<String,String> map=new HashMap<String, String>();
        //转换下单金额按照元
        long total = Long.parseLong(total_fee);
        BigDecimal bigTotal = BigDecimal.valueOf(total);
        BigDecimal cs = BigDecimal.valueOf(100d);

        BigDecimal bigYuan = bigTotal.divide(cs);
        System.out.println("预下单金额:"+bigYuan.doubleValue());



        AlipayTradePrecreateRequest request   =   new   AlipayTradePrecreateRequest (); //创建API对应的request类
        request . setBizContent ( "{"   +
                "    \"out_trade_no\":\""+out_trade_no+"\","   + //商户订单号
                "    \"total_amount\":\""+bigYuan.doubleValue()+"\","   +
                "    \"subject\":\"测试支付宝沙箱的使用\","   +
                "    \"store_id\":\"NJ_001\","   +
                "    \"timeout_express\":\"90m\"}" ); //订单允许的最晚付款时间
        try {
            //发出预下单业务请求
            AlipayTradePrecreateResponse response  = alipayClient . execute ( request );
            //从相应对象读取相应结果
            String code = response.getCode();
            System.out.println("响应码:"+code);
            //全部的响应结果
            String body = response.getBody();
            System.out.println("返回结果:"+body);
            if(code.equals("10000")){
                map.put("qrcode", response.getQrCode());
                map.put("out_trade_no", response.getOutTradeNo());
                map.put("total_fee",total_fee);
                System.out.println("qrcode:"+response.getQrCode());
                System.out.println("out_trade_no:"+response.getOutTradeNo());
                System.out.println("total_fee:"+total_fee);
            }else{
                System.out.println("预下单接口调用失败:"+body);
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;
    }
    /**
     * 交易查询接口alipay.trade.query：
     * 获取指定订单编号的，交易状态
     * @throws AlipayApiException
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map<String,String> map=new HashMap<String, String>();
        AlipayTradeQueryRequest request   =   new   AlipayTradeQueryRequest (); //创建API对应的request类
        request . setBizContent ( "{"   +
                "    \"out_trade_no\":\""+out_trade_no+"\","   +
                "    \"trade_no\":\"\"}" );  //设置业务参数
        try {
            AlipayTradeQueryResponse response   =   alipayClient . execute ( request ); //通过alipayClient调用API，获得对应的response类

            String code=response.getCode();
            System.out.println("查询交易状态返回值1:"+response.getBody());
            if(code.equals("10000")){
                System.out.println("查询交易状态返回值2:"+response.getBody());
                map.put("out_trade_no", out_trade_no);
                map.put("tradestatus", response.getTradeStatus());
                map.put("trade_no",response.getTradeNo());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        return map;
    }
}
