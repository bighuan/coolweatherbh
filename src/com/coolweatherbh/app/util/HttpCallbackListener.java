package com.coolweatherbh.app.util;
/**
 * �ص����񷵻صĽ��
 */
public interface HttpCallbackListener {
	
	void onFinish(String response);

	void onError(Exception e);
}
