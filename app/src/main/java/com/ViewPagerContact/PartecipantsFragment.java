package com.ViewPagerContact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.styledmap.R;

import java.util.List;


public class PartecipantsFragment extends Fragment {

    private static final int STARTING_NUM_LIST_ITEMS = 100;

    private static final String EXTRA_ARGUMENTS_PAGE = "page";

    private int page;

    private RecyclerAdapter mAdapter;

    private RecyclerView mPartecipantsList;


    public PartecipantsFragment() {
        // Required empty public constructor
    }

    public static PartecipantsFragment newInstance(int page) {
        PartecipantsFragment fragment = new PartecipantsFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ARGUMENTS_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_ARGUMENTS_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_partecipants, container, false);
        mPartecipantsList = (RecyclerView) rootView.findViewById(R.id.rv_partecipants);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mPartecipantsList.setLayoutManager(layoutManager);
        mPartecipantsList.setHasFixedSize(false);

        List<Contacts> contacts = Contacts.createContactsList(STARTING_NUM_LIST_ITEMS);
        mAdapter = new RecyclerAdapter(contacts);
        mPartecipantsList.setAdapter(mAdapter);
        mPartecipantsList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration verticalDivider = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);

        mPartecipantsList.addItemDecoration(verticalDivider);

        // Inflate the layout for this fragment
        return rootView;
    }

}
