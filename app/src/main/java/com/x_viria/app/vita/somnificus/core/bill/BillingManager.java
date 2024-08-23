package com.x_viria.app.vita.somnificus.core.bill;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;
import com.x_viria.app.vita.somnificus.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BillingManager {

    private final Activity ACTIVITY;
    private final BillingClient BILLING_CLIENT;

    private static final HashMap<String, List<String>> SKUS;
    static
    {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.ProductType.SUBS, Arrays.asList("somnificus.subs.premium"));
    }

    public BillingResponseListener billingResponseListener = null;

    public BillingManager(Activity activity) {
        this.ACTIVITY = activity;
        this.BILLING_CLIENT = BillingClient.newBuilder(ACTIVITY)
                .enablePendingPurchases()
                .setListener((billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        for (Purchase purchase : list) {
                            handlePurchase(purchase);
                        }
                        if (billingResponseListener != null) billingResponseListener.onPurchased();
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        if (billingResponseListener != null) billingResponseListener.onCancelled();
                    } else {
                        if (billingResponseListener != null) billingResponseListener.onError();
                    }
                })
                .build();
        BILLING_CLIENT.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.w("BillingManager", "Disconnected: onBillingServiceDisconnected()");
                BILLING_CLIENT.startConnection(this);
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i("BillingManager", "Connected: onBillingSetupFinished() response: 0");
                } else {
                    Log.w("BillingManager", "Connected: onBillingSetupFinished() error code: " + billingResult.getResponseCode());
                }
            }
        });
    }

    public List<String> getProducts(@BillingClient.ProductType String type) {
        return SKUS.get(type);
    }

    public void queryProductDetailsAsync(
            @BillingClient.ProductType final String itemType,
            final String productId,
            final ProductDetailsResponseListener listener
    ) {
        QueryProductDetailsParams productDetailsParams = QueryProductDetailsParams
                .newBuilder()
                .setProductList(
                        ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(productId)
                                .setProductType(itemType)
                                .build()
                ))
                .build();
        BILLING_CLIENT.queryProductDetailsAsync(
                productDetailsParams,
                listener
        );
    }

    public void queryPurchasesAsync(
            @BillingClient.ProductType final String itemType,
            final PurchasesResponseListener listener
    ) {
        QueryPurchasesParams purchasesParams = QueryPurchasesParams
                .newBuilder()
                .setProductType(itemType)
                .build();
        BILLING_CLIENT.queryPurchasesAsync(
                purchasesParams,
                listener
        );
    }

    public void queryPurchaseAsync(
            @BillingClient.ProductType final String itemType,
            final String productId,
            final PurchaseResponseListenser listener
    ) {
        queryPurchasesAsync(
                itemType,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            for (String product : purchase.getProducts()) {
                                if (
                                        product.equals(productId)
                                        && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                                        && purchase.isAcknowledged()
                                ) {
                                    listener.onQueryPurchaseResponse(billingResult, true);
                                    return;
                                }
                            }
                        }
                        listener.onQueryPurchaseResponse(billingResult, false);
                    } else {
                        listener.onQueryPurchaseResponse(billingResult, false);
                    }
                }
        );
    }

    public void launchBillingFlow(ProductDetails productDetails, ProductDetails.SubscriptionOfferDetails offerDetails) {
        BILLING_CLIENT.launchBillingFlow(
                ACTIVITY,
                BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                        ImmutableList.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .setOfferToken(offerDetails.getOfferToken())
                                        .build()
                ))
                .build());
    }

    public void release() {
        if (BILLING_CLIENT != null) BILLING_CLIENT.endConnection();
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                BILLING_CLIENT.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    }
                });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            Toast.makeText(ACTIVITY, R.string.activity_premium__toast_purchase_pending, Toast.LENGTH_LONG).show();
        }
    }

    public static class ProductId {

        public static final String SUBS_PREMIUM = "somnificus.subs.premium";

    }

    public interface BillingResponseListener {

        void onPurchased();
        void onCancelled();
        void onError();

    }

    public interface PurchaseResponseListenser {

        void onQueryPurchaseResponse(BillingResult billingResult, boolean purchased);

    }

}
