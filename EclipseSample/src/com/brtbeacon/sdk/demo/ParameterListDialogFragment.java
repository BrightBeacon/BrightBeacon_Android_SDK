package com.brtbeacon.sdk.demo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ParameterListDialogFragment extends DialogFragment implements OnItemClickListener {
	
	public interface ParameterListener {
		void onParameterSelected(DialogFragment fragment, ParameterInfo param);
	}
	
	public final static String KEY_TITLE = "key_title";
	public final static String KEY_PARAM_ARRAY = "key_param_array";
	
	private ListView listView;
	private String title;
	//private ArrayList<ParameterInfo> paramList;
	private ParameterInfo[] paramArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getArguments().getString(KEY_TITLE, "参数选择");
		paramArray = (ParameterInfo[]) getArguments().getParcelableArray(KEY_PARAM_ARRAY);
		
		/*
		paramList = getArguments().getParcelableArrayList(KEY_PARAM_ARRAY);
		if(paramList == null) {
			paramList = new ArrayList<ParameterInfo>();
		}
		*/
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(title);
		View view = inflater.inflate(R.layout.dialog_parameter_list, container, false);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(new ArrayAdapter<ParameterInfo>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				paramArray));
		listView.setOnItemClickListener(this);
		return view;
	}
	
	public static ParameterListDialogFragment newInstance(String title, ParameterInfo[] params) {
		ParameterListDialogFragment fragment = new ParameterListDialogFragment();
		Bundle args = new Bundle();
		args.putString(KEY_TITLE, title);
		args.putParcelableArray(KEY_PARAM_ARRAY, params);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(getActivity() instanceof ParameterListener) {
			ParameterListener listener = (ParameterListener)getActivity();
			listener.onParameterSelected(this, (ParameterInfo)arg0.getItemAtPosition(arg2));	
		}
		dismiss();
	}

}
