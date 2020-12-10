//服务器的一桌类
//正在下棋的两个人组成一桌
package chess;

import java.awt.Color;

public class Table {
	// 棋盘上的棋子数组
	public ChessPieces[][] chessPieces;
	// 申请游戏玩家
	public String playerApply;
	// 接受申请玩家
	public String playerAgree;

	// 带参构造器
	public Table(String playerApply, String playerAgree) {
		this.playerApply = playerApply;
		this.playerAgree = playerAgree;
		// 初始化Table时，自动初始化ChessPieces[][]
		chessPieces = new ChessPieces[15][15];
	}

	// 添加棋子
	public void addChessPiece(int x, int y, Color color) {
		chessPieces[x][y] = new ChessPieces();
		chessPieces[x][y].setColor(color);
	}
}
