//服务器窗体类
package frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import thread.ServerAgentThread;
import thread.ServerThread;

public class ServerFrame extends JFrame {
	private static final long serialVersionUID = 7376063369309521938L;

	// 端口号Label
	public JLabel label_portNumber = new JLabel("端口号:");
	// 端口号TextField
	public JTextField textfield_portNumber = new JTextField("6788");
	// 开启服务器Button
	public JButton button_openServer = new JButton("开启服务器");
	// 关闭服务器Button
	public JButton button_closeServer = new JButton("关闭服务器");
	// 关闭程序Button
	public JButton button_closeProgram = new JButton("关闭程序");
	// 右Panel
	public JPanel panel_right = new JPanel();
	// 在线用户列表
	public JList<String> list_userOnline = new JList<String>();
	// 给列表加滚动条
	public JScrollPane scrollpane_userOnline = new JScrollPane(list_userOnline);
	// 分割面板
	public JSplitPane splitpane_split = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT, scrollpane_userOnline, panel_right);
	// ServerSocket
	public ServerSocket serverSocket;
	// ServerThread
	public ServerThread serverThread;
	// 存放当前所有在线用户的线程
	public Vector<Thread> userOnlineList = new Vector<Thread>();

	// 空参构造器
	public ServerFrame() {
		// 初始化组件
		initComponents();
		// 添加监听器
		addListeners();
		// 初始化窗体
		initFrame();
	}

	// 初始化组件
	public void initComponents() {
		// 设置右Panel为null布局
		panel_right.setLayout(null);

		// 设置Button和TextField等的位置和大小
		label_portNumber.setBounds(30, 20, 45, 30);
		textfield_portNumber.setBounds(30, 60, 45, 30);
		button_openServer.setBounds(20, 120, 100, 30);
		button_closeServer.setBounds(20, 170, 100, 30);
		button_closeProgram.setBounds(20, 220, 100, 30);

		// 设置可用性
		button_closeServer.setEnabled(false);

		// 加入右Panel
		panel_right.add(label_portNumber);
		panel_right.add(textfield_portNumber);
		panel_right.add(button_openServer);
		panel_right.add(button_closeServer);
		panel_right.add(button_closeProgram);
	}

	// 添加监听器
	public void addListeners() {
		// 开启服务器Button监听
		button_openServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 端口号
				int portNumber;
				// 先看看端口号能不能得到整数
				try {
					portNumber = Integer.parseInt(textfield_portNumber
							.getText().trim());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(ServerFrame.this,
							"端口号必须是整数！", "服务器开启错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 再看是不是0~65535之间的整数
				if (portNumber < 0 || portNumber > 65535) {
					JOptionPane.showMessageDialog(ServerFrame.this,
							"端口号必须是0~65535之间的整数", "服务器开启错误",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 能到这里说明端口号没问题，可以开启服务器了
				try {
					// 初始化ServerSocket
					serverSocket = new ServerSocket(portNumber);
					// 创建服务器线程
					serverThread = new ServerThread(ServerFrame.this);
					// 启动该服务器线程
					serverThread.start();
					// 设置可用性
					label_portNumber.setEnabled(false);
					textfield_portNumber.setEnabled(false);
					button_openServer.setEnabled(false);
					button_closeServer.setEnabled(true);
					// 通知
					JOptionPane.showMessageDialog(ServerFrame.this, "服务器已开启！",
							"通知", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(ServerFrame.this, "服务器开启失败！",
							"服务器开启错误", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});

		// 关闭服务器Button监听
		button_closeServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					for (int i = 0; i < userOnlineList.size(); i++) {
						ServerAgentThread tempServerAgentThread = (ServerAgentThread) userOnlineList
								.get(i);
						// 通知客户端
						tempServerAgentThread.dataOutputStream
								.writeUTF("<#SERVER_SHUT_DOWN#>");
						tempServerAgentThread.socket.close();
						tempServerAgentThread.flag = false;
						tempServerAgentThread = null;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				userOnlineList.clear();
				serverThread.refreshServerList();
				serverThread.flag = false;
				serverThread = null;
				try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				label_portNumber.setEnabled(true);
				textfield_portNumber.setEnabled(true);
				button_openServer.setEnabled(true);
				button_closeServer.setEnabled(false);
				JOptionPane.showMessageDialog(ServerFrame.this, "已关闭服务器！",
						"通知", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// 关闭程序Button监听
		button_closeProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					for (int i = 0; i < userOnlineList.size(); i++) {
						ServerAgentThread tempServerAgentThread = (ServerAgentThread) userOnlineList
								.get(i);
						tempServerAgentThread.dataOutputStream
								.writeUTF("<#SERVER_SHUT_DOWN#>");
						tempServerAgentThread.socket.close();
						tempServerAgentThread.flag = false;
						tempServerAgentThread = null;
					}
					System.exit(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// 点红叉时关闭程序
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					for (int i = 0; i < userOnlineList.size(); i++) {
						ServerAgentThread tempServerAgentThread = (ServerAgentThread) userOnlineList
								.get(i);
						tempServerAgentThread.dataOutputStream
								.writeUTF("<#SERVER_SHUT_DOWN#>");
						tempServerAgentThread.socket.close();
						tempServerAgentThread.flag = false;
						tempServerAgentThread = null;
					}
					System.exit(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	// 初始化窗体
	public void initFrame() {
		// 设置窗体标题
		this.setTitle("五子棋：服务器");
		// 拿到屏幕分辨率
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		// 显示在屏幕中央
		this.setBounds(screenWidth / 2 - 250, screenHeight / 2 - 200, 500, 400);
		// 设置分割面板分割线的位置
		splitpane_split.setDividerLocation(350);
		// 设置分割面板分割线的宽度
		splitpane_split.setDividerSize(5);
		// 把分割面板加入窗体
		this.add(splitpane_split);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	// 主方法
	public static void main(String[] args) {
		new ServerFrame();
	}
}
