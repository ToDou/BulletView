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
import android.widget.TextView;

import com.tudou.bulletview.R;
import com.tudou.bulletview.drawable.StateRoundRectDrawable;
import com.tudou.bulletview.model.Comment;
import com.tudou.bulletview.util.DisplayUtil;
import com.tudou.bulletview.util.DrawableUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tudou on 15-2-3.
 */
public class BulletView extends LinearLayout {
    private final int MAX_BULLET_VIEW_HEIGHT = 200;
    private final int MAX_BULLET_VIEW_WIDTH = 100;
    private final int DEFAULT_TAG_PADDING = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding);
    private final int DEFAULT_TAG_MARGIN = getResources().getDimensionPixelOffset(R.dimen.default_tag_margin);
    private final int DEFAULT_TAG_PADDING_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_padding_top);
    private final int DEFAULT_LAYOUT_MARGIN_TOP = getResources().getDimensionPixelOffset(R.dimen.default_tag_layout_margin_top);
    private final int DEFAULT_TAG_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.default_tag_height);

    private final int MAX_WIDTH = getResources().getDimensionPixelOffset(R.dimen.max_width);
    private final int MAX_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.max_height);

    private ArrayList<Comment> comments;
    private ArrayList<Comment> commentsShow = new ArrayList<>();
    private LinearLayout mLayoutItem;
    private Context mContext;
    private int mTotalHeight;
    private int mTotalHeightPre;
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
        setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        init();
    }

    private void init() {
    }

    private void addTag(final Comment tag) {
        final TextView button = new TextView(mContext);
        button.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        button.setText(stringFilter(ToDBC(tag.content)));
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(14);
        button.setIncludeFontPadding(false);
        button.setCompoundDrawablePadding(0);
        //button.setLineSpacing(DEFAULT_TAG_PADDING_TOP, 1);
        button.setPadding(DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP,
                DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        int btnWidth = (int) (2 * DEFAULT_TAG_PADDING + button.getPaint().measureText(button.getText().toString()));
        int line = btnWidth / MAX_WIDTH;
        int btnHeight = DisplayUtil.sp2px(mContext, 14) + DEFAULT_TAG_PADDING_TOP * 2 + line * DisplayUtil.sp2px(mContext, 14) + line * DEFAULT_TAG_PADDING_TOP;
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
      //  AlphaAnimation animation_alpha=new AlphaAnimation(0.1f,1.0f);
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
       // animation_alpha.setDuration(500);//设置时间持续时间为 5000毫秒
       // button.setAnimation(animation_alpha);
        //button.startAnimation(animation_alpha);
    }

    private void refresh() {
        removeAllViews();
        mLayoutItem = new LinearLayout(mContext);
        mLayoutItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mLayoutItem);
        for (Comment tag : comments) {
            addTag(tag);
        }
    }

    public void addComments(final ArrayList<String> comments) {
        mTimer = new CountDownTimer(20 * 1000 * 1000, 2 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (comments.size() == 0) {
                    mTimer.cancel();
                    return;
                }
                addTag(new Comment(comments.get(0)));
                comments.remove(0);
                //getChildAt(getChildCount() - 1).setVisibility(View.GONE);
                mTotalHeightPre = mTotalHeight;
                mTotalHeight += DEFAULT_LAYOUT_MARGIN_TOP + getChildAt(getChildCount() - 1).getHeight();
                final long millis = millisUntilFinished;
                //int layoutAdd = calculateHeight(comments.get(0)) + DEFAULT_LAYOUT_MARGIN_TOP;
                int layoutAdd = getChildAt(getChildCount() - 1).getHeight();
                while (mTotalHeight > MAX_HEIGHT) {
                    int removeHeight = getChildAt(0).getHeight();
                    removeViewAt(0);
                    mTotalHeight -= removeHeight + DEFAULT_LAYOUT_MARGIN_TOP;
                }
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    TranslateAnimation animation_translate=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, - layoutAdd);
                    //第一个参数fromXDelta为动画起始时 X坐标上的移动位置
                    //第二个参数toXDelta为动画结束时 X坐标上的移动位置
                    //第三个参数fromYDelta为动画起始时Y坐标上的移动位置
                    //第三个参数toYDelta为动画结束时Y坐标上的移动位置
                    animation_translate.setDuration(500);//设置时间持续时间为 5000毫秒
                    view.setAnimation(animation_translate);
                    view.startAnimation(animation_translate);
                }
            }

            @Override
            public void onFinish() {

            }
        };
        mTimer.start();
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

    private int calculateHeight(String comment) {
        final TextView button = new TextView(mContext);
        button.setText(stringFilter(ToDBC(comment)));
        button.setIncludeFontPadding(false);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(14);
        //button.setLineSpacing(DEFAULT_TAG_PADDING_TOP, 1);
        button.setPadding(DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP,
                DEFAULT_TAG_PADDING, DEFAULT_TAG_PADDING_TOP);
        int btnWidth = (int) (2 * DEFAULT_TAG_PADDING + button.getPaint().measureText(button.getText().toString()));
        int line = btnWidth / MAX_WIDTH;
        int btnHeight = DisplayUtil.sp2px(mContext, 14) + DEFAULT_TAG_PADDING_TOP * 2 + line * DisplayUtil.sp2px(mContext, 14) + line * DEFAULT_TAG_PADDING_TOP;
        return btnHeight;
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
