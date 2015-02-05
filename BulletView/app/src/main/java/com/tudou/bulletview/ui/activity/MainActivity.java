package com.tudou.bulletview.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.tudou.bulletview.R;
import com.tudou.bulletview.ui.view.BulletView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.bullet_view)
    BulletView mBulletView;
    @InjectView(R.id.btn_start)
    Button mBtnStart;

    private boolean isPause = true;
    private boolean isStart = false;

    private String[] comments = new String[]{"你做个值班背景人应该很潇洒，都喜欢段子、糗事，根本就不会看", "导演瞄到，说这个", "做来做去跟动态", "槽的方式来表达自我比如你可以说你基友没节操你也可", "作部，结果舞美总监就",
            "点的正能量。他们会说，哇塞，这高贵冷艳的抠脚少女，原来内心也很柔软，这种会戳到你内心深处的东西就让人停不下来（众笑）。大家不要笑", "来做去", "方式来表达自我。比如你可以说你基友没节方式来表达自我。比如你可以说你基友没节", "方式来表达自我。比如你可以说你基友没节", "方式来表达自我。比如你可以说你基友没节",
            "方式来表达自我。比如你基友没节", "方式来表基友没节", "方式来表达自我。", "基友没节", "没节", "表达自我。比如你可以说你基表达自我。比如你可以说你基"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_start)
    @SuppressWarnings("unused")
    public void OnClick() {
        if (!isStart) {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < comments.length; i++) {
                strings.add(comments[i]);
            }
            mBulletView.addComments(strings);
            mBtnStart.setText("暂停");
            isStart = true;
            isPause = false;
        } else {
            if (!isPause) {
                mBulletView.stop();
                mBtnStart.setText("继续");
                isPause = true;
            } else {
                mBulletView.start();
                mBtnStart.setText("暂停");
                isPause = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
