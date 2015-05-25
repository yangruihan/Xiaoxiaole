package com.yrh.threewin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MenuActivity extends Activity implements OnClickListener {

	private ImageButton btn_start;
	private ImageButton btn_about;
	private ImageButton btn_quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 将此 Activity 添加到系统管理类中
		SysApplication.getInstance().addActivity(this);
		// 设置菜单布局
		setContentView(R.layout.activity_menu);

		// 初始化控件
		initView();
	}

	private void initView() {
		btn_start = (ImageButton) findViewById(R.id.btn_start);
		btn_about = (ImageButton) findViewById(R.id.btn_about);
		btn_quit = (ImageButton) findViewById(R.id.btn_quit);
		btn_start.setOnClickListener(this);
		btn_about.setOnClickListener(this);
		btn_quit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击开始游戏按钮
		case R.id.btn_start:
			// 进行 Activity 跳转
			Intent intent = new Intent();
			intent.setClass(MenuActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		// 点击关于按钮
		case R.id.btn_about:

			break;
		// 点击退出游戏按钮
		case R.id.btn_quit:
			// 创建一个退出的对话框
			creatExitDialog();
			break;

		default:
			break;
		}
	}

	/**
	 * 创建一个退出的对话框
	 */
	private void creatExitDialog() {
		// 创建一个退出的对话框
		AlertDialog exitDialog = new AlertDialog.Builder(MenuActivity.this)
				.setTitle("确定要退出？")
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SysApplication.getInstance().exit();
					}

				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		// 将退出的对话框显示出来
		exitDialog.show();
	}

}
