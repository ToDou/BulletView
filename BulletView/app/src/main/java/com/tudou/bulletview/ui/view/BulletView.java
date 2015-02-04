package com.tudou.bulletview.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tudou.bulletview.drawable.StateRoundRectDrawable;
import com.tudou.bulletview.model.Comment;
import com.tudou.bulletview.util.DrawableUtils;

import java.util.ArrayList;

/**
 * Created by tudou on 15-2-3.
 */
public class BulletView extends LinearLayout {
    private final int MAX_BULLET_VIEW_HEIGHT = 200;
    private final int MAX_BULLET_VIEW_WIDTH = 100;
    private final int DEFAULT_TAG_PADDING = 12;
    private final int DEFAULT_TAG_MARGIN = 12;
    private final int DEFAULT_TAG_PADDING_TOP = 3;
    private final int DEFAULT_LAYOUT_MARGIN_TOP = 12;
    private final int DEFAULT_TAG_HEIGHT = 28;

    private ArrayList<Comment> comments;
    private ArrayList<Comment> commentsShow = new ArrayList<>();
    private LinearLayout mLayoutItem;
    private Context mContext;
    private int tempWidth = 0;
    private int mTotalHeight;
    private CountDownTimer mTimer;
    private int maxHeight;

    public BulletView(Context context) {
        this(context, null);
    }

    public BulletView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public BulletView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.BOTTOM);
        init();
    }

    private void init() {
    }

    private void addTag(final Comment tag) {
        final Button button = new Button(mContext);
        button.setText(tag.content);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(15);
        StateRoundRectDrawable drawable = new StateRoundRectDrawable(Color.parseColor(DrawableUtils.getBackgoundColor(
                tag.content.hashCode())), Color.parseColor("#5d5d5d"));
        drawable.setDefautRadius(dip2px(DEFAULT_TAG_HEIGHT) / 2);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackground(drawable);
        } else {
            button.setBackgroundDrawable(drawable);
        }
        button.setPadding(dip2px(DEFAULT_TAG_PADDING), dip2px(DEFAULT_TAG_PADDING_TOP),
                dip2px(DEFAULT_TAG_PADDING), dip2px(DEFAULT_TAG_PADDING_TOP));
        int btnWidth = (int) (2 * dip2px(DEFAULT_TAG_PADDING) + button.getPaint().measureText(button.getText().toString()));
        LayoutParams layoutParams = new LayoutParams(btnWidth, dip2px(DEFAULT_TAG_HEIGHT));
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(button);
        mLayoutItem = new LinearLayout(mContext);
        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = dip2px(DEFAULT_LAYOUT_MARGIN_TOP);
        mTotalHeight += dip2px(DEFAULT_LAYOUT_MARGIN_TOP) + dip2px(DEFAULT_TAG_HEIGHT);
        mLayoutItem.setLayoutParams(lParams);
        addView(mLayoutItem);
        tempWidth = dip2px(DEFAULT_TAG_MARGIN) + btnWidth;
        mLayoutItem.addView(frameLayout, layoutParams);
        AlphaAnimation animation_alpha=new AlphaAnimation(0.1f,1.0f);
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
        animation_alpha.setDuration(500);//设置时间持续时间为 5000毫秒
        button.setAnimation(animation_alpha);
        button.startAnimation(animation_alpha);
    }

    private void refresh() {
        removeAllViews();
        mLayoutItem = new LinearLayout(mContext);
        mLayoutItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mLayoutItem);
        tempWidth = 0;
        for (Comment tag : comments) {
            addTag(tag);
        }
    }

    public void addComments() {
        mTimer = new CountDownTimer(16 * 1000, 2 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                final long millis = millisUntilFinished;
                int relative_to_self_translate_height = DEFAULT_LAYOUT_MARGIN_TOP + DEFAULT_TAG_HEIGHT;
                int layoutAdd = dip2px(DEFAULT_LAYOUT_MARGIN_TOP) + dip2px(DEFAULT_TAG_HEIGHT);
                if (mTotalHeight + layoutAdd > dip2px(150)) {
                    removeViewAt(0);
                    mTotalHeight -= layoutAdd;
                }
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    final float scale = mContext.getResources().getDisplayMetrics().density;

                    TranslateAnimation animation_translate=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, - layoutAdd);
                    //第一个参数fromXDelta为动画起始时 X坐标上的移动位置
                    //第二个参数toXDelta为动画结束时 X坐标上的移动位置
                    //第三个参数fromYDelta为动画起始时Y坐标上的移动位置
                    //第三个参数toYDelta为动画结束时Y坐标上的移动位置
                    if (i == getChildCount() - 1) {
                        animation_translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                addTag(new Comment("这是弹幕" + millis));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                    animation_translate.setDuration(500);//设置时间持续时间为 5000毫秒
                    view.setAnimation(animation_translate);
                    view.startAnimation(animation_translate);
                }
                if (getChildCount() == 0) {
                    addTag(new Comment("这是弹幕" + millis));
                }
            }

            @Override
            public void onFinish() {

            }
        };
        mTimer.start();
    }

    private int getDeviceWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
