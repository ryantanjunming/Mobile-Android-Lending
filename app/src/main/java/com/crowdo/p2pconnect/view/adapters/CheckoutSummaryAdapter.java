package com.crowdo.p2pconnect.view.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdo.p2pconnect.R;
import com.crowdo.p2pconnect.data.client.BiddingClient;
import com.crowdo.p2pconnect.helpers.ConstantVariables;
import com.crowdo.p2pconnect.helpers.HTTPResponseUtils;
import com.crowdo.p2pconnect.helpers.SnackBarUtil;
import com.crowdo.p2pconnect.model.core.Investment;
import com.crowdo.p2pconnect.model.core.Loan;
import com.crowdo.p2pconnect.model.response.DeleteBidResponse;
import com.crowdo.p2pconnect.oauth.AuthAccountUtils;
import com.crowdo.p2pconnect.view.activities.CheckoutActivity;
import com.crowdo.p2pconnect.viewholders.ItemCheckoutSummaryViewHolder;
import com.loopeer.itemtouchhelperextension.Extension;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CheckoutSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String LOG_TAG = CheckoutSummaryAdapter.class.getSimpleName();
    private List<Investment> biddingInvestmentList;
    private List<Loan> biddingLoanList;
    private Context mContext;
    private Disposable disposablePostDeleteBid;
    private RecyclerView mRecyclerView;
    private TextView mHeaderNoOfLoans;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public CheckoutSummaryAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.biddingInvestmentList = new ArrayList<>();
        this.biddingLoanList = new ArrayList<>();
        this.mRecyclerView = recyclerView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_checkout_summary, parent, false);
            return new HeaderCheckoutSummaryViewHolder(view);
        }else if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_checkout_summary, parent, false);

            ItemCheckoutSummaryViewHolder viewHolder = new ItemCheckoutSummaryViewHolder(view, mContext);
            viewHolder.initView();

            return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderCheckoutSummaryViewHolder){
            HeaderCheckoutSummaryViewHolder headerHolder = (HeaderCheckoutSummaryViewHolder) holder;
            Log.d(LOG_TAG, "APP Number of Pending Bids: " + biddingInvestmentList.size());

            mHeaderNoOfLoans = headerHolder.mHeaderCheckoutSummaryNoOfLoans;
            mHeaderNoOfLoans.setText(Integer.toString(biddingInvestmentList.size()));

        }else if(holder instanceof ItemCheckoutSummaryViewHolder){
            final int listPosition = holder.getAdapterPosition()-1;
            final ItemCheckoutSummaryViewHolder itemHolder = (ItemCheckoutSummaryViewHolder) holder;

            //taking note of header, thus position-1
            final Investment bidInvestmentItem = biddingInvestmentList.get(listPosition);
            final Loan bidLoanItem = biddingLoanList.get(listPosition);

            itemHolder.populateItemDetails(bidInvestmentItem, bidLoanItem);
            itemHolder.mItemDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDelete(listPosition, bidInvestmentItem, bidLoanItem);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        //investment list should be equal to loan list + header
        if(biddingInvestmentList.size() == 0 ){
            return 1;
        }
        if(biddingInvestmentList == null){
            return 0;
        }
        return biddingInvestmentList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)){
            return TYPE_HEADER;
        }else {
            return TYPE_ITEM;
        }
    }

    private void doDelete(final int position, final Investment bidInvestmentItem,
                          final Loan bidLoanItem){

        //do check here
        BiddingClient.getInstance(mContext)
                .postDeleteBid(bidInvestmentItem.getId(),
                        ConstantVariables.getUniqueAndroidID(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<DeleteBidResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposablePostDeleteBid = d;
                    }

                    @Override
                    public void onNext(@NonNull Response<DeleteBidResponse> response) {
                        if(response.isSuccessful()){
                            DeleteBidResponse deleteBidResponse = response.body();
                            if(mRecyclerView !=null) {
                                SnackBarUtil.snackBarForInfoCreate(mRecyclerView,
                                        deleteBidResponse.getServer().getMessage(),
                                        Snackbar.LENGTH_SHORT).show();
                            }

                        }else{
                            Log.d(LOG_TAG, "APP getCheckoutSummary onNext() status > " + response.code());
                            if(HTTPResponseUtils.check4xxClientError(response.code())){
                                if(ConstantVariables.HTTP_UNAUTHORISED == response.code()){
                                    AuthAccountUtils.actionLogout((CheckoutActivity) mContext);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "ERROR: " + e.getMessage(), e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG, "APP postDeleteBid Rx onComplete");
                        biddingInvestmentList.remove(bidInvestmentItem);
                        biddingLoanList.remove(bidLoanItem);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, biddingInvestmentList.size()+1);
                    }
                });
    }



    public void setBiddingInvestmentsAndLoans(@Nullable List<Investment> investments,
                                              @Nullable List<Loan> loans){
        if(investments == null || loans == null){
            return; //must not be null
        }else if(investments.size() != loans.size()){
            return; //must be equal in size
        }

        biddingInvestmentList.clear();
        biddingLoanList.clear();
        biddingInvestmentList.addAll(investments);
        biddingLoanList.addAll(loans);
        notifyDataSetChanged();
    }

    private boolean isPositionHeader (int position){
        return position == 0;
    }


    public void removeDisposablePostDeleteBid() {
        if(disposablePostDeleteBid != null){
            disposablePostDeleteBid.dispose();
        }
    }

    class HeaderCheckoutSummaryViewHolder extends RecyclerView.ViewHolder{
        @Nullable @BindView(R.id.checkout_summary_no_of_loans_value)
        TextView mHeaderCheckoutSummaryNoOfLoans;

        @Nullable @BindView(R.id.checkout_summary_no_of_loans_icon)
        ImageView mSummaryCartNoOfLoansIcon;

        @Nullable @BindView(R.id.checkout_summary_swipe_delete_info_icon)
        ImageView mSummaryCartSwipeToDeleteIcon;

        HeaderCheckoutSummaryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mSummaryCartNoOfLoansIcon.setImageDrawable(
                    new IconicsDrawable(mContext)
                            .icon(CommunityMaterial.Icon.cmd_cash_multiple)
                            .colorRes(R.color.color_secondary_text)
                            .sizeRes(R.dimen.checkout_summary_cart_icon_size));


            mSummaryCartSwipeToDeleteIcon.setImageDrawable(
                    new IconicsDrawable(mContext)
                            .icon(CommunityMaterial.Icon.cmd_chevron_double_left)
                            .colorRes(R.color.color_secondary_text)
                            .sizeRes(R.dimen.item_checkout_summary_cart_swipe_info_icon_size));
        }
    }

}
