package com.f0x1d.store.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.f0x1d.store.R;

public class CenteredToolbar extends Toolbar {
    private TextView tvSubtitle;
    private TextView tvTitle;

    public CenteredToolbar(Context context) {
        super(context);
        setupTextViews();
    }

    public CenteredToolbar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupTextViews();
    }

    public CenteredToolbar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupTextViews();
    }

    public CenteredToolbar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setupTextViews();
    }

    public CharSequence getTitle() {
        return tvTitle.getText().toString();
    }

    public void setTitle(int i) {
        setTitle(getResources().getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        tvTitle.setText(charSequence);
    }

    public CharSequence getSubtitle() {
        return tvSubtitle.getText().toString();
    }

    public void setSubtitle(int i) {
        setSubtitle(getResources().getString(i));
    }

    public void setSubtitle(CharSequence charSequence) {
        tvSubtitle.setVisibility(VISIBLE);
        tvSubtitle.setText(charSequence);
    }

    private void setupTextViews() {
        tvSubtitle = new TextView(getContext());
        tvTitle = new TextView(getContext());
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body2);
        LinearLayout linear = new LinearLayout(getContext());
        linear.setGravity(17);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.addView(tvTitle);
        linear.addView(tvSubtitle);
        tvSubtitle.setSingleLine();
        tvSubtitle.setEllipsize(TextUtils.TruncateAt.END);
        tvSubtitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Caption);
        tvSubtitle.setVisibility(GONE);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        linear.setLayoutParams(layoutParams);
        addView(linear);
    }
}
