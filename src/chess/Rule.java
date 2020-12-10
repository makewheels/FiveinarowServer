//游戏规则类
//判断是否有人赢了
package chess;

import java.awt.Color;

public class Rule {
	// 谁赢了
	// 0:没人赢
	// 1:白方胜
	// 2:黑方胜

	// 引用棋子数组
	ChessPieces[][] chessPieces;

	// 带参构造器
	public Rule(ChessPieces[][] chessPieces) {
		this.chessPieces = chessPieces;
	}

	// 判断是否有人赢了
	public int[] whoWin(Color myColor) {
		// 横向
		for (int i = 0; i <= 10; i++) {
			for (int j = 0; j <= 14; j++) {
				// 先找到有棋子的地方
				if (chessPieces[i][j] == null) {
					continue;
				}
				// 找到我这种颜色
				if (chessPieces[i][j].getColor() != myColor) {
					continue;
				}
				// 是否需要continue
				boolean needContinue = false;
				// 再看后面四个棋子是不是跟第一个颜色一样
				for (int k = 1; k <= 4; k++) {
					// 如果后面有棋子的话
					if (chessPieces[i + k][j] != null) {
						// 如果颜色不一样
						if (chessPieces[i + k][j].getColor() != myColor) {
							// 那就需要continue了
							needContinue = true;
							break;
						}
						// 如果后面直接就没棋子了的话，那也要continue
					} else {
						needContinue = true;
						break;
					}
				}
				// 如果颜色不一样，那需要continue
				if (needContinue == true) {
					needContinue = false;
					continue;
				}
				// 能到这里，说明已经有五子一线了
				// 要返回的数组
				int[] array = new int[5];
				// [0]存颜色
				if (myColor == Color.WHITE) {
					// 白色
					array[0] = 1;
				} else {
					// 黑色
					array[0] = 2;
				}
				// [1]存开始的横坐标
				array[1] = i;
				// [2]存开始的纵坐标
				array[2] = j;
				// [3]存结束的横坐标
				array[3] = i + 4;
				// [4]存结束的纵坐标
				array[4] = j;
				return array;
			}
		}

		// 竖向
		for (int i = 0; i <= 14; i++) {
			for (int j = 0; j <= 10; j++) {
				// 先找到有棋子的地方
				if (chessPieces[i][j] == null) {
					continue;
				}
				// 找到我这种颜色
				if (chessPieces[i][j].getColor() != myColor) {
					continue;
				}
				// 是否需要continue
				boolean needContinue = false;
				// 再看后面四个棋子是不是跟第一个颜色一样
				for (int k = 1; k <= 4; k++) {
					// 如果后面有棋子的话
					if (chessPieces[i][j + k] != null) {
						// 如果颜色不一样
						if (chessPieces[i][j + k].getColor() != myColor) {
							// 那就需要continue了
							needContinue = true;
							break;
						}
						// 如果后面直接就没棋子了的话，那也要continue
					} else {
						needContinue = true;
						break;
					}
				}
				// 如果颜色不一样，那需要continue
				if (needContinue == true) {
					needContinue = false;
					continue;
				}
				// 能到这里，说明已经有五子一线了
				// 要返回的数组
				int[] array = new int[5];
				// [0]存颜色
				if (myColor == Color.WHITE) {
					// 白色
					array[0] = 1;
				} else {
					// 黑色
					array[0] = 2;
				}
				// [1]存开始的横坐标
				array[1] = i;
				// [2]存开始的纵坐标
				array[2] = j;
				// [3]存结束的横坐标
				array[3] = i;
				// [4]存结束的纵坐标
				array[4] = j + 4;
				return array;
			}
		}

		// 从左上到右下
		for (int i = 0; i <= 10; i++) {
			for (int j = 0; j < 10; j++) {
				// 先找到有棋子的地方
				if (chessPieces[i][j] == null) {
					continue;
				}
				// 找到我这种颜色
				if (chessPieces[i][j].getColor() != myColor) {
					continue;
				}
				// 是否需要continue
				boolean needContinue = false;
				// 再看后面四个棋子是不是跟第一个颜色一样
				for (int k = 1; k <= 4; k++) {
					// 如果后面有棋子的话
					if (chessPieces[i + k][j + k] != null) {
						// 如果颜色不一样
						if (chessPieces[i + k][j + k].getColor() != myColor) {
							// 那就需要continue了
							needContinue = true;
							break;
						}
						// 如果后面直接就没棋子了的话，那也要continue
					} else {
						needContinue = true;
						break;
					}
				}
				// 如果颜色不一样，那需要continue
				if (needContinue == true) {
					needContinue = false;
					continue;
				}
				// 能到这里，说明已经有五子一线了
				// 要返回的数组
				int[] array = new int[5];
				// [0]存颜色
				if (myColor == Color.WHITE) {
					// 白色
					array[0] = 1;
				} else {
					// 黑色
					array[0] = 2;
				}
				// [1]存开始的横坐标
				array[1] = i;
				// [2]存开始的纵坐标
				array[2] = j;
				// [3]存结束的横坐标
				array[3] = i + 4;
				// [4]存结束的纵坐标
				array[4] = j + 4;
				return array;
			}
		}

		// 从左下到右上
		for (int i = 4; i <= 14; i++) {
			for (int j = 0; j <= 10; j++) {
				// 先找到有棋子的地方
				if (chessPieces[i][j] == null) {
					continue;
				}
				// 找到我这种颜色
				if (chessPieces[i][j].getColor() != myColor) {
					continue;
				}
				// 是否需要continue
				boolean needContinue = false;
				// 再看后面四个棋子是不是跟第一个颜色一样
				for (int k = 1; k <= 4; k++) {
					// 如果后面有棋子的话
					if (chessPieces[i - k][j + k] != null) {
						// 如果颜色不一样
						if (chessPieces[i - k][j + k].getColor() != myColor) {
							// 那就需要continue了
							needContinue = true;
							break;
						}
						// 如果后面直接就没棋子了的话，那也要continue
					} else {
						needContinue = true;
						break;
					}
				}
				// 如果颜色不一样，那需要continue
				if (needContinue == true) {
					needContinue = false;
					continue;
				}
				// 能到这里，说明已经有五子一线了
				// 要返回的数组
				int[] array = new int[5];
				// [0]存颜色
				if (myColor == Color.WHITE) {
					// 白色
					array[0] = 1;
				} else {
					// 黑色
					array[0] = 2;
				}
				// [1]存开始的横坐标
				array[1] = i;
				// [2]存开始的纵坐标
				array[2] = j;
				// [3]存结束的横坐标
				array[3] = i - 4;
				// [4]存结束的纵坐标
				array[4] = j + 4;
				return array;
			}
		}
		// 看了一圈发现有人赢，那就返回null
		return null;
	}
}
