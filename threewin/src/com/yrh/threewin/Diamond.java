package com.yrh.threewin;

import android.graphics.Color;

/**
 * 消消乐每个格子中的方块
 * 
 * @author Yrh
 *
 */
public class Diamond {

	public static final int KIND_WALL = 1689; // 表示墙壁
	public static final int KIND_OBJ = KIND_WALL + 1; // 表示可消除物体
	
	private int kind; // 种类，
	private int color; // 颜色，以后用图片代替
	private int id; // 通过 id 区分相同方块
	private int x; // 在区域中的 x 坐标
	private int y; // 在区域中的 y 坐标
//	private int[] colorList = {0x000000, 0xbf0000, 0x7ecd18, 0xeb858c, 0x3b9dd5, 0x7f6247};
	public static final int[] COLOR_LIST = {Color.BLACK, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.MAGENTA};
			
	// 构造函数
	public Diamond(int kind, int id, int x, int y) {
		this.kind = kind;
		this.id = id;
		this.x = x;
		this.y = y;
		this.color = COLOR_LIST[this.id - 1];
	}
	
	/* Get 方法 */
	public int getKind() {
		return kind;
	}
	
	public int getColor() {
		return this.color;
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
