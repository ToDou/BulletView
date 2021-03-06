package com.tudou.barragelibrary.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tudou.barragelibrary.R;
import com.tudou.barragelibrary.drawable.StateRoundRectDrawable;
import com.tudou.barragelibrary.model.Comment;
import com.tudou.barragelibrary.util.DrawableUtils;

import java.util.ArrayList;

/**
 * Created by tudou on 15-2-3.
 */
public class BulletView extends LinearLayout {
    private final int DEFAULT_TAG_PADDING = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding);
    private final int DEFAULT_TAG_MARGIN = getResources().getDimensionPixelOffset(R.dimen.default_tag_margin);
    private final int DEFAULT_TAG_PADDING_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding_top);
    private final int DEFAULT_LAYOUT_MARGIN_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_layout_margin_top);
    private final int CIRCLE_IMAGE_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.image_circle_height);
    private final int DEFAULT_TAG_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.default_tag_height);

    private final int MAX_WIDTH = getResources().getDimensionPixelOffset(R.dimen.max_width);
    private final int MAX_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.max_height);
    private LinearLayout mLayoutItem;
    private Context mContext;
    private int mTotalHeight;
    private CountDownTimer mTimer;
    private ArrayList<String> mComments;
    private int mNextIndex;
    private Boolean isScreenPause = false;
    private Boolean isDoingReShow = false;
    private Boolean isPause = false;

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
        setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        init();
    }

    private void init() {
    }

    private void addTag(final Comment tag) {
        final Button button = new Button(mContext);
        button.setGravity(Gravity.LEFT);
        button.setText(tag.content);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(14);
        button.setCompoundDrawablePadding(0);
        button.setPadding(DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP,
                DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        int btnWidth = (int) (2 * DEFAULT_TAG_PADDING + button.getPaint().measureText(button.getText().toString()));
        int line = btnWidth / MAX_WIDTH;
        final int btnHeight = getHeight(mContext, tag.content, 14, MAX_WIDTH, Typeface.DEFAULT, DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        StateRoundRectDrawable drawable = new StateRoundRectDrawable(Color.parseColor(DrawableUtils.getBackgoundColor(
                tag.content.hashCode())), Color.parseColor("#5d5d5d"));
        drawable.setBottomLeftRedius(DEFAULT_TAG_PADDING);
        drawable.setTopLeftRedius(DEFAULT_TAG_PADDING);
        drawable.setBottomRightRedius(0);
        drawable.setTopRightRedius(0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackground(drawable);
        } else {
            button.setBackgroundDrawable(drawable);
        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (isPause) {
                    if (isPause) {
                        reStart();
                    } else {
                        stop();
                    }
                }
            }
        });
        if (line > 0) btnWidth = MAX_WIDTH;
        LayoutParams layoutParams = new LayoutParams(btnWidth, btnHeight);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.addView(button);
        mLayoutItem = new LinearLayout(mContext);
        LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = DEFAULT_LAYOUT_MARGIN_TOP;
        mLayoutItem.setLayoutParams(lParams);
        addView(mLayoutItem);
        mLayoutItem.addView(linearLayout, layoutParams);
        mTotalHeight += DEFAULT_LAYOUT_MARGIN_TOP + btnHeight;
        AlphaAnimation animation_alpha = new AlphaAnimation(0.1f, 1.0f);
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
        animation_alpha.setDuration(500);//设置时间持续时间为 5000毫秒
        button.setAnimation(animation_alpha);
        button.startAnimation(animation_alpha);
    }

    public void setData(ArrayList<String> comments) {
        stop();
        finsh();
        removeAllView();
        mTotalHeight = 0;
        mNextIndex = 0;
        synchronized (isPause) {
            isPause = false;
        }
        //getBarrageList(timestamp); # to get the internet info
        mComments = comments;
        startShow();
    }

    public void reShow() {
        synchronized (isDoingReShow) {
            if (isDoingReShow) {
                return;
            } else {
                isDoingReShow = true;
            }
        }
        stop();
        finsh();

        mTotalHeight = 0;
        mNextIndex = 0;
        removeAllView();
        startShow();
        synchronized (isDoingReShow) {
            isDoingReShow = false;
        }
    }

    public void startShow() {
        if (mTimer != null) {
            synchronized (mTimer) {
                startTimer();
            }
        } else {
            startTimer();
        }
    }

    public void refresh(String string) {
        if (mComments == null) {
            mComments = new ArrayList<>();
            mComments.add(mNextIndex, string);
            startShow();
        } else if (isPause) {
            mComments.add(mNextIndex, string);
            reStart();
        }
        //getBarrageList(timestamp);
    }

    public void startTimer() {
        mTimer = new CountDownTimer(1501, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (getVisibility() != View.VISIBLE) {
                    stop();
                    return;
                }
                if (mComments == null) {
                    finsh();
                    return;
                }
                if (mNextIndex > mComments.size() - 1 && canShowLoop()) {
                    mNextIndex = 0;
                    reStart();
                }
                int layoutAdd = calculateHeight(mComments.get(mNextIndex)) + DEFAULT_LAYOUT_MARGIN_TOP;

                ArrayList<Integer> removeList = new ArrayList<>();

                for (int i = 0; i < getChildCount() && mTotalHeight + layoutAdd > MAX_HEIGHT; i++) {
                    int removeHeight = getChildAt(i).getHeight();
                    removeList.add(i);
                    mTotalHeight -= removeHeight + DEFAULT_LAYOUT_MARGIN_TOP;
                }

                doAnimationRemove(removeList, removeList.size(), layoutAdd);

                if (getChildCount() == 0) {
                    addTag(new Comment(mComments.get(mNextIndex)));
                    mNextIndex++;
                }
            }

            @Override
            public void onFinish() {
                if (mComments != null && mNextIndex <= mComments.size() - 1) {
                    mTimer.start();
                }

                if (mComments != null && mNextIndex > mComments.size() - 1 && canShowLoop()) {
                    mNextIndex = 0;
                    reStart();
                }
            }
        };
        mTimer.start();
    }

    private void doAnimationRemove(final ArrayList<Integer> list, final int listSize, final int layoutAdd) {
        if (list.size() == 0) {
            doAnimationAdd(layoutAdd);
            return;
        }
        final View view = getChildAt(0);
        AlphaAnimation animation_alpha = new AlphaAnimation(1.0f, 0.0f);
        animation_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                removeView(view);
                list.remove(0);
                doAnimationRemove(list, listSize, layoutAdd);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
        animation_alpha.setDuration(200 / listSize);//设置时间持续时间为 5000毫秒
        view.setAnimation(animation_alpha);
        view.startAnimation(animation_alpha);
    }

    private void doAnimationAdd(int layoutAdd) {
        for (int i = 0; i < getChildCount(); i++) {
            final View view = getChildAt(i);
            TranslateAnimation animation_translate = new TranslateAnimation(0, 0, 0, -layoutAdd);
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

                        if (mComments == null) {
                            finsh();
                            return;
                        }

                        if (mNextIndex > mComments.size() - 1 && canShowLoop()) {
                            mNextIndex = 0;
                            reStart();
                        }

                        view.clearAnimation();
                        addTag(new Comment(mComments.get(mNextIndex)));
                        mNextIndex++;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            if (i != getChildCount() - 1) {
                animation_translate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.clearAnimation();
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
    }

    public void stop() {
        if (mTimer != null) {
            synchronized (mTimer) {
                mTimer.cancel();
                synchronized (isPause) {
                    isPause = true;
                }
            }
        }
    }

    private void finsh() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void screenStop() {
        stop();
        synchronized (isScreenPause) {
            isScreenPause = true;
        }
    }

    public boolean isScreenPause() {
        return isScreenPause;
    }

    public void reStart() {
        stop();
        synchronized (isPause) {
            if (mTimer != null) {
                synchronized (mTimer) {
                    if (isPause == true) {
                        mTimer.start();
                    }
                }
                isPause = false;
            }
        }
        synchronized (isScreenPause) {
            if (isScreenPause = true) {
                isScreenPause = false;
            }
        }
    }

    @SuppressWarnings("unused")
    public void show() {
        this.setVisibility(View.VISIBLE);
        reStart();
    }

    public void hide() {
        stop();
        this.setVisibility(View.GONE);
    }

    public int getHeight(Context context, CharSequence text, int textSize, int deviceWidth, Typeface typeface, int paddingH, int paddingV) {
        TextView textView = new TextView(context);

        textView.setPadding(paddingH, paddingV, paddingH, paddingV);
        textView.setTypeface(typeface);
        textView.setText(text, TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    private int calculateHeight(String comment) {
        int btnHeight = getHeight(mContext, comment, 14, MAX_WIDTH, Typeface.DEFAULT, DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        return btnHeight;
    }

    private boolean canShowLoop() {
        if (mComments == null) return false;
        int total = 0;
        for (int i = mComments.size() - 1; i >= 0; i--) {
            total += calculateHeight(mComments.get(i)) + DEFAULT_LAYOUT_MARGIN_TOP;
            if (total > MAX_HEIGHT) {
                return true;
            }
        }
        return false;
    }

    private void removeAllView() {
        clearAnimation();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

}
