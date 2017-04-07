package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
	Socket socket;//��ȭ�� ����! ���� ��Ʈ���� �̾Ƴ� ���� 
	String ip;
	
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ClientMain() {
		
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port),10);
		t_input = new JTextField(10);
		bt_connect = new JButton("����");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager=DBManager.getInstance(); //�̱��� ������� ����
		
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
		
		setBounds(300, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//�����ͺ��̽� ��������
	public void loadIP(){
		Connection con=manager.getConnection();//db����
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from chat order by chat_id asc";
		try {
			pstmt=con.prepareStatement(sql); //�������� ������ select���̶� resultset�� ��ȯ�ȴ�. 
			rs=pstmt.executeQuery(); //rs�� dto�� �űⲨ�� ��ũ���� �ִ� Ŀ���� �ű� �ʿ䰡 ����. 
			
			//rs�� ��� �����͸� dto�� �ű�� ����(����)
			while(rs.next()){
				Chat dto=new Chat(); //�� chat�� ���ڵ� �Ѱ��� ����
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				//��������� ���ڵ� �Ѱ��� ��°���

				list.add(dto); //�̷��� �ؼ� 2������ ��, ���� rs�� �ʿ������ ������. 
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
			manager.disConnect(con);//con�� �ݳ�
		}
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();//������ �ε����� ���Ϸ��� ���̽��� ����ȯ, ���̽� ������ ����Ƽ�� �ε����� �ִ�. 
		int index=ch.getSelectedIndex();//���� ������ �ε���
		Chat chat=list.get(index);
		this.setTitle(chat.getIp());
		ip=chat.getIp();//������� ip���� ����ip���� ����
	}

	//������ ������ �õ�����!!
	public void connect(){
		//���ϻ����� ������ �߻���
		try {
			port=Integer.parseInt(t_port.getText());
			socket = new Socket(ip, port);
			
			//��ȭ�� ������ ���� ��Ʈ�� ������
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			buffw.write("�ȳ�?\n");
			buffw.flush();
			
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
