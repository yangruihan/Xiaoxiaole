package com.yrh.threewin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	/*---------------------------------------------------------------*/

	private int numberX = 7; // x 轴上方块个数，默认为8
	private int numberY = 7; // y 轴上方块个数，默认为8
	private int kindOfObj = 6; // 方块的种类个数，默认为6
	private float lineWidth = 14f; // 分割线的宽度
	private int moveX; // 水平移动的方向
	private int moveY; // 垂直移动的方向
	private int[][] map = GameLogic.MAP_7x7;
	private GameLogic game = new GameLogic(numberX, numberY, kindOfObj, map,
			this); // 游戏逻辑类

	private float gameScreenW; // 游戏区域宽度
	private float gameScreenH; // 游戏区域高度

	private float distanceToLeft; // 游戏区域离屏幕左边的距离
	private float distanceToTop; // 游戏区域离屏幕顶部的距离

	public static final float IMAGE_SIZE = 64F;

	private float oldPointX = 0f; // 记录按下点的 x 坐标
	private float oldPointY = 0f; // 记录按下点的 y 坐标

	private int pushX = -1; // 按下的方块的 x 坐标
	private int pushY = -1; // 按下的方块的 y 坐标

	private Canvas canvas;// /////////////////////////

	// 复写 onDraw 方法绘制游戏画面
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		this.canvas = canvas;// ////////////////////////////

		/*------------------------*/
		System.out.println("调用 onDraw 刷新");

		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(getResources().getColor(R.color.lineColor));
		canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

		gameScreenW = (IMAGE_SIZE + 2 * lineWidth) * numberX; // 游戏区域宽度
		gameScreenH = (IMAGE_SIZE + 2 * lineWidth) * numberY; // 游戏区域高度

		distanceToLeft = (getWidth() - gameScreenW) / 2f;
		distanceToTop = (getHeight() - gameScreenH) / 2f;

		// 绘制方块
		drawDiamonds(canvas);
	}

	private void drawDiamonds(Canvas canvas) {
		Paint diaPaint = new Paint();
		for (int j = 0; j < numberY; j++) {
			for (int i = 0; i < numberX; i++) {
				if (game.getDiaAt(i, j).isPush()) {
					@SuppressWarnings("deprecation")
					BitmapDrawable bitmapDrawable = new BitmapDrawable(this
							.getContext()
							.getResources()
							.openRawResource(
									getDrawableById(
											game.getDiaAt(i, j).getId(),
											PUSH_IMAGE)));
					canvas.drawBitmap(bitmapDrawable.getBitmap(),
							distanceToLeft + i * (IMAGE_SIZE + 2 * lineWidth),
							distanceToTop + j * (IMAGE_SIZE + 2 * lineWidth)
									+ lineWidth, diaPaint);
				} else {
					@SuppressWarnings("deprecation")
					BitmapDrawable bitmapDrawable = new BitmapDrawable(this
							.getContext()
							.getResources()
							.openRawResource(
									getDrawableById(
											game.getDiaAt(i, j).getId(),
											NORMAL_IMAGE)));
					canvas.drawBitmap(bitmapDrawable.getBitmap(),
							distanceToLeft + i * (IMAGE_SIZE + 2 * lineWidth),
							distanceToTop + j * (IMAGE_SIZE + 2 * lineWidth)
									+ lineWidth, diaPaint);
				}
			}
		}
	}

	public static final int NORMAL_IMAGE = 97812;
	public static final int PUSH_IMAGE = NORMAL_IMAGE + 1;

	private int getDrawableById(int id, int kind) {
		if (kind == NORMAL_IMAGE)
			switch (id) {
			case 1:
				return R.drawable.dia1;
			case 2:
				return R.drawable.dia2;
			case 3:
				return R.drawable.dia3;
			case 4:
				return R.drawable.dia4;
			case 5:
				return R.drawable.dia5;
			case 6:
				return R.drawable.dia6;
			}
		if (kind == PUSH_IMAGE) {
			switch (id) {
			case 1:
				return R.drawable.dia1_push;
			case 2:
				return R.drawable.dia2_push;
			case 3:
				return R.drawable.dia3_push;
			case 4:
				return R.drawable.dia4_push;
			case 5:
				return R.drawable.dia5_push;
			case 6:
				return R.drawable.dia6_push;
			}
		}
		return R.drawable.dia1;
	}

	// 复写触摸方法
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/*-----------------*/
			System.out.println("调用Action down");

			if (pushX != -1 && pushY != -1
					&& game.getDiaAt(pushX, pushY).isPush()) {
				game.getDiaAt(pushX, pushY).setPush(false);
				invalidate();
			}

			/*-----------------*/
			Log.i("mytag", "(" + event.getX() + ", " + event.getY() + ")");
			Log.i("mytag", "dis_toLeft:" + distanceToLeft + "     "
					+ "dis_toTop:" + distanceToTop);

			if (event.getX() < distanceToLeft
					|| event.getX() > distanceToLeft + gameScreenW) {
				return false;
			}
			if (event.getY() < distanceToTop
					|| event.getY() > distanceToTop + gameScreenH) {
				return false;
			}

			oldPointX = event.getX(); // 得到按下点的 x 坐标
			oldPointY = event.getY(); // 得到按下点的 y 坐标

			pushX = (int) ((oldPointX - distanceToLeft) / (IMAGE_SIZE + 2 * lineWidth));
			pushY = (int) ((oldPointY - distanceToTop) / (IMAGE_SIZE + 2 * lineWidth));

			game.getDiaAt(pushX, pushY).setPush(true);
			invalidate();

			/*-----------------*/
			Log.i("mytag", "(" + pushX + ", " + pushY + ")");
			Log.i("mytag", "当前点状态" + game.getDiaAt(pushX, pushY).getId() + " "
					+ game.getDiaAt(pushX, pushY).getColor());

			return true;

		case MotionEvent.ACTION_MOVE:
			// 移动后的坐标减按下时的坐标
			float pointX = event.getX() - oldPointX;
			float pointY = event.getY() - oldPointY;

			// 如果手指没有移动一个方块的宽度，则忽略移动
			if (Math.abs(pointX) < (IMAGE_SIZE / 2)
					&& Math.abs(pointY) < (IMAGE_SIZE / 2)) {
				return false;
			}

			// 判断移动的方向
			if (Math.abs(pointX) > Math.abs(pointY)) {
				if (pointX > 0) {
					moveX = +1;
				} else {
					moveX = -1;
				}
			} else {
				if (pointY > 0) {
					moveY = +1;
				} else {
					moveY = -1;
				}
			}

			return true;

		case MotionEvent.ACTION_UP:
			/*-----------------*/
			System.out.println("调用Action up");
			if (pushX != -1 && pushY != -1
					&& game.getDiaAt(pushX, pushY).isPush()) {
				game.getDiaAt(pushX, pushY).setPush(false);
				invalidate();
			}

			if (moveX == 0 && moveY == 0) {
				return false;
			}

			// 调用游戏逻辑类，移动方块，返回 True 移动成功，返回 False 移动失败
			if (game.exchange(pushX, pushY, moveX, moveY) == GameLogic.MOVE_SUCCESS) {

				if (game.canRemove()) {
					for (int j = 0; j < numberY; j++) {
						for (int i = 0; i < numberX; i++) {
							System.out
									.print(game.getDiaAt(i, j).isPush() + " ");
						}
						System.out.println();
					}
				}
				game.removeGoOn(pushX + moveX, pushY + moveY, pushX, pushY);

			} else {

			}

			// 还原状态
			oldPointX = 0;
			oldPointY = 0;
			moveX = 0;
			moveY = 0;

			return true;
		}
		return super.onTouchEvent(event);
	}

	public void refresh() {
		invalidate();
	}

	// 设置游戏难度等
	public void setGame(int numberX, int numberY, int kindOfObj,
			int[][] input_map) {
		this.numberX = numberX;
		this.numberY = numberY;
		this.kindOfObj = kindOfObj;
		this.map = input_map;
		this.game = null;
		this.game = new GameLogic(this.numberX, this.numberY, this.kindOfObj,
				this.map, this);
		System.out.println(this.game.getNumberX() + ", "
				+ this.game.getNumberY());
		invalidate();
	}

	/* Get Set 方法 */
	public GameLogic getGame() {
		return game;
	}

	public int getKindOfObj() {
		return kindOfObj;
	}

	public void setKindOfObj(int kindOfObj) {
		this.kindOfObj = kindOfObj;
	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	public int getNumberX() {
		return numberX;
	}

	public void setNumberX(int numberX) {
		this.numberX = numberX;
	}

	public int getNumberY() {
		return numberY;
	}

	public void setNumberY(int numberY) {
		this.numberY = numberY;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

}
