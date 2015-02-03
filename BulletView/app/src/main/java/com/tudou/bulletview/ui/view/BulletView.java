package com.tudou.bulletview.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.tudou.bulletview.model.Comment;

import java.util.ArrayList;

/**
 * Created by tudou on 15-2-3.
 */
public class BulletView extends LinearLayout {
    private final int MAX_BULLET_VIEW_HEIGHT = 200;
    private final int MAX_BULLET_VIEW_WIDTH = 100;

    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Comment> commentsShow = new ArrayList<>();

    public BulletView(Context context) {
        this(context, null);
    }

    public BulletView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BulletView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.BOTTOM);
        init();
    }

    private void init() {

    }

}
