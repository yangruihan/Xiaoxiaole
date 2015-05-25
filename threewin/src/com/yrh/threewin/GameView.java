package com.yrh.threewin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
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
	private float diaWidth = 0f; // 每一个方块的宽度
	private float diaHeight = 0f; // 每一个方块的高度
	private float lineWidth = 8f; // 分割线的宽度
	private int moveX; // 水平移动的方向
	private int moveY; // 垂直移动的方向
	private int[][] map = GameLogic.MAP_7x7;
	private GameLogic game = new GameLogic(numberX, numberY, kindOfObj, map,
			this); // 游戏逻辑类

	// 复写 onDraw 方法绘制游戏画面
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(getResources().getColor(R.color.lineColor));
		canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

		diaWidth = getWidth() / numberX; // 得到每块方块的宽度
		diaHeight = getHeight() / numberY; // 得到每块方块的高度

		// 绘制方块
		Paint diaPaint = new Paint();
		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				diaPaint.setColor(game.getDiaAt(i, j).getColor());
				// canvas.drawRect(i * diaWidth, j * diaHeight,
				// (i + 1) * diaWidth, (j + 1) * diaHeight, diaPaint);
				canvas.drawRoundRect(new RectF(i * diaWidth, j * diaHeight,
						(i + 1) * diaWidth, (j + 1) * diaHeight), 30f, 40f,
						diaPaint);
			}
		}

		// 绘制分割线
		Paint linePaint = new Paint();
		linePaint.setColor(getResources().getColor(R.color.lineColor));
		linePaint.setStrokeWidth(lineWidth);
		for (int i = 0; i < numberX; i++) {
			canvas.drawLine(i * diaWidth, 0, i * diaWidth, getHeight(),
					linePaint);
		}
		for (int i = 0; i < numberY; i++) {
			canvas.drawLine(0, i * diaHeight, getWidth(), i * diaHeight,
					linePaint);
		}

	}

	private float oldPointX = 0f; // 记录按下点的 x 坐标
	private float oldPointY = 0f; // 记录按下点的 y 坐标

	private int pushX; // 按下的方块的 x 坐标
	private int pushY; // 按下的方块的 y 坐标

	// 复写触摸方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			oldPointX = event.getX(); // 得到按下点的 x 坐标
			oldPointY = event.getY(); // 得到按下点的 y 坐标

			pushX = (int) (oldPointX / diaWidth);
			pushY = (int) (oldPointY / diaHeight);

			Log.i("mytag", "(" + pushX + ", " + pushY + ")");
			Log.i("mytag", "当前点状态" + game.getDiaAt(pushX, pushY).getId() + " "
					+ game.getDiaAt(pushX, pushY).getColor());

			return true;

		case MotionEvent.ACTION_MOVE:
			// 移动后的坐标减按下时的坐标
			float pointX = event.getX() - oldPointX;
			float pointY = event.getY() - oldPointY;

			// 如果手指没有移动一个方块的宽度，则忽略移动
			if (Math.abs(pointX) < diaWidth && Math.abs(pointY) < diaHeight) {
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
			if (moveX == 0 && moveY == 0) {
				return false;
			}

			// 调用游戏逻辑类，移动方块，返回 True 移动成功，返回 False 移动失败
			if (game.move(pushX, pushY, moveX, moveY) == GameLogic.MOVE_SUCCESS) {
				Log.i("mytag", "移动成功！");
			} else {
				Log.i("mytag", "移动失败！");
			}

			// 刷新画面
			invalidate();

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
		this.invalidate();
	}

	// 设置游戏难度等
	public void setGame(int numberX, int numberY, int kindOfObj, int[][] input_map) {
		this.numberX = numberX;
		this.numberY = numberY;
		this.kindOfObj = kindOfObj;
		this.map = input_map;
		this.game = new GameLogic(this.numberX, this.numberY, this.kindOfObj, this.map, this);
		// 刷新画面
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
