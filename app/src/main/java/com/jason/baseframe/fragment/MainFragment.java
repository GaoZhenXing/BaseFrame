package com.jason.baseframe.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jason.baseframe.R;

/**
 * Created by MJJ on 2015/7/29.
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private int number;

    public MainFragment(int number) {
        this.number = number;
        Log.i(TAG, "MainFragment: 参数");
    }

    public MainFragment() {
        Log.i(TAG, "MainFragment: 无");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null)
        {
            number = getArguments().getInt("title");
        }

        View view = inflater.inflate(R.layout.fragment, container, false);
        ((TextView) view.findViewById(R.id.text)).setText("" + number);
        ((TextView) view.findViewById(R.id.text)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 100);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
