package com.ViewPagerContact;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.styledmap.R;


public class CondivisionPageFragment extends Fragment {

    private static final String EXTRA_ARGUMENTS_PAGE = "page";


    private int page;


    public CondivisionPageFragment() {
        // Required empty public constructor
    }

    public static CondivisionPageFragment newInstance(int page) {
        CondivisionPageFragment fragment = new CondivisionPageFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ARGUMENTS_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_ARGUMENTS_PAGE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_condivision_page, container, false);

        // Inflate the layout for this fragment
        return view;
    }

}
