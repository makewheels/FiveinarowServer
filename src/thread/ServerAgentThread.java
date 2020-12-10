//服务器代理线程类
package thread;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import chess.Rule;
import chess.Table;

import frame.ServerFrame;

public class ServerAgentThread extends Thread {
	// 线程是否运行控制位
	public boolean flag = true;
	// 引用ServerFrame
	public ServerFrame serverFrame;
	// 引用ServerThread
	public ServerThread serverThread;
	// Socket
	public Socket socket;
	// DataInputStream
	public DataInputStream dataInputStream;
	// DataOutputStream
	public DataOutputStream dataOutputStream;
	// 对手昵称
	public String opponent = null;
	// 对手的服务器代理线程
	public ServerAgentThread opponentServerAgentThread;
	// 一桌
	public Table table;
	// 规则
	public Rule rule;
	// 我是什么颜色
	public Color myColor;

	// 带参构造器
	public ServerAgentThread(ServerFrame serverFrame,
			ServerThread serverThread, Socket socket) {
		this.serverFrame = serverFrame;
		this.serverThread = serverThread;
		this.socket = socket;
		try {
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// run方法
	@Override
	public void run() {
		while (flag) {
			try {
				String messageFromClient = dataInputStream.readUTF().trim();
				// 新用户登录
				if (messageFromClient.startsWith("<#NEW_CLIENT#>")) {
					newClient(messageFromClient.substring(14));
					continue;
					// 用户登出
				} else if (messageFromClient.startsWith("<#CLIENT_LEAVE#>")) {
					clientLeave();
					continue;
					// 申请游戏
				} else if (messageFromClient.startsWith("<#APPLY_GAME#>")) {
					applyGame(messageFromClient.substring(14));
					continue;
					// 同意游戏申请
				} else if (messageFromClient.startsWith("<#AGREE#>")) {
					agree(messageFromClient.substring(9));
					continue;
					// 拒绝游戏申请
				} else if (messageFromClient.startsWith("<#REFUSE#>")) {
					refuse(messageFromClient.substring(10));
					continue;
					// 时间到
				} else if (messageFromClient.startsWith("<#TIME_UP#>")) {
					timeUp();
					continue;
					// 下棋
				} else if (messageFromClient.startsWith("<#MOVE#>")) {
					move(messageFromClient.substring(8));
					continue;
					// 聊天
				} else if (messageFromClient.startsWith("<#CHAT#>")) {
					chat(messageFromClient.substring(8));
					continue;
					// 认输
				} else if (messageFromClient.startsWith("<#GIVE_UP#>")) {
					giveUp();
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 返回指定昵称的服务器代理线程
	// SAT代表ServerAgentThread
	public ServerAgentThread getSATByName(String nickName) {
		// 遍历服务器列表
		for (int i = 0; i < serverFrame.userOnlineList.size(); i++) {
			// 拿到临时代理线程
			ServerAgentThread tempServerAgentThread = (ServerAgentThread) serverFrame.userOnlineList
					.get(i);
			// 如果找到了指定的名字的代理线程，就返回
			if (tempServerAgentThread.getName().equals(nickName)) {
				return tempServerAgentThread;
			}
		}
		// 没找到，就返回null
		return null;
	}

	// 新用户登录
	public void newClient(String messageFromClient) {
		// 设置线程名字
		this.setName(messageFromClient);
		// 如果能找到，说明重名了
		if (getSATByName(messageFromClient) != null) {
			try {
				// 通知客户端：重名了！
				dataOutputStream.writeUTF("<#HAVE_THE_NAME#>");
				// 该关的关
				dataInputStream.close();
				dataOutputStream.close();
				// 线程也干掉
				this.flag = false;
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		// 能到这说明没有重名
		// 先加进来
		serverFrame.userOnlineList.add(this);
		// 刷新服务器在线用户列表
		serverThread.refreshServerList();
		// 通知所有客户端
		refreshClientList();
	}

	// 用户登出
	public void clientLeave() {
		// 如果该用户正在和别人下棋，属于逃跑的话
		if (this.opponent != null) {
			try {
				// 通知他，他的对手已经逃跑了
				opponentServerAgentThread.dataOutputStream
						.writeUTF("<#RUN_AWAY#>");
				// 设置他的opponent为null
				opponentServerAgentThread.opponent = null;
				opponentServerAgentThread.opponentServerAgentThread = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		serverFrame.userOnlineList.remove(this);
		this.flag = false;
		refreshClientList();
		serverThread.refreshServerList();
	}

	// 申请游戏
	public void applyGame(String messageFromClient) {
		// 从列表找这个人
		ServerAgentThread tempServerAgentThread = getSATByName(messageFromClient);
		// 如果能找到这个人
		if (tempServerAgentThread != null) {
			// 先看看他是不是已经在和别人下棋
			if (tempServerAgentThread.opponent != null) {
				try {
					// 如果在和别人下棋，通知客户端
					this.dataOutputStream.writeUTF("<#BUSY#>" + "|"
							+ messageFromClient + "|"
							+ tempServerAgentThread.opponent);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 能到这里说明可以通知被申请的客户端了
			} else {
				try {
					// 通知要被申请的客户端
					tempServerAgentThread.dataOutputStream
							.writeUTF("<#APPLY_GAME#>" + this.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 同意游戏申请：usefulMessageFromClient是提出申请者
	public void agree(String usefulMessageFromClient) {
		// 开始游戏
		startGame(this.getName(), usefulMessageFromClient);
		// 通知提出申请者
		try {
			// 设置对手
			opponentServerAgentThread.opponent = this.getName();
			opponentServerAgentThread.dataOutputStream.writeUTF("<#AGREE#>"
					+ this.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 刷新服务器列表
		serverThread.refreshServerList();
	}

	// 开始游戏
	public void startGame(String playerAgree, String playerApply) {
		// 初始化Table
		table = new Table(playerApply, playerAgree);
		// 初始化rule
		rule = new Rule(table.chessPieces);
		// 设置申请者opponent
		opponent = playerApply;
		// 设置申请者颜色
		myColor = Color.WHITE;
		// 拿到对手的服务器代理线程
		ServerAgentThread tempServerAgentThread = getSATByName(playerApply);
		// 设置申请者的对手服务器代理线程
		this.opponentServerAgentThread = tempServerAgentThread;
		if (tempServerAgentThread != null) {
			// 初始化同意者的Table
			tempServerAgentThread.table = this.table;
			// 初始化同意者的rule
			tempServerAgentThread.rule = rule;
			// 设置同意者的opponent
			tempServerAgentThread.opponent = playerAgree;
			tempServerAgentThread.opponentServerAgentThread = this;
			// 设置颜色
			tempServerAgentThread.myColor = Color.BLACK;
		}
	}

	// 拒绝游戏申请
	public void refuse(String usefulMessageFromClient) {
		// 通知提出申请者
		try {
			// 拿到临时代理线程
			ServerAgentThread tempServerAgentThread = getSATByName(usefulMessageFromClient);
			if (tempServerAgentThread != null) {
				tempServerAgentThread.dataOutputStream.writeUTF("<#REFUSE#>"
						+ this.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 时间到
	public void timeUp() {
		// 通知对手
		try {
			opponentServerAgentThread.dataOutputStream.writeUTF("<#TIME_UP#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 下棋
	public void move(String usefulMessageFromClient) {
		// 得到坐标
		String[] coordinate = usefulMessageFromClient.split("\\|");
		int x = Integer.parseInt(coordinate[0]);
		int y = Integer.parseInt(coordinate[1]);
		// 添加棋子
		table.addChessPiece(x, y, myColor);
		// 通知对手走棋
		if (opponentServerAgentThread.getName().equals(this.opponent)) {
			try {
				opponentServerAgentThread.dataOutputStream.writeUTF("<#MOVE#>"
						+ x + "|" + y);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 看看是否有人赢了
		int[] messageArray = rule.whoWin(myColor);
		// 如果不等于null，说明有人赢了，执行gameOver();
		if (messageArray != null) {
			gameOver(messageArray);
		}
	}

	// 聊天
	public void chat(String usefulMessageFromClient) {
		// 拿到stringArray，[0]是对方昵称，[1]是聊天内容
		String[] stringArray = usefulMessageFromClient.split("\\|");
		// 如果是向对手发送
		if (stringArray[0].equals("OPPONENT")) {
			// 通知对手聊天信息
			try {
				opponentServerAgentThread.dataOutputStream.writeUTF("<#CHAT#>"
						+ "OPPONENT" + "|" + stringArray[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 如果不是向对手发送
		} else {
			// 先拿到这个人的服务器代理线程
			ServerAgentThread tempServerAgentThread = getSATByName(stringArray[0]);
			try {
				// 通知他聊天信息
				tempServerAgentThread.dataOutputStream.writeUTF("<#CHAT#>"
						+ this.getName() + "|" + stringArray[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 认输
	public void giveUp() {
		// 设置对手的opponent为null
		opponentServerAgentThread.opponent = null;
		// 通知客户端
		try {
			opponentServerAgentThread.dataOutputStream.writeUTF("<#GIVE_UP#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 设置自己的opponent为null
		opponent = null;
		// 刷新服务器列表
		serverThread.refreshServerList();
	}

	// 游戏结束
	public void gameOver(int[] messageArray) {
		// 如果是白色赢了
		if (messageArray[0] == 1) {
			// 如果我自己也是白色
			if (myColor == Color.WHITE) {
				try {
					// 通知自己的客户端：赢
					dataOutputStream.writeUTF("<#GAME_OVER#>" + "WIN" + "|"
							+ messageArray[1] + "|" + messageArray[2] + "|"
							+ messageArray[3] + "|" + messageArray[4]);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// 通知对手的客户端：输
				try {
					opponentServerAgentThread.dataOutputStream
							.writeUTF("<#GAME_OVER#>" + "LOSE" + "|"
									+ messageArray[1] + "|" + messageArray[2]
									+ "|" + messageArray[3] + "|"
									+ messageArray[4]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 如果是黑色赢了
			}
		} else if (myColor == Color.BLACK) {
			// 如果我自己也是黑色
			if (myColor == Color.BLACK) {
				// 通知自己的客户端：赢
				try {
					dataOutputStream.writeUTF("<#GAME_OVER#>" + "WIN" + "|"
							+ messageArray[1] + "|" + messageArray[2] + "|"
							+ messageArray[3] + "|" + messageArray[4]);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// 通知对手的客户端：输
				try {
					opponentServerAgentThread.dataOutputStream
							.writeUTF("<#GAME_OVER#>" + "LOSE" + "|"
									+ messageArray[1] + "|" + messageArray[2]
									+ "|" + messageArray[3] + "|"
									+ messageArray[4]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 先把对手清空
		opponentServerAgentThread.opponent = null;
		opponentServerAgentThread.table = null;
		opponentServerAgentThread.rule = null;
		opponentServerAgentThread.myColor = null;
		// 再把自己清空
		this.opponent = null;
		this.table = null;
		this.rule = null;
		this.myColor = null;
		// 刷新服务器列表
		serverThread.refreshServerList();
	}

	// 刷新客户端列表
	public void refreshClientList() {
		// 把列表整理成字符串
		String userListString = "";
		for (int i = 0; i < serverFrame.userOnlineList.size(); i++) {
			userListString = userListString
					+ serverFrame.userOnlineList.get(i).getName() + "|";
		}
		// 加上命令名
		userListString = "<#NICK_LIST#>" + userListString;
		// 去掉最后一个"|"
		userListString = userListString.substring(0,
				userListString.length() - 1);
		// 通知所有客户端
		for (int i = 0; i < serverFrame.userOnlineList.size(); i++) {
			ServerAgentThread serverAgentThread = (ServerAgentThread) serverFrame.userOnlineList
					.get(i);
			try {
				serverAgentThread.dataOutputStream.writeUTF(userListString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
