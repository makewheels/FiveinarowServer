//棋子类
package chess;

import java.awt.Color;

public class ChessPieces {
	// 颜色
	private Color color;
	// 位置
	private int x;
	private int y;

	// get和set方法
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
