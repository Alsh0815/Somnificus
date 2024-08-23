package com.x_viria.app.vita.somnificus.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.x_viria.app.vita.somnificus.R;

public class NativeAds {

    private final Context CONTEXT;

    public NativeAds(Context context) {
        this.CONTEXT = context;
    }

    public void load(
            FrameLayout container,
            String adUnitId
    ) {
        AdLoader.Builder builder = new AdLoader.Builder(CONTEXT, adUnitId);
        builder.forNativeAd(nativeAd -> {
            View view = LayoutInflater.from(CONTEXT).inflate(R.layout.nativead_view, null);

            ((TextView) view.findViewById(R.id.nativead_headline)).setText(nativeAd.getHeadline());
            ((TextView) view.findViewById(R.id.nativead_body)).setText(nativeAd.getBody());
            ((TextView) view.findViewById(R.id.nativead_call_to_action)).setText(nativeAd.getCallToAction());

            NativeAd.Image icon = nativeAd.getIcon();
            ImageView iconView = view.findViewById(R.id.nativead_icon);
            if (icon == null) {
                iconView.setVisibility(View.INVISIBLE);
            } else {
                iconView.setImageDrawable(icon.getDrawable());
            }

            TextView priceView = view.findViewById(R.id.nativead_price);
            if (nativeAd.getPrice() == null) {
                priceView.setVisibility(View.INVISIBLE);
            } else {
                priceView.setText(nativeAd.getPrice());
            }

            TextView storeView = view.findViewById(R.id.nativead_store);
            if (nativeAd.getStore() == null) {
                storeView.setVisibility(View.INVISIBLE);
            } else {
                storeView.setText(nativeAd.getStore());
            }

            RatingBar ratingView = view.findViewById(R.id.nativead_stars);
            if (nativeAd.getStarRating() == null) {
                ratingView.setVisibility(View.INVISIBLE);
            } else {
                ratingView.setRating(nativeAd.getStarRating().floatValue());
            }

            TextView advView = view.findViewById(R.id.nativead_advertiser);
            if (nativeAd.getAdvertiser() == null) {
                advView.setVisibility(View.INVISIBLE);
            } else {
                advView.setText(nativeAd.getAdvertiser());
            }

            MediaView mediaView = view.findViewById(R.id.nativead_media);
            if (nativeAd.getMediaContent() == null) {
                mediaView.setVisibility(View.INVISIBLE);
            } else {
                mediaView.setMediaContent(nativeAd.getMediaContent());
            }

            NativeAdView adview = (NativeAdView) view;
            adview.setNativeAd(nativeAd);

            container.removeAllViews();
            container.addView(view);
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        builder.build().loadAd(adRequest);
    }

}
