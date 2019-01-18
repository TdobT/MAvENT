package com.ViewPagerContact;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.styledmap.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.NumberViewHolder> {

    private List<Contacts> mContacts;

    private int selectedPosition = -1;

    private ActionMode mCab;


    public RecyclerAdapter(List<Contacts> contacts) {

        mContacts = contacts;
    }
    // notifiedItemChanged(position)

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        boolean shouldAttachToParentImmediately = false;
        LayoutInflater inflater;
        int layoutIdForListItem;

        if (viewType == selectedPosition) {

            layoutIdForListItem = R.layout.selected_number_list_item;
            inflater = LayoutInflater.from(context);

        } else {

            layoutIdForListItem = R.layout.number_list_item;
            inflater = LayoutInflater.from(context);
        }
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        if (!mContacts.get(viewType).getIsAdmin()) {
            View star = view.findViewById(R.id.iv_admin_star);
            if (star != null) star.setVisibility(View.INVISIBLE);
        }
        return new NumberViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {

        holder.bind(position);
    }


    @Override
    public int getItemCount() {

        return mContacts.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, ActionMode.Callback {

        ImageView contactPhoto;

        TextView contactName, contactNickname, contactEmail;

        int position;



        public NumberViewHolder(View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            contactNickname = (TextView) itemView.findViewById(R.id.tv_contact_nickname);
            contactEmail = (TextView) itemView.findViewById(R.id.tv_contact_email);
            contactPhoto = (ImageView) itemView.findViewById(R.id.contact_photo);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        void bind(int position) {
            Contacts contact = mContacts.get(position);

            // Set item views based on your views and data model
            contactName.setText(contact.getName());
            contactNickname.setText(contact.getNickname());
            contactEmail.setText(contact.getEmail());

            this.position = position;
        }

        @Override
        public void onClick(View v) {

            if (selectedPosition != -1) {

                if (selectedPosition == position) {
                    // Press to the already selected view: cancel the selection
                    v.setSelected(false);
                    mCab.finish();

                }
            } else {

                // Start the Signup activity
                Context c = v.getContext();
                Intent intent = new Intent(c.getApplicationContext(), ContactActivity.class);
                Bundle b = new Bundle();
                b.putParcelable(c.getString(R.string.contact_key_parcelable), mContacts.get(position));
                intent.putExtras(b);
                c.startActivity(intent);

            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (mCab != null) {
                // CAB already open, nothing to do
                return false;
            } else {

                if (selectedPosition == -1) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    mCab = activity.startSupportActionMode(this);

                    v.setSelected(true);

                    selectedPosition = position;
                    notifyItemChanged(position);
                    return true;
                }
                return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.event_partecipants_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            selectedPosition = -1;
            mCab = null;
            notifyItemChanged(position);
        }
    }
}
