package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_north;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	
	DBManager manager;
	ArrayList<Chat> list=new ArrayList<Chat>();
	
	int port=7777;	
	Socket socket;//대화용 소켓! 따라서 스트림도 뽑아낼 예정 
	String ip;
	ClientThread ct;
	
	public ClientMain() {
		
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port),10);
		t_input = new JTextField(10);
		bt_connect = new JButton("접속");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager=DBManager.getInstance(); //싱글톤 기법으로 생성
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		add(t_input, BorderLayout.SOUTH);
		
		loadIP();
		
		for(int i=0;i<list.size();i++){
			choice.add(list.get(i).getName());
		}
		
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		t_input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int key=e.getKeyCode();
				
				if(key==KeyEvent.VK_ENTER){
					String msg=t_input.getText();
					
					ct.send(msg);	//서버에 보내고
					t_input.setText("");//입력한 글씨 지우기
					
					
					//listen(); //서버에서 다시 받기//앞으로 실시간으로 listen하기 때문에 통째로 지워도 됨
				}
			}
		});
		
		setBounds(300, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//데이터베이스 가져오기
	public void loadIP(){
		Connection con=manager.getConnection();//db연동
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from chat order by chat_id asc";
		try {
			pstmt=con.prepareStatement(sql); //쿼리문의 종류가 select문이라서 resultset이 반환된다. 
			rs=pstmt.executeQuery(); //rs는 dto로 옮기꺼라서 스크롤이 있는 커서로 옮길 필요가 없다. 
			
			//rs의 모든 데이터를 dto로 옮기는 과정(맵핑)
			while(rs.next()){
				Chat dto=new Chat(); //이 chat이 레코드 한건을 받음
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				//여기까지가 레코드 한건을 담는과정

				list.add(dto); //이렇게 해서 2차원이 됨, 이제 rs는 필요없으니 죽이자. 
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			manager.disConnect(con);//con도 반납
		}
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();//선택한 인덱스를 구하려고 초이스로 형변환, 초이스 형에만 셀릭티느 인덱스가 있다. 
		int index=ch.getSelectedIndex();//내가 선택한 인덱스
		Chat chat=list.get(index);
		this.setTitle(chat.getIp());
		ip=chat.getIp();//멤버변수 ip에도 실제ip값을 대입
	}

	//서버에 접속을 시도하자!!
	public void connect(){
		//소켓생성시 접속이 발생함
		try {
			port=Integer.parseInt(t_port.getText());
			socket = new Socket(ip, port);
			
			/* 대화를 나누기 전에 스트림 얻어놓기
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		 	--> 이 업무를 동생에게 보내자 
			 
			 접속이 성공하면 buffr, buffw을 직접만들지 말고 그 정보를 담아두기만 하자
			 실시간으로 서버의 메세지를 청취하기 위해 쓰레드를 생성하여 대화업무를 다 맟겨버리자
			 따라서 종이컵&실의 보유자는 동생  */
			
			ct=new ClientThread(socket, area);
			ct.start();
			
			ct.send("안녕?");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		connect();
	}

	public static void main(String[] args) {
		new ClientMain();
	}

}
