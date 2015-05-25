package com.yrh.threewin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

public class GameLogic {

	// 地图，1表示可消除方块，0表示墙壁
	public final static int[][] MAP_7x7 = { { 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1 }, };

	public final static int[][] MAP_8x8 = { { 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1 } };

	private GameView gameView;
	private int numberX; // x 轴上方块个数， 默认为7
	private int numberY; // y 轴上方块个数， 默认为7
	private int kindOfObj; // 可消除方块的种类个数，小于等于6, 默认为6
	private Diamond[][] map; // 用来存放方块的地图
	private HashMap<Integer, Integer> removeList = new HashMap<Integer, Integer>(); // 用来存放即将被消除的方块
	private int[][] tempMap; // 用来存放临时地图，用来判断地图上是否还有可以通过滑动消除的方块
	private int[][] inputMap; // 用来记录构造时传入的地图

	private static final int CHECK_INT = 6123;
	private static final int CHECK_DIA = CHECK_INT + 1;
	private int[] dirX = { -2, -1, 1, 2 }; // 需要判断的X坐标变化
	private int[] dirY = { -2, -1, 1, 2 }; // 需要判断的Y坐标变化
	private int[] tip = new int[4]; // 记录提示的坐标及方向

	public GameLogic(int numberX, int numberY, int kindOfObj,
			int[][] input_map, GameView gameView) {
		this.numberX = numberX;
		this.numberY = numberY;
		this.kindOfObj = kindOfObj;
		this.gameView = gameView;
		this.inputMap = input_map;
		if (kindOfObj > 6) {
			this.kindOfObj = 6;
		}
		// 初始化
		init(numberX, numberY, kindOfObj, input_map);

		/*-------*/
		debugPrint();
	}

	// 初始化函数
	private void init(int numberX, int numberY, int kindOfObj, int[][] input_map) {
		map = new Diamond[numberX][numberY];
		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				if (input_map[i][j] == 1) {
					int id = 1 + (int) (Math.random() * kindOfObj);
					// 判断当前ID号生成是否可以
					while (!idIsOk(id, i, j)) {
						id = 1 + (int) (Math.random() * kindOfObj);
					}
					map[i][j] = new Diamond(Diamond.KIND_OBJ, id, i, j);
				} else {
					map[i][j] = new Diamond(Diamond.KIND_WALL, 0, i, j);
				}
			}
		}

		/*----------------------*/
		System.out.println("before check will remove");

		// 检查是否有滑动即可清楚的滑块，如果没有则重新生成
		checkWillRemove(numberX, numberY, kindOfObj, inputMap);

	}

	private void checkWillRemove(int numberX, int numberY, int kindOfObj,
			int[][] input_map) {
		/*----------------------*/
		System.out.println("in check will remove");

		while (!haveWillRemoveDia()) {
			/*----------------------*/
			System.out.println("in have will remove dia loop");

			for (int i = 0; i < numberX; i++) {
				for (int j = 0; j < numberY; j++) {
					if (input_map[i][j] == 1) {
						int id = 1 + (int) (Math.random() * kindOfObj);
						// 判断当前ID号生成是否可以
						while (!idIsOk(id, i, j)) {
							id = 1 + (int) (Math.random() * kindOfObj);
						}
						map[i][j] = new Diamond(Diamond.KIND_OBJ, id, i, j);
					} else {
						map[i][j] = new Diamond(Diamond.KIND_WALL, 0, i, j);
					}
				}
			}
		}
	}

	// 判断当前位置的 ID 是否合理
	private boolean idIsOk(int id, int x, int y) {
		if (x - 2 >= 0) {
			if (map[x - 1][y].getId() == map[x - 2][y].getId()
					&& map[x - 1][y].getId() == id) {
				return false;
			}
		}
		if (y - 2 >= 0) {
			if (map[x][y - 2].getId() == map[x][y - 1].getId()
					&& map[x][y - 1].getId() == id) {
				return false;
			}
		}
		return true;
	}

	public final static int MOVE_SUCCESS = 1293; // 移动成功
	public final static int MOVE_ERROR = MOVE_SUCCESS + 1; // 不能移动
	public final static int MOVE_FAIL = MOVE_ERROR + 1; // 移动失败

	public int exchange(int x, int y, int moveX, int moveY) {
		if (x + moveX < 0 || x + moveX >= numberX || y + moveY < 0
				|| y + moveY >= numberY) {
			return MOVE_ERROR; // 返回移动失败
		}

		// 保存状态
		int oldX = x;
		int oldY = y;

		x += moveX;
		y += moveY;

		// 交换
		Diamond temp = map[oldX][oldY];
		map[oldX][oldY] = map[x][y];
		map[x][y] = temp;
		
		return MOVE_SUCCESS;
	}

	public int removeGoOn(int x, int y, int oldX, int oldY) {
		// 检查消除
		if (!checkRemove()) {
			// 如果消除失败，还原状态
			Diamond temp = map[oldX][oldY];
			map[oldX][oldY] = map[x][y];
			map[x][y] = temp;
			return MOVE_FAIL; // 返回移动失败
		}

		// 消除后的状态继续检查是否能消除
		while (checkRemove()) {

		}

		return MOVE_SUCCESS; // 返回移动成功
	}

	public boolean canRemove() {
		removeList.clear();
		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				// 每一个都进行周围检查
				checkAt(i, j, CHECK_DIA);
			}
		}
		// 如果执行完一遍，没有一个位置加入待消除列表，则没有需要消除的位置
		if (removeList.isEmpty()) {
			return false;
		}
		return true;
	}

	private boolean checkRemove() {

		if (!canRemove()) {
			return false;
		}

		/*-------*/
		System.out.println("before remove");
		debugPrint();
		// 消除
		remove();

		/*-------*/
		System.out.println("after remove");
		debugPrint();

		// 下移
		downMove();

		/*-------*/
		System.out.println("after down_move");
		debugPrint();

		// 补充
		add();

		/*-------*/
		System.out.println("after add");
		debugPrint();

		// 检查是否有通过滑动即可清除的方块如果没有则重新生成棋盘
		checkWillRemove(numberX, numberY, kindOfObj, inputMap);

		/*-------*/
		System.out.println("after check will remove");
		debugPrint();

		return true;
	}

	private void add() {
		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				if (map[i][j] == null) {
					int id = 1 + (int) (Math.random() * kindOfObj);
					map[i][j] = new Diamond(Diamond.KIND_OBJ, id, i, j);
				}
			}
		}
	}

	private void downMove() {
		for (int i = 0; i < numberX; i++) {
			for (int j = numberY - 1; j >= 0; j--) {
				if (map[i][j] == null && j - 1 >= 0) {
					// 将 null 方块向上移动到顶
					upMoveNull(i, j);
				}
			}

		}
	}

	private void upMoveNull(int x, int y) {
		int loc = y;
		while (y - 1 >= 0) {
			// 当 y 已经到最上面时退出
			if (y < 0) {
				return;
			}
			if (map[x][y - 1] == null) {
				loc = y;
				while (y - 1 >= 0 && map[x][y - 1] == null) {
					y--;
				}
				continue;
			} else if (map[x][y - 1].getKind() == Diamond.KIND_WALL) {
				loc = y;
				while (y - 1 >= 0
						&& map[x][y - 1].getKind() == Diamond.KIND_WALL) {
					y--;
				}
				continue;
			}
			Diamond temp = map[x][y - 1];
			map[x][y - 1] = null;
			map[x][loc] = temp;
			loc = y - 1;
			y--;
		}
	}

	private void remove() {
		Iterator<Entry<Integer, Integer>> iter = removeList.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<Integer, Integer> entry = iter.next();
			Integer key = entry.getKey();
			// 得到待消除的方块的坐标
			int x = key % numberX;
			int y = key / numberX;
			// 直接消除
			map[x][y] = null;
		}
	}

	private boolean haveWillRemoveDia() {
		/*----------------------*/
		System.out.println("in have will remove dia");

		tempMap = new int[numberX][numberY];

		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				tempMap[i][j] = map[i][j].getId(); // tempMap 只需要通过id号来进行判断即可
			}
		}

		/*--------------*/
		System.out.println("tempMap init successful!");

		for (int i = 0; i < numberX; i++) {
			for (int j = 0; j < numberY; j++) {
				// 如果当前位置可以往左交换，尝试一下，如果交换后能消除，则返回True，反之False，下同
				if (i - 1 >= 0) {
					if (tryMoveCanRemove(i, j, -1, 0)) {
						return true;
					}
				}
				if (i + 1 < numberX) {
					if (tryMoveCanRemove(i, j, 1, 0)) {
						return true;
					}
				}
				if (j - 1 >= 0) {
					if (tryMoveCanRemove(i, j, 0, -1)) {
						return true;
					}
				}
				if (j + 1 < numberY) {
					if (tryMoveCanRemove(i, j, 0, 1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean tryMoveCanRemove(int x, int y, int moveX, int moveY) {

		/*-----------------*/
		System.out.println("begin tryMoveCanRemove (" + x + ", " + y + ", "
				+ moveX + ", " + moveY + ")");

		int newX = x + moveX;
		int newY = y + moveY;

		int temp = tempMap[x][y];
		tempMap[x][y] = tempMap[newX][newY];
		tempMap[newX][newY] = temp;

		for (int i = 0; i < dirX.length; i++) {
			int tempX = newX + dirX[i];
			if (tempX >= 0 && tempX < numberX) {
				if (checkAt(tempX, newY, CHECK_INT)) {
					tip[0] = x;
					tip[1] = y;
					tip[2] = moveX;
					tip[3] = moveY;
					return true;
				}
			}
		}
		for (int i = 0; i < dirY.length; i++) {
			int tempY = newY + dirY[i];
			if (tempY >= 0 && tempY < numberY) {
				if (checkAt(newX, tempY, CHECK_INT)) {
					tip[0] = x;
					tip[1] = y;
					tip[2] = moveX;
					tip[3] = moveY;
					return true;
				}
			}
		}

		for (int i = 0; i < dirX.length; i++) {
			int tempX = x + dirX[i];
			if (tempX >= 0 && tempX < numberX) {
				if (checkAt(tempX, newY, CHECK_INT)) {
					tip[0] = x;
					tip[1] = y;
					tip[2] = moveX;
					tip[3] = moveY;
					return true;
				}
			}
		}
		for (int i = 0; i < dirY.length; i++) {
			int tempY = y + dirY[i];
			if (tempY >= 0 && tempY < numberY) {
				if (checkAt(x, tempY, CHECK_INT)) {
					tip[0] = x;
					tip[1] = y;
					tip[2] = moveX;
					tip[3] = moveY;
					return true;
				}
			}
		}

		// 如果没有找到可以消除的还要换回来
		temp = tempMap[x][y];
		tempMap[x][y] = tempMap[newX][newY];
		tempMap[newX][newY] = temp;
		return false;
	}

	private boolean checkAt(int x, int y, int kind) {

		if (kind == CHECK_INT) {
			if (x - 1 >= 0 && x + 1 < numberX) {
				if (tempMap[x - 1][y] == tempMap[x][y]
						&& tempMap[x][y] == tempMap[x + 1][y]) {
					return true;
				}
			}
			if (y - 1 >= 0 && y + 1 < numberY) {
				if (tempMap[x][y - 1] == tempMap[x][y]
						&& tempMap[x][y + 1] == tempMap[x][y]) {
					return true;
				}
			}
		}

		if (kind == CHECK_DIA) {
			if (x - 1 >= 0 && x + 1 < numberX) {
				if (map[x - 1][y].getId() == map[x][y].getId()
						&& map[x + 1][y].getId() == map[x][y].getId()) {
					int loc = x - 1 + y * numberX;
					removeList.put(loc, 1);
					removeList.put(loc + 1, 1);
					removeList.put(loc + 2, 1);
					map[x - 1][y].setPush(true);
					map[x][y].setPush(true);
					map[x + 1][y].setPush(true);
				}
			}
			if (y - 1 >= 0 && y + 1 < numberY) {
				if (map[x][y - 1].getId() == map[x][y].getId()
						&& map[x][y].getId() == map[x][y + 1].getId()) {
					removeList.put(x + y * numberX, 1);
					removeList.put(x + (y - 1) * numberY, 1);
					removeList.put(x + (y + 1) * numberX, 1);
					map[x][y + 1].setPush(true);
					map[x][y].setPush(true);
					map[x][y - 1].setPush(true);
				}
			}
			return true;
		}

		return false;
	}

	/* 调试工具 */
	private void debugPrint() {
		for (int i = 0; i < numberY; i++) {
			for (int j = 0; j < numberX; j++) {
				if (map[j][i] != null) {
					System.out.print(map[j][i].getId() + " ");
				} else {
					System.out.print("n ");
				}
			}
			System.out.println();
		}
	}

	/* Get 函数 */
	public Diamond getDiaAt(int x, int y) {
		if (x < numberX && x >= 0 && y < numberY && y >= 0) {
			return map[x][y];
		} else
			return null;
	}

	public int getNumberX() {
		return numberX;
	}

	public int getNumberY() {
		return numberY;
	}

	public int getKindOfObj() {
		return kindOfObj;
	}

	public Diamond[][] getMap() {
		return map;
	}

}
