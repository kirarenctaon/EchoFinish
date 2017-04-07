package echo.server;

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
import javax.swing.JOptionPane;
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
	ServerSocket server;//접속감지용 소켓, 대화용 아님, ip필요없음 기다리기만 하면됨
	
	Thread thread;//서버 가동용 쓰레드. 왜? 메인은 프로그램은 운영해야하므로
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 10);
		bt_start = new JButton("가동");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	//서버 생성 및 가동
	public void startServer(){
		bt_start.setEnabled(false);//버튼을 한번누르면 다음부터는 비활성화, 쓰레드로 인해 대기상태가 아니라 의도적인 비활성화임
		
		try {
			port=Integer.parseInt(t_port.getText()); //이 문장에 대해서 Runtime Exception이 발생 할수 있음. 블럭으로 만들고 shift +alt +z 누르면 try/catch문 생김
			
			server=new ServerSocket(port);
			area.append("서버 준비 \n");
			
			//가동
			Socket socket=server.accept(); //실행부라 불리는 메인쓰레드는 절대 무한루프나 대지, 지연상태에 빠지게 해서는 안된다. 
			//왜? 실행부는 유저들의 이벤트를 감지한다거나 프로그램을 운영해야하므로 무한루프나 대기에 빠지만 본연의 역할을 할 수 없게 된다.
			//스마트폰 개발분야에서는 이와 같은 코드는 이와 같은 코드는 이미 컴파일 타임부터 에러 발생함
			//이벤트 감지는 메인본연의 역할이므로, 쓰레드를 만들어 서버가동하자
			area.append("서버 가동 \n");
			
			//클라이언트는 대화를 하기위해 접속한 것이므로 접속이 되는 순간 스트림을 얻어놓자
			buffr= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						
			String data;
			while(true){
				//클라이언트의 메시지 받기!
				data=buffr.readLine(); //보내는 쪽에서  +\n 을 같이 보내야지만 작동함
				//모니터링 목적으로 클라이언트말을 모니터에 뿔리기
				area.append("클라이언트의 말:"+data+"\n");
				
				//받은 메세지를 다시 서버에 보내기
				buffw.write(data+"\n"); //문장의 끝임을 알리기 위해 +\n을 표시해야함
				//버퍼를 비우기
				buffw.flush();
			}
		} catch (NumberFormatException e) {
			/*  예외의 종류
			 1. Checked Exception : 컴파일 타이밍에 잡아내는예외. 예외처리 강요되어 이클립스가 자동으로 try/catch문 생성해줌.
			 2. Runtime Exception(UnChecked Exception) : 실행 타이밍에 발생할 수 있는 예외로 잡아내지 못하면 비정상종료됨.
			    예외처리가 강요되지 않아서 사용자가  shift +alt +z 누르고  try/catch문을 직접 작성해야함.
			    
			    본 예제에서는 사용자가 실행타이밍에 포트번호에 숫자대신 문자를 넣으면 비정상 종료될 수 있기 때문에 "포트번호는 숫자로 넣으세요" 이런식으로 안내하도록 하자. */
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "포트는 숫자로 넣어주세요");
		} 	catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		thread = new Thread(){
			@Override
			public void run() {//독립실행시키고 싶은 코드를 run에 두자
				startServer();
			}
		};
		thread.start();
	}

	public static void main(String[] args) {
		new ServerMain();
	}

}
