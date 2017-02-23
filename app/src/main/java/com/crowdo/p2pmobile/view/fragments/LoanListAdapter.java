package com.crowdo.p2pmobile.view.fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crowdo.p2pmobile.R;
import com.crowdo.p2pmobile.model.LoanListItem;
import com.crowdo.p2pmobile.viewholders.LoanListViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by cwdsg05 on 15/11/16.
 */

public class LoanListAdapter extends BaseAdapter{

    private final String LOG_TAG = LoanListAdapter.class.getSimpleName();

    private Context mContext;
    private List<LoanListItem> mLoanList;
    private List<LoanListItem> mFilteredList;

    private String searchQuery;
    private List<String> gradesToFilter;
    private List<Integer> termsToFilter;
    private List<String> securityToFilter;

    private TextView filteringCountTextView;
    private String filteringCountTextViewTail;

    public LoanListAdapter(Context context) {
        super();
        this.mContext = context;
        this.searchQuery = new String("");
        this.gradesToFilter = new ArrayList<String>();
        this.termsToFilter = new ArrayList<Integer>();
        this.securityToFilter = new ArrayList<String>();
        this.mFilteredList = new ArrayList<LoanListItem>();
        this.mLoanList = new ArrayList<LoanListItem>();
    }

    @Override
    public int getCount() {
        return mFilteredList.size();
    }

    @Override
    public LoanListItem getItem(int position) {
        if(position < 0 || position >= mFilteredList.size()){
            return null;
        }else{
            return mFilteredList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contextView, ViewGroup parent) {
        final View view = (contextView != null ? contextView : createView(parent));
        final LoanListViewHolder viewHolder = (LoanListViewHolder) view.getTag();
        viewHolder.attachLoanItem(getItem(position), mContext);
        return view;
    }

    private View createView(ViewGroup parent){
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_list_loan, parent, false);
        final LoanListViewHolder loanListViewHolder = new LoanListViewHolder(mContext, view);
        view.setTag(loanListViewHolder);
        return view;
    }

    public void setLoans(@Nullable List<LoanListItem> retreivedLoans){
        if(retreivedLoans == null){
            return;
        }
        mLoanList.clear();
        mFilteredList.clear();
        mLoanList.addAll(retreivedLoans);
        mFilteredList.addAll(retreivedLoans);
        notifyDataSetChanged();
        if(filteringCountTextView != null){
            filteringCountTextView.setText(Integer.toString(getCount())
                    + filteringCountTextViewTail);
        }
        searchLoans();
    }

    public void setFilteringCountTextView(TextView filteringCountTextView, String filteringCountTextViewTail) {
        this.filteringCountTextView = filteringCountTextView;
        this.filteringCountTextViewTail = filteringCountTextViewTail;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<String> getGradesToFilter() {
        return gradesToFilter;
    }

    public void setGradesToFilter(List<String> gradesToFilter) {
        this.gradesToFilter = gradesToFilter;
    }

    public List<Integer> getTermsToFilter() {
        return termsToFilter;
    }

    public void setTermsToFilter(List<Integer> termsToFilter) {
        this.termsToFilter = termsToFilter;
    }

    public List<String> getSecurityToFilter() {
        return securityToFilter;
    }

    public void setSecurityToFilter(List<String> securityToFilter) {
        this.securityToFilter = securityToFilter;
    }

    public void searchLoans(){

        if(this.searchQuery == null || this.gradesToFilter == null || this.termsToFilter == null
                || this.securityToFilter == null){
            //if any input null, do nothing
            return;
        }

        if(this.searchQuery.isEmpty() && this.gradesToFilter.isEmpty() && this.termsToFilter.isEmpty()
                && this.securityToFilter.isEmpty()){
            mFilteredList.clear();
            mFilteredList.addAll(mLoanList);
            notifyDataSetChanged();
            return;
        }else{
            mFilteredList.clear();
            mFilteredList.addAll(mLoanList);
            Iterator<LoanListItem> llit = mFilteredList.iterator();
            //fill and pick-off method

            while(llit.hasNext()){
                LoanListItem item = llit.next();

                if(!"".contains(this.searchQuery)){
                    if(!item.loanId.toLowerCase().trim().contains(this.searchQuery.toLowerCase().trim())) {
                        llit.remove();
                        continue;
                    }
                }

                if(!gradesToFilter.isEmpty()){
                    if(!gradesToFilter.contains(item.grade)){
                        llit.remove();
                        continue;
                    }
                }

                if (!termsToFilter.isEmpty()){
                    if(!termsToFilter.contains(item.tenure)){
                        llit.remove();
                        continue;
                    }
                }

                if(!securityToFilter.isEmpty()){
                    if(!securityToFilter.contains(item.security)){
                        llit.remove();
                        continue;
                    }
                }
            }

            if(filteringCountTextView != null){
                filteringCountTextView.setText(Integer.toString(getCount()) + filteringCountTextViewTail);
            }
            notifyDataSetChanged();
        }
    }

}
