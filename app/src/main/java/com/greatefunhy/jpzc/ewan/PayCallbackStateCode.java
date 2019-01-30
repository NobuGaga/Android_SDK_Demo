package com.greatefunhy.jpzc.ewan;

public class PayCallbackStateCode {
	public static final int Success = 1;	//回调至Unity的支付成功状态码
	public static final int Fail = -1;		//回调至Unity的支付失败状态码
	public static final int Cancel = 0;		//回调至Unity的支付取消状态码
}
