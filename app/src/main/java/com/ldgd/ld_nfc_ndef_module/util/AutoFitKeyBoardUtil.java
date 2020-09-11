package com.ldgd.ld_nfc_ndef_module.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by ldgd on 2019/10/16.
 * 功能：
 * 说明：解决键盘适配问题
 */

public class AutoFitKeyBoardUtil  implements ViewTreeObserver.OnGlobalLayoutListener  {

    private static AutoFitKeyBoardUtil keyBoardUtil;

    public static AutoFitKeyBoardUtil getInstance() {
        if (keyBoardUtil == null) {
            synchronized (AutoFitKeyBoardUtil.class) {
                if (keyBoardUtil == null) {
                    keyBoardUtil = new AutoFitKeyBoardUtil();
                }
            }
        }
        return keyBoardUtil;
    }

    private AutoFitKeyBoardUtil() {

    }

    private View mChildOfContent;
    private int visibleHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int statusBarHeight;//状态栏高度
    private Activity activity;

    public void assistActivity(Activity activity) {
        this.activity = activity;
        //找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        //获取contentView
        mChildOfContent = content.getChildAt(0);
        //给布局设置View树监听，当布局有变化会调用
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        statusBarHeight = getStatusBarHeight(activity);
    }

    @Override
    public void onGlobalLayout() {
        //当前布局发生变化时，对Activity的xml布局进行重绘
        //获取当前界面可用高度
        int visibleHeight = computeVisibleHeight();
        //如果当前可见高度和之前的可见高度不一样,则判断是否键盘弹出
        if (visibleHeight != visibleHeightPrevious) {
            //获取Activity中xml中布局在当前界面显示的高度
            int contentHeight = mChildOfContent.getRootView().getHeight();
            //计算出布局被占用的高度
            int heightDifference = contentHeight - visibleHeight;
            //高度差大于屏幕1/4时，则判断为显示键盘
            if (heightDifference > (contentHeight / 4)) {
                frameLayoutParams.height = contentHeight - heightDifference + statusBarHeight;
            } else {//关闭键盘
                frameLayoutParams.height = contentHeight;
            }
            //重绘布局
            mChildOfContent.requestLayout();
            //记录本次的可见高度
            visibleHeightPrevious = visibleHeight;
        }
    }

    public void onDestory() {
        if (mChildOfContent != null) {
            mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mChildOfContent = null;
        }
        if (activity != null) {
            activity = null;
        }
    }

    /**
     * 获取可见区域大小
     *
     * @return
     */
    private int computeVisibleHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        return (r.bottom - r.top);
    }

    /**
     * 获取状态栏大小
     *
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }


}
