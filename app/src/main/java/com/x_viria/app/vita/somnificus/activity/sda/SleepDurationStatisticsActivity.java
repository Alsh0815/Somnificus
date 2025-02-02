package com.x_viria.app.vita.somnificus.activity.sda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.ads.NativeAds;
import com.x_viria.app.vita.somnificus.core.ads.NativeAdsSDS;
import com.x_viria.app.vita.somnificus.core.bill.BillingManager;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.core.ui.view.CircularScoreGraphView;
import com.x_viria.app.vita.somnificus.util.Theme;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SleepDurationStatisticsActivity extends AppCompatActivity {

    private static final long CONST__MIN_SLEEP_TIME_REQ = 5 * 60 * 60 * 1000;
    private boolean IS_PREMIUM = false;

    private Map<String, Statistics> getDailySleep(List<SleepDurationInfo> sleepSessions) {
        Map<String, Statistics> dailySleepMap = new HashMap<>();

        for (SleepDurationInfo session : sleepSessions) {
            long wakeupTime = session.getWakeupTime();
            long bedTime = session.getBedTime();
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getDefault());
            String dateKey = sdf.format(new Date(wakeupTime));
            long sleepDuration = wakeupTime - bedTime;

            Statistics statistics = dailySleepMap.getOrDefault(dateKey, new Statistics());
            statistics.totalSleepTime += sleepDuration;
            statistics.sessions++;
            if (session.EVAL__GOOD_OR_BAD == SleepDurationInfo.Eval.BAD) statistics.eval_bad++;
            dailySleepMap.put(dateKey, statistics);
        }

        return dailySleepMap;
    }

    private Result getResult(List<SleepDurationInfo> sdlist) {
        int cnt = 0;
        int totalSessions = 0;
        long totalSleepTime = 0;
        int bad_sessions = 0;
        int short_long_sessions = 0;
        Map<String, Statistics> map = getDailySleep(sdlist);
        for (Map.Entry<String, Statistics> entry : map.entrySet()) {
            totalSessions += entry.getValue().sessions;
            totalSleepTime += entry.getValue().totalSleepTime;
            if (0 < entry.getValue().eval_bad) {
                bad_sessions += entry.getValue().eval_bad;
            }
            if (totalSleepTime < 6 * 60 * 60 * 1000 || 9 * 60 * 60 * 1000 < totalSleepTime) {
                short_long_sessions++;
            }
            cnt++;
        }
        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(totalSleepTime);
        long avgSleepTime = totalSleepTime / Math.max(cnt, 1);
        long ast_minutes = TimeUnit.MILLISECONDS.toMinutes(avgSleepTime);
        float score_ast = Math.min(((float) avgSleepTime) / ((float) (8 * 60 * 60 * 1000)), 1.0f);
        float score_tsn = 1.0f - (Math.abs(totalSessions - (1.4f * cnt)) * 0.1f);
        score_tsn = Math.max(0.0f, Math.min(score_tsn, 1.0f));
        float score = score_ast * 80.0f + score_tsn * 20.0f - (bad_sessions * 5.0f + short_long_sessions * 3.0f);
        score = Math.min(score, 100.0f);
        if (cnt == 0) score = 0.0f;

        Result res = new Result();
        res.ast_minutes = ast_minutes;
        res.low_rated_sessions = bad_sessions;
        res.score = score;
        res.total_minutes = totalMinutes;
        res.total_sessions = totalSessions;
        return res;
    }

    private void process() throws JSONException {
        SleepDurationManager sdm = new SleepDurationManager(this);
        List<SleepDurationInfo> sdlist_7days = sdm.get(SleepDurationManager.Period.LAST_7DAYS);
        List<SleepDurationInfo> sdlist_mr = sdm.get(SleepDurationManager.Period.MONTH, 0);
        List<SleepDurationInfo> sdlist_mr_mom = sdm.get(SleepDurationManager.Period.MONTH, 1);

        Result rs_7days = getResult(sdlist_7days);
        long hours = rs_7days.total_minutes / 60;
        long minutes = rs_7days.total_minutes % 60;
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__ScoreCard_TST_H)).setText(String.valueOf(hours));
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__ScoreCard_TST_M)).setText(String.valueOf(minutes));
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__ScoreCard_TST_Score)).setText(String.valueOf((int) rs_7days.score));
        ((CircularScoreGraphView) findViewById(R.id.SleepDurationStatisticsActivity__7D_CSGV)).setScore((int) rs_7days.score);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__7D_AST)).setText(
                String.format(
                        getString(R.string.activity_sleep_duration_details__text_sleep_duration_format_hm),
                        rs_7days.ast_minutes / 60,
                        rs_7days.ast_minutes % 60
                )
        );
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__7D_Total_Sessions)).setText(String.valueOf(rs_7days.total_sessions));

        Result rs_mr = getResult(sdlist_mr);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_Score)).setText(
                String.format(
                        "%d pts",
                        (int) rs_mr.score
                )
        );
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_AST)).setText(
                String.format(
                        getString(R.string.activity_sleep_duration_details__text_sleep_duration_format_hm),
                        rs_mr.ast_minutes / 60,
                        rs_mr.ast_minutes % 60
                )
        );
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_LRS)).setText(
                String.format(
                        "%d",
                        rs_mr.low_rated_sessions
                )
        );

        Result rs_mr_mom = getResult(sdlist_mr_mom);
        TextView tv_mr_score_mom = (TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_Score_MoM);
        tv_mr_score_mom.setText(
                String.format(
                        "%+d",
                        (int) (rs_mr.score - rs_mr_mom.score)
                )
        );
        if (rs_mr.score - rs_mr_mom.score < 0) {
            tv_mr_score_mom.setTextColor(Theme.getColor(this, R.attr.compareNegative));
        } else {
            tv_mr_score_mom.setTextColor(Theme.getColor(this, R.attr.comparePositive));
        }
        TextView tv_mr_ast_mom = (TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_AST_MoM);
        tv_mr_ast_mom.setText(
                String.format(
                        "%+d" + getString(R.string.common__text_unit_min),
                        rs_mr.ast_minutes - rs_mr_mom.ast_minutes
                )
        );
        if (rs_mr.ast_minutes - rs_mr_mom.ast_minutes < 0.0f) {
            tv_mr_ast_mom.setTextColor(Theme.getColor(this, R.attr.compareNegative));
        } else {
            tv_mr_ast_mom.setTextColor(Theme.getColor(this, R.attr.comparePositive));
        }
        TextView tv_mr_lrs_mom = (TextView) findViewById(R.id.SleepDurationStatisticsActivity__MR_LRS_MoM);
        tv_mr_lrs_mom.setText(
                String.format(
                        "%+d",
                        rs_mr.low_rated_sessions - rs_mr_mom.low_rated_sessions
                )
        );
        if (rs_mr.low_rated_sessions - rs_mr_mom.low_rated_sessions >= 0.0f) {
            tv_mr_lrs_mom.setTextColor(Theme.getColor(this, R.attr.compareNegative));
        } else {
            tv_mr_lrs_mom.setTextColor(Theme.getColor(this, R.attr.comparePositive));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_duration_statistics);

        ImageView IV_Btn__Back = findViewById(R.id.SleepDurationStatisticsActivity__Back_Btn);
        IV_Btn__Back.setOnClickListener(v -> finish());

        CardView nativeAd1 = findViewById(R.id.SleepDurationStatisticsActivity__NativeAd_1);
        FrameLayout nativeAd1_fl = findViewById(R.id.SleepDurationStatisticsActivity__NativeAd_Container);
        nativeAd1.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            try {
                process();
                if (!IS_PREMIUM) {
                    nativeAd1.setVisibility(View.VISIBLE);
                    NativeAdsSDS nativeAds = new NativeAdsSDS(SleepDurationStatisticsActivity.this);
                    nativeAds.load(nativeAd1_fl, getString(R.string.ads_id_native_sds_1));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, 1000);

        IS_PREMIUM = false;
        BillingManager billingManager = new BillingManager(this);
        new Thread(() -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            billingManager.queryPurchaseAsync(
                    BillingClient.ProductType.SUBS,
                    BillingManager.ProductId.SUBS_PREMIUM,
                    (billingResult, purchased) -> IS_PREMIUM = purchased
            );
        }).start();
    }

    class Statistics {
        public long totalSleepTime = 0;
        public int sessions = 0;
        public int eval_bad = 0;
    }

    class Result {
        public float score = 0.0f;
        public long total_minutes = 0;
        public int total_sessions = 0;
        public long ast_minutes = 0;
        public int low_rated_sessions = 0;
    }

}