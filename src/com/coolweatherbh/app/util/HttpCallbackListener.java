package com.coolweatherbh.app.util;
/**
 * 回调服务返回的结果
 */
public interface HttpCallbackListener {
	
	void onFinish(String response);

	void onError(Exception e);
}
