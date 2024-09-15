package com.x_viria.app.vita.somnificus.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WhatsNewActivity extends AppCompatActivity {

    private String _load() throws IOException {
        InputStream inputStream = getAssets().open("json/whats_new.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }

    private WhatsNewInfo _content(JSONArray content) throws JSONException {
        WhatsNewInfo wni = new WhatsNewInfo();

        for (int i = 0; i < content.length(); i++) {
            JSONObject obj = content.getJSONObject(i);
            if (!obj.has("type")) continue;
            String type = obj.getString("type");
            if (!obj.has("text")) continue;
            switch (type) {
                case "ft":
                    wni.WNI_FT.add(obj.getJSONObject("text"));
                    break;
                case "ch":
                    wni.WNI_CH.add(obj.getJSONObject("text"));
                    break;
                case "fx":
                    wni.WNI_FX.add(obj.getJSONObject("text"));
                    break;
                default:
                    break;
            }
        }

        return wni;
    }

    private TextView _typeTV(@StringRes int resId) {
        ViewGroup.MarginLayoutParams tv_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_lp.setMarginStart(Unit.dp2px(this, 8));
        tv_lp.topMargin = Unit.dp2px(this, 16);
        TextView tv = new TextView(this);
        tv.setLayoutParams(tv_lp);
        tv.setText(resId);
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private LinearLayout _child(JSONObject object, Locale locale) throws JSONException {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setPaddingRelative(
                Unit.dp2px(this, 16),
                Unit.dp2px(this, 8),
                ll.getPaddingEnd(),
                Unit.dp2px(this, 8)
        );

        String text;
        if (object.has(locale.getLanguage())) {
            text = object.getString(locale.getLanguage());
        } else {
            text = object.getString("en");
        }

        TextView _t = new TextView(this);
        _t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _t.setText(" - ");
        ll.addView(_t);

        TextView _text = new TextView(this);
        _text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _text.setText(text);
        ll.addView(_text);

        return ll;
    }

    private LinearLayout _layout(JSONObject version) throws JSONException {
        Configuration config = getResources().getConfiguration();
        Locale locale = config.getLocales().get(0);

        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);

        if (!version.has("version")) return null;
        JSONArray _ver = version.getJSONArray("version");

        ViewGroup.MarginLayoutParams tv_version_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_version_lp.setMarginStart(Unit.dp2px(this, 8));
        tv_version_lp.topMargin = Unit.dp2px(this, 8);
        TextView tv_version = new TextView(this);
        tv_version.setLayoutParams(tv_version_lp);
        tv_version.setText(
                String.format(
                        "ver.%d.%d.%d.x",
                        _ver.getInt(0), _ver.getInt(1), _ver.getInt(2)
                )
        );
        tv_version.setTextSize(24);
        tv_version.setTypeface(null, Typeface.BOLD);
        parent.addView(tv_version);

        if (!version.has("content")) return null;
        WhatsNewInfo wni = _content(version.getJSONArray("content"));

        TextView tv_ft = _typeTV(R.string.activity_whats_new__text_features);
        TextView tv_ch = _typeTV(R.string.activity_whats_new__text_changes);
        TextView tv_fx = _typeTV(R.string.activity_whats_new__text_fixed);

        if (0 < wni.WNI_FT.size()) parent.addView(tv_ft);
        for (int i = 0; i < wni.WNI_FT.size(); i++) {
            parent.addView(_child(wni.WNI_FT.get(i), locale));
        }

        if (0 < wni.WNI_CH.size()) parent.addView(tv_ch);
        for (int i = 0; i < wni.WNI_CH.size(); i++) {
            parent.addView(_child(wni.WNI_CH.get(i), locale));
        }

        if (0 < wni.WNI_FX.size()) parent.addView(tv_fx);
        for (int i = 0; i < wni.WNI_FX.size(); i++) {
            parent.addView(_child(wni.WNI_FX.get(i), locale));
        }

        return parent;
    }

    private void loadWhatsNew() throws IOException, JSONException {
        JSONObject json = new JSONObject(_load());

        if (!json.has("whats_new")) return;

        List<LinearLayout> layout_list = new ArrayList<>();
        JSONArray versions = json.getJSONArray("whats_new");
        for (int i = 0; i < versions.length(); i++) {
            layout_list.add(_layout(versions.getJSONObject(i)));
        }

        ((LinearLayout) findViewById(R.id.WhatsNewActivity__Latest)).addView(layout_list.get(0));
        LinearLayout list = findViewById(R.id.WhatsNewActivity__UPD);
        for (int j = 1; j < layout_list.size(); j++) {
            list.addView(layout_list.get(j));
            ViewGroup.MarginLayoutParams v_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            v_lp.setMargins(Unit.dp2px(this, 4), Unit.dp2px(this, 4), Unit.dp2px(this, 4), Unit.dp2px(this, 4));
            View v = new View(this);
            v.setBackgroundColor(getColor(R.color.primaryHorizontalBar));
            v.setLayoutParams(v_lp);
            list.addView(v);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);

        ImageView IV__UPD_OpenClose = findViewById(R.id.WhatsNewActivity__UPD_OpenClose_IV);
        LinearLayout LL_Btn__UPD_OpenClose = findViewById(R.id.WhatsNewActivity__UPD_OpenClose);
        LL_Btn__UPD_OpenClose.setOnClickListener(v -> {
            LinearLayout LL__UPD = findViewById(R.id.WhatsNewActivity__UPD);
            int visibility = LL__UPD.getVisibility();
            switch (visibility) {
                case GONE:
                    IV__UPD_OpenClose.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_keyboard_arrow_up));
                    LL__UPD.setVisibility(VISIBLE);
                    break;
                case VISIBLE:
                    IV__UPD_OpenClose.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_keyboard_arrow_down));
                    LL__UPD.setVisibility(GONE);
                    break;
                case android.view.View.INVISIBLE:
                    break;
            }
        });

        new Handler().post(() -> {
            try {
                loadWhatsNew();
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class WhatsNewInfo {

        public List<JSONObject> WNI_FT = new ArrayList<>();
        public List<JSONObject> WNI_CH = new ArrayList<>();
        public List<JSONObject> WNI_FX = new ArrayList<>();

    }
}