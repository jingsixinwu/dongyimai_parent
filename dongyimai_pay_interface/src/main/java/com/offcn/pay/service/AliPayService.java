package com.offcn.pay.service;

import java.util.Map;

/**
 * 支付宝连接的接口
 */
public interface AliPayService {

    /**
     * 生成一个支付二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 检测支付状态
     *
     */
    public Map queryPayStatus(String out_trade_no);




}
