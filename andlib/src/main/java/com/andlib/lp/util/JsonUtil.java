package com.andlib.lp.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

	@SuppressWarnings("hiding")
	public static <T> T parse(String data, Class<T> class1) {
		return JSON.parseObject(data, class1);
		// Gson().fromJson(data, class1);
	}


	@SuppressWarnings("hiding")
	public static <T> List<T> parseList(String data, Class<T> class1) {
		if (TextUtils.isEmpty(data)) {
			return null;
		}
		List<T> mList = new ArrayList<T>();
		try {
			JSONArray mArray = new JSONArray(data);
			final int size = mArray.length();
			for (int i = 0; i < size; i++) {
				T t = parse(mArray.getJSONObject(i).toString(), class1);
				mList.add(t);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mList=null;
		}
		return mList;
	}

//no need filed
//	SimplePropertyPreFilter filter = new SimplePropertyPreFilter(Student.class, "id","age");
//  String jsonStu =JSON.toJSONString(students,filter);

	
	
	
}
