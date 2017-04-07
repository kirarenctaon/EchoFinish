package uni.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	JPanel p_north; 
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	ServerSocket server;
	Thread thread;//서버 운영쓰레도
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);

		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//서버를 가동한다. 
	public void startServer(){
		bt_start.setEnabled(false);//버튼 비활성화
		
		try {
			port=Integer.parseInt(t_port.getText());
			server=new ServerSocket(port); //여기까지는 서버 준비
			area.append("서버 생성\n");
			
			//server.accept(); 이렇게 하면 메인이 무한 대기에 빠지니까 쓰레드 만들자
			thread= new Thread(){
				@Override
				public void run() {
					//여러 접속자의 접속을 감지하라
					try {
						while(true){
							Socket socket=server.accept(); //서버는 접속에 집중하는 코드가 됨
							String ip=socket.getInetAddress().getHostAddress();
							area.append(ip+"접속자 발견\n");
							
							/* 그대로 아바타로 옮기기
							 * //대화를 나눌 스트림 뽑기
							BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
							BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							*/
							
							//접속자마다 아바타를 생성해주고 대화를 나눌 수 있도록 해주자
							/*String msg=buffr.readLine();
							area.append(msg+"\n");*/
							Avatar av=new Avatar(socket, area);
							av.start();

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		startServer();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}