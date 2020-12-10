//服务器线程类
package thread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import frame.ServerFrame;

public class ServerThread extends Thread {
	// 线程是否运行控制位
	public boolean flag = true;
	// 引用
	public ServerFrame serverFrame;
	public ServerSocket serverSocket;

	// 带参构造器
	public ServerThread(ServerFrame serverFrame) {
		this.serverFrame = serverFrame;
		serverSocket = serverFrame.serverSocket;
	}

	@Override
	public void run() {
		while (flag) {
			try {
				Socket socket = serverSocket.accept();
				ServerAgentThread serverAgentThread = new ServerAgentThread(
						serverFrame, this, socket);
				serverAgentThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 刷新服务器列表
	public void refreshServerList() {
		Vector<String> listString = new Vector<String>();
		for (int i = 0; i < serverFrame.userOnlineList.size(); i++) {
			// 每一条信息
			String eachString = "";
			// 拿到tempServerAgentThread
			ServerAgentThread tempServerAgentThread = (ServerAgentThread) serverFrame.userOnlineList
					.get(i);
			// IP
			eachString = tempServerAgentThread.socket.getInetAddress()
					.toString();
			// 昵称
			eachString = eachString + " : "
					+ serverFrame.userOnlineList.get(i).getName();
			// 设置状态
			if (tempServerAgentThread.opponent == null) {
				eachString = eachString + "(空闲)";
			} else {
				eachString = eachString + "(正在和"
						+ tempServerAgentThread.opponent + "游戏)";
			}
			// 把每条添加进去
			listString.add(eachString);
		}
		serverFrame.list_userOnline.setListData(listString);
	}
}
