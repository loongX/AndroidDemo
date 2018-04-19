package com.pxl.base.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pxl.common.ui.R;

/**
 * 提示控件
 * Created by Rao on 2015/10/17.
 */
public class TipView extends LinearLayout {

    private ImageView base_tips_view_img;
    private TextView base_tips_view_text;
    private Button base_tips_button;

    public TipView(Context context) {
        super(context);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public TipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.base_tip_view, this);
        base_tips_view_img = (ImageView) findViewById(R.id.base_tips_view_img);
        base_tips_view_text = (TextView) findViewById(R.id.base_tips_view_text);
        base_tips_button = (Button) findViewById(R.id.base_tips_button);
        showButton(false);
    }

    public TextView getTipTextView() {
        return base_tips_view_text;
    }

    public Button getButton() {
        return base_tips_button;
    }

    public void setTip(CharSequence tips) {
        setTip(tips, null);
    }

    public void setTip(CharSequence tips, Integer drawableId) {
        if (drawableId != null) {
            base_tips_view_img.setImageResource(drawableId.intValue());
        }
        base_tips_view_text.setText(tips);
    }

    public void setTipTextColor(int color) {
        base_tips_view_text.setTextColor(color);
    }

    public void setTipTextSize(int sp) {
        base_tips_view_text.setTextSize(sp);
    }

    public void setTipTextPaddingTop(int paddingTop) {
        base_tips_view_text.setPadding(0, paddingTop, 0, 0);
    }

    public void showButton(boolean show) {
        base_tips_button.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setButton(CharSequence btnText) {
        base_tips_button.setText(btnText);
    }


    public void showLoadingTip(CharSequence tips) {
        setTip(tips, R.drawable.default_loading_anim);
    }

    public void showLoadingTip() {
        setTip(getContext().getResources().getString(R.string.default_loadding_tip), R.drawable.default_loading_anim);
    }


}
