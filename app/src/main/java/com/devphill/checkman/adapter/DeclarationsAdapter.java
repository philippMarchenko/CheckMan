package com.devphill.checkman.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devphill.checkman.DeclarationActivity;
import com.devphill.checkman.R;
import com.devphill.checkman.model.Declarations;

import java.util.List;


public class DeclarationsAdapter extends RecyclerView.Adapter<DeclarationsAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "DecrlarationsAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<Declarations.Item> declarationsList;

    IDeclarationsAdapterListener iDeclarationsAdapterListener;

    public interface IDeclarationsAdapterListener {
        public void onClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name,place_work,position;
        private View card_view;

        public MyViewHolder(View v) {
            super(v);
            this.name = (TextView) v.findViewById(R.id.name);
            this.place_work = (TextView) v.findViewById(R.id.place_work);
            this.position = (TextView) v.findViewById(R.id.position);
            this.card_view = v.findViewById(R.id.card_view_declarations);

        }
    }


    public DeclarationsAdapter(Context context, Activity activity, List<Declarations.Item> list,IDeclarationsAdapterListener iDeclarationsAdapterListener) {

        mContext = context;
        myActivity = activity;
        declarationsList = list;
        this.iDeclarationsAdapterListener = iDeclarationsAdapterListener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.declarations_card_view, viewGroup, false);
        return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final Declarations.Item declarations = declarationsList.get(position);

        viewHolder.name.setText(Html.fromHtml(declarations.getLastname() + " " + declarations.getFirstname()));
        viewHolder.place_work.setText(Html.fromHtml(declarations.getPlaceOfWork()));
        viewHolder.position.setText(Html.fromHtml(declarations.getPosition()));

        // define an on click listener to open PlaybackFragment
        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              iDeclarationsAdapterListener.onClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return declarationsList.size();
    }

}
