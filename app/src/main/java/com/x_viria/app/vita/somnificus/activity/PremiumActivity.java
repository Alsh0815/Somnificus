package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.bill.BillingManager;
import com.x_viria.app.vita.somnificus.util.Theme;
import com.x_viria.app.vita.somnificus.util.Unit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremiumActivity extends AppCompatActivity {

    private BillingManager billingManager;
    private List<ProductDetails> productDetailsList;

    private List<Purchase> productInfoList;
    private int productInfoResponseCode = 0xFFFF;

    private final long refreshRate = 2000;

    private void refreshOffersList() {
        if (productDetailsList == null) {
            new Handler().postDelayed(this::refreshOffersList, refreshRate);
            return;
        }
        Map<String, Integer> plan_name = new HashMap<>();
        plan_name.put("P1Y", R.string.subs_plan__yearly);
        plan_name.put("P1M", R.string.subs_plan__monthly);
        plan_name.put("P1W", R.string.subs_plan__7days);

        final int dp_2 = Unit.Pixel.dp2px(this, 2);
        final int dp_4 = Unit.Pixel.dp2px(this, 4);
        final int dp_8 = Unit.Pixel.dp2px(this, 8);
        final int dp_24 = Unit.Pixel.dp2px(this, 24);

        LinearLayout list = findViewById(R.id.PremiumActivity__Item);
        list.removeAllViews();

        for (ProductDetails productDetails : productDetailsList) {
            List<ProductDetails.SubscriptionOfferDetails> offerDetailsList = productDetails.getSubscriptionOfferDetails();
            if (offerDetailsList == null) continue;
            for (ProductDetails.SubscriptionOfferDetails offerDetails : offerDetailsList) {
                LinearLayout.LayoutParams item_lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                item_lp.setMargins(dp_8, dp_8, dp_8, dp_8);
                LinearLayout item = new LinearLayout(this);
                item.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_offer_card));
                item.setElevation(Unit.Pixel.dp2px(this, 4));
                item.setGravity(Gravity.CENTER);
                item.setLayoutParams(item_lp);
                item.setOrientation(LinearLayout.VERTICAL);
                item.setPadding(dp_24, dp_24, dp_24, dp_24);

                TextView title = new TextView(this);
                title.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                title.setTextSize(22);
                item.addView(title);

                TextView tv_price_max = new TextView(this);
                tv_price_max.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                tv_price_max.setPadding(dp_2, dp_2, dp_2, dp_2);
                tv_price_max.setTextColor(Theme.getColor(this, R.attr.secondaryTextColor));
                tv_price_max.setTextSize(16);
                tv_price_max.setVisibility(View.INVISIBLE);
                TextPaint tp_price_max = tv_price_max.getPaint();
                tp_price_max.setFlags(tv_price_max.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tp_price_max.setAntiAlias(true);
                item.addView(tv_price_max);

                TextView tv_price_f = new TextView(this);
                tv_price_f.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                tv_price_f.setPadding(dp_2, dp_2, dp_2, dp_2);
                tv_price_f.setTextSize(18);
                item.addView(tv_price_f);

                int iconTintColor = Theme.getColor(this, R.attr.primaryBackgroundColor);

                LinearLayout ll_btn = new LinearLayout(this);
                ll_btn.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_offer_card_btn));
                ll_btn.setClickable(true);
                ll_btn.setElevation(Unit.Pixel.dp2px(this, 4));
                ll_btn.setGravity(Gravity.CENTER);
                ll_btn.setOnClickListener(v -> billingManager.launchBillingFlow(productDetails, offerDetails));
                ll_btn.setOrientation(LinearLayout.HORIZONTAL);
                ll_btn.setPadding(dp_8, dp_8, dp_8, dp_8);
                ImageView iv_btn_icon = new ImageView(this);
                iv_btn_icon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_shopping_bag));
                iv_btn_icon.setImageTintList(ColorStateList.valueOf(iconTintColor));
                iv_btn_icon.setLayoutParams(new LinearLayout.LayoutParams(
                        dp_24,
                        dp_24
                ));
                iv_btn_icon.setPadding(dp_4, dp_4, dp_4, dp_4);
                ll_btn.addView(iv_btn_icon);
                TextView tv_btn_text = new TextView(this);
                tv_btn_text.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                tv_btn_text.setText(R.string.activity_premium__text_subscribe);
                tv_btn_text.setTextColor(Theme.getColor(this, R.attr.primaryBackgroundColor));
                tv_btn_text.setTextSize(16);
                ll_btn.addView(tv_btn_text);
                item.addView(ll_btn);

                long min_price = Long.MAX_VALUE;
                long max_price = Long.MIN_VALUE;

                if (offerDetails.getOfferId() != null) {
                    tv_price_max.setVisibility(View.VISIBLE);
                }

                List<ProductDetails.PricingPhase> pricingPhaseList = offerDetails.getPricingPhases().getPricingPhaseList();
                for (ProductDetails.PricingPhase pricingPhase : pricingPhaseList) {
                    if (max_price < pricingPhase.getPriceAmountMicros()) {
                        max_price = pricingPhase.getPriceAmountMicros();
                        tv_price_max.setText(pricingPhase.getFormattedPrice());
                    }
                    if (pricingPhase.getPriceAmountMicros() < min_price) {
                        min_price = pricingPhase.getPriceAmountMicros();
                        tv_price_f.setText(pricingPhase.getFormattedPrice());
                        title.setText(
                                String.format(
                                        "%s (%s)",
                                        productDetails.getName(),
                                        getString(plan_name.get(pricingPhase.getBillingPeriod()))
                                )
                        );
                    }
                }

                list.addView(item);
            }
        }
    }

    private void refreshPurchasedList() {
        if (productInfoResponseCode == 0xFFFF) {
            new Handler().postDelayed(this::refreshPurchasedList, refreshRate);
            return;
        }

        final int dp_2 = Unit.Pixel.dp2px(this, 2);
        final int dp_4 = Unit.Pixel.dp2px(this, 4);
        final int dp_8 = Unit.Pixel.dp2px(this, 8);

        LinearLayout parent = findViewById(R.id.PremiumActivity__Purchased);
        parent.removeAllViews();

        if (productInfoResponseCode == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : productInfoList) {
                if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) continue;
                for (String product : purchase.getProducts()) {
                    LinearLayout.LayoutParams card_lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    card_lp.setMargins(dp_4, dp_4, dp_4, dp_4);
                    LinearLayout card = new LinearLayout(this);
                    card.setBackground(AppCompatResources.getDrawable(this, R.drawable.bg_purchased_card));
                    card.setLayoutParams(card_lp);
                    card.setOrientation(LinearLayout.HORIZONTAL);
                    card.setPadding(dp_8, dp_8, dp_8, dp_8);

                    LinearLayout th = new LinearLayout(this);
                    th.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    th.setOrientation(LinearLayout.VERTICAL);
                    th.setPadding(dp_2, dp_2, dp_2, dp_2);
                    card.addView(th);

                    TextView th_product = new TextView(this);
                    th_product.setText(R.string.activity_premium__text_product_id);
                    th_product.setTextSize(14);
                    th.addView(th_product);

                    TextView th_order = new TextView(this);
                    th_order.setText(R.string.activity_premium__text_order_id);
                    th_order.setTextSize(14);
                    th.addView(th_order);

                    TextView th_time = new TextView(this);
                    th_time.setText(R.string.activity_premium__text_time);
                    th_time.setTextSize(14);
                    th.addView(th_time);

                    TextView th_status = new TextView(this);
                    th_status.setText(R.string.activity_premium__text_status);
                    th_status.setTextSize(14);
                    th.addView(th_status);

                    LinearLayout td = new LinearLayout(this);
                    td.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    td.setOrientation(LinearLayout.VERTICAL);
                    td.setPadding(dp_2, dp_2, dp_2, dp_2);
                    card.addView(td);

                    TextView td_product = new TextView(this);
                    td_product.setText(String.format(": %s", product));
                    td_product.setTextSize(14);
                    td.addView(td_product);

                    TextView td_order = new TextView(this);
                    td_order.setText(String.format(": %s", purchase.getOrderId()));
                    td_order.setTextSize(14);
                    td.addView(td_order);

                    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.activity_premium__text_time_format));
                    TextView td_time = new TextView(this);
                    td_time.setText(String.format(": %s", sdf.format(new Date(purchase.getPurchaseTime()))));
                    td_time.setTextSize(14);
                    td.addView(td_time);

                    String status = getString(R.string.activity_premium__text_status_not_ack);
                    if (purchase.isAcknowledged()) status = getString(R.string.activity_premium__text_status_ok);
                    if (!purchase.isAutoRenewing()) status = getString(R.string.activity_premium__text_status_cancelled);
                    TextView td_status = new TextView(this);
                    td_status.setText(String.format(": %s", status));
                    td_status.setTextSize(14);
                    td.addView(td_status);

                    parent.addView(card);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        billingManager = new BillingManager(this);
        billingManager.billingResponseListener = new BillingManager.BillingResponseListener() {
            @Override
            public void onPurchased() {
                Toast.makeText(PremiumActivity.this, R.string.activity_premium__toast_purchase_completed,Toast.LENGTH_LONG).show();
                productInfoList = null;
                productInfoResponseCode = 0xFFFF;
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (billingManager == null) return;
                    billingManager.queryPurchasesAsync(
                            BillingClient.ProductType.SUBS,
                            (billingResult, list) -> {
                                productInfoResponseCode = billingResult.getResponseCode();
                                productInfoList = list;
                            }
                    );
                }).start();
                new Handler().postDelayed(() -> refreshPurchasedList(), refreshRate);
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onError() {
            }
        };

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (billingManager == null) return;
            List<String> products = billingManager.getProducts(BillingClient.ProductType.SUBS);
            billingManager.queryProductDetailsAsync(
                    BillingClient.ProductType.SUBS,
                    products.get(0),
                    (billingResult, list) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            productDetailsList = list;
                        }
                    }
            );
            billingManager.queryPurchasesAsync(
                    BillingClient.ProductType.SUBS,
                    (billingResult, list) -> {
                        productInfoResponseCode = billingResult.getResponseCode();
                        productInfoList = list;
                    }
            );
        }).start();

        new Handler().postDelayed(() -> {
            refreshOffersList();
            refreshPurchasedList();
        }, refreshRate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingManager != null) billingManager.release();
    }
}