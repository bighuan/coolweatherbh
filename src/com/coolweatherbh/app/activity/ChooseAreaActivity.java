package com.coolweatherbh.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweatherbh.app.R;
import com.coolweatherbh.app.db.CoolWeatherDB;
import com.coolweatherbh.app.model.City;
import com.coolweatherbh.app.model.County;
import com.coolweatherbh.app.model.Province;
import com.coolweatherbh.app.util.HttpCallbackListener;
import com.coolweatherbh.app.util.HttpUtil;
import com.coolweatherbh.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;

	public static final int LEVEL_CITY = 1;

	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;

	private TextView titleText;

	private ListView listView;

	private ArrayAdapter<String> adapter;

	private CoolWeatherDB coolWeatherDB;

	private List<String> dataList = new ArrayList<String>();

	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;

	/**
	 * ���б�
	 */
	private List<City> cityList;

	/**
	 * ���б�
	 */
	private List<County> countyList;

	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;

	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;

	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					Log.d("ChooseAreaActivity", "222222222222222");
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					Log.d("ChooseAreaActivity", "33333333333333333");
					selectedCity = cityList.get(index);
					queryCounties();
				}
			}

		});
		Log.d("ChooseAreaActivity", "111111111111");
		queryProvinces();// ����ʡ������
	}

	/**
	 * ��ѯȫ�����е�ʡ,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ.��
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();// �����ݿ��ж�ȡ����
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			// �ӷ������ϲ�ѯ����
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ��ʡ�����е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * ��ѯѡ���������е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ.
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}

	}

	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		// ���ݴ���Ĳ�����ƴװ��ѯ��ַ
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		/*
		 * 1,ȷ����ѯ��ַ��,����HttpUtil��sendHttpRequest()���������������������,��Ӧ��
		 * ���ݻ�ص���onFinish()������, 2,Ȼ����ȥ����Utility��handleProvincesResponse()
		 * �����������ʹ������������ص�����,���洢�����ݿ���, 3,��������һ���ܹؼ�,�ڽ����ʹ�
		 * ����֮��,�ٴε���queryProvinces()���������¼���ʡ������,��queryProvinces()ǣ��
		 * ����UI����,��˱��������̵߳���,����runOnUiThread()������ʵ�ִ����߳��л�������
		 * ����ʵ��ԭ��Ҳ�ǻ����첽��Ϣ�������Ƶ�.�������ݿ����Ѿ���������,��˵���queryProvinces() �ͻ�ֱ�ӽ�������ʾ����������
		 * 4,���û������ĳ��ʡ��ʱ��,����뵽onItemClick()������,���ʱ�����ݵ�ǰ�ļ������ж�
		 * �ǵ���queryCities()����queryCounties()����,�������������ڲ����̺�queryProvinces()����
		 * ������ͬ.
		 */
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("Province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ�Ի����ȿ�
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ�����...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ��дonBackPressed()����������Ĭ��Back������Ϊ ����Back����,���ݵ�ǰ�ļ������ж�,
	 * ��ʱӦ�÷������б�,ʡ�б�,����ֱ���˳�
	 */

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}