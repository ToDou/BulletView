package com.tudou.bulletview.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tudou.bulletview.R;
import com.tudou.bulletview.drawable.StateRoundRectDrawable;
import com.tudou.bulletview.model.Comment;
import com.tudou.bulletview.util.DrawableUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tudou on 15-2-3.
 */
public class BulletView extends LinearLayout {
    private final int DEFAULT_TAG_PADDING = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding);
    private final int DEFAULT_TAG_MARGIN = getResources().getDimensionPixelOffset(R.dimen.default_tag_margin);
    private final int DEFAULT_TAG_PADDING_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding_top);
    private final int DEFAULT_LAYOUT_MARGIN_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_layout_margin_top);
    private final int DEFAULT_TAG_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.default_tag_height);

    private final int MAX_WIDTH = getResources().getDimensionPixelOffset(R.dimen.max_width);
    private final int MAX_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.max_height);
    private LinearLayout mLayoutItem;
    private Context mContext;
    private int mTotalHeight;
    private CountDownTimer mTimer;
    private ArrayList<String> mComments;

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
        button.setGravity(Gravity.CENTER);
        button.setText(stringFilter(ToDBC(tag.content)));
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(14);
        button.setCompoundDrawablePadding(0);
        button.setPadding(DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP,
                DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        int btnWidth = (int) (2 * DEFAULT_TAG_PADDING + button.getPaint().measureText(button.getText().toString()));
        int line = btnWidth / MAX_WIDTH;
        int btnHeight = getHeight(mContext, tag.content, 14, MAX_WIDTH, Typeface.DEFAULT, DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        StateRoundRectDrawable drawable = new StateRoundRectDrawable(Color.parseColor(DrawableUtils.getBackgoundColor(
                tag.content.hashCode())), Color.parseColor("#5d5d5d"));
        drawable.setBottomLeftRedius(btnHeight / 2);
        drawable.setTopLeftRedius(btnHeight / 2);
        drawable.setBottomRightRedius(0);
        drawable.setTopRightRedius(0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackground(drawable);
        } else {
            button.setBackgroundDrawable(drawable);
        }

        button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stop();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    start();
                }
                return false;
            }
        });

        if (line > 0) btnWidth = MAX_WIDTH;
        LayoutParams layoutParams = new LayoutParams(btnWidth, btnHeight);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(button);
        mLayoutItem = new LinearLayout(mContext);
        LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = DEFAULT_LAYOUT_MARGIN_TOP;
        mLayoutItem.setLayoutParams(lParams);
        addView(mLayoutItem);
        mLayoutItem.addView(frameLayout, layoutParams);
        mTotalHeight += DEFAULT_LAYOUT_MARGIN_TOP + btnHeight;
        AlphaAnimation animation_alpha = new AlphaAnimation(0.1f, 1.0f);
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
        animation_alpha.setDuration(500);//设置时间持续时间为 5000毫秒
        button.setAnimation(animation_alpha);
        button.startAnimation(animation_alpha);
    }

    public void addComments(ArrayList<String> comments) {
        mComments = comments;
        mTimer = new CountDownTimer(20 * 1000 * 1000, 3 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mComments.size() == 0) {
                    mTimer.cancel();
                    return;
                }
                int layoutAdd = calculateHeight(mComments.get(0)) + DEFAULT_LAYOUT_MARGIN_TOP;

                ArrayList<Integer> removeList = new ArrayList<>();

                for (int i = 0; i < getChildCount() && mTotalHeight + layoutAdd > MAX_HEIGHT; i++) {
                    int removeHeight = getChildAt(i).getHeight();
                    removeList.add(i);
                    mTotalHeight -= removeHeight + DEFAULT_LAYOUT_MARGIN_TOP;
                }

                doAnimationRemove(removeList, removeList.size(), layoutAdd);

                if (getChildCount() == 0) {
                    addTag(new Comment(mComments.get(0)));
                    mComments.remove(0);
                }
            }

            @Override
            public void onFinish() {

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
                        if (mComments.size() == 0) {
                            mTimer.cancel();
                            return;
                        }
                        view.clearAnimation();
                        addTag(new Comment(mComments.get(0)));
                        mComments.remove(0);
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
            mTimer.cancel();
        }
    }

    public void start() {
        if (mTimer != null) {
            mTimer.start();
        }
    }

    public static int getHeight(Context context, CharSequence text, int textSize, int deviceWidth, Typeface typeface, int paddingH, int paddingV) {
        TextView textView = new TextView(context);
        textView.setPadding(paddingH, paddingV, paddingH, paddingV);
        textView.setTypeface(typeface);
        textView.setText(text, TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    private int calculateHeight(String comment) {
        int btnHeight = getHeight(mContext, comment, 14, MAX_WIDTH, Typeface.DEFAULT, DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        return btnHeight;
    }

    /**
     * 全角转换为半角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":").replace("，", ",").replace("。", ".");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
