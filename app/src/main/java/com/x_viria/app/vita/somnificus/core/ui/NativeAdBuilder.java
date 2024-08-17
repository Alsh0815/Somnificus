package com.x_viria.app.vita.somnificus.core.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.x_viria.app.vita.somnificus.R;

public class NativeAdBuilder {

    private final Activity ACTIVITY;
    private final Context CONTEXT;

    private AdLoader.Builder BUILDER;

    private @IdRes int ID_TARGET;

    public NativeAdBuilder(Activity activity, Context context) {
        this.ACTIVITY = activity;
        this.CONTEXT = context;
    }

    public void setLayout(@IdRes int target) {
        this.ID_TARGET = target;
    }

    public void create(String adUnitID, View adView) {
        BUILDER = new AdLoader.Builder(CONTEXT, adUnitID);
        BUILDER.forNativeAd(nativeAd -> {
            FrameLayout container = ACTIVITY.findViewById(ID_TARGET);

            ((TextView) adView.findViewById(R.id.nativead_headline)).setText(nativeAd.getHeadline());
            ((TextView) adView.findViewById(R.id.nativead_body)).setText(nativeAd.getBody());
            ((TextView) adView.findViewById(R.id.nativead_call_to_action)).setText(nativeAd.getCallToAction());

            NativeAd.Image icon = nativeAd.getIcon();
            ImageView iconView = adView.findViewById(R.id.nativead_icon);
            if (icon == null) {
                iconView.setVisibility(View.INVISIBLE);
            } else {
                iconView.setImageDrawable(icon.getDrawable());
                iconView.setVisibility(View.VISIBLE);
            }

            TextView priceView = adView.findViewById(R.id.nativead_price);
            if (nativeAd.getPrice() == null) {
                priceView.setVisibility(View.INVISIBLE);
            } else {
                priceView.setVisibility(View.VISIBLE);
                priceView.setText(nativeAd.getPrice());
            }

            TextView storeView = adView.findViewById(R.id.nativead_store);
            if (nativeAd.getStore() == null) {
                storeView.setVisibility(View.INVISIBLE);
            } else {
                storeView.setVisibility(View.VISIBLE);
                storeView.setText(nativeAd.getStore());
            }

            RatingBar ratingView = adView.findViewById(R.id.nativead_stars);
            if (nativeAd.getStarRating() == null) {
                ratingView.setVisibility(View.INVISIBLE);
            } else {
                ratingView.setRating(nativeAd.getStarRating().floatValue());
                ratingView.setVisibility(View.VISIBLE);
            }

            TextView advertiserView = adView.findViewById(R.id.nativead_advertiser);
            if (nativeAd.getAdvertiser() == null) {
                advertiserView.setVisibility(View.INVISIBLE);
            } else {
                advertiserView.setText(nativeAd.getAdvertiser());
                advertiserView.setVisibility(View.VISIBLE);
            }

            MediaView mediaView = adView.findViewById(R.id.nativead_media);
            mediaView.setMediaContent(nativeAd.getMediaContent());

            NativeAdView nativeAdView = (NativeAdView) adView;
            nativeAdView.setMediaView(mediaView);
            nativeAdView.setNativeAd(nativeAd);

            container.removeAllViews();
            container.addView(adView);
        });
    }

    public void loadAd() {
        AdLoader loader = BUILDER.build();
        loader.loadAd(new AdRequest.Builder().build());
    }

}
