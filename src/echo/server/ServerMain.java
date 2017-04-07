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
	ServerSocket server;//���Ӱ����� ����, ��ȭ�� �ƴ�, ip�ʿ���� ��ٸ��⸸ �ϸ��
	
	Thread thread;//���� ������ ������. ��? ������ ���α׷��� ��ؾ��ϹǷ�
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port), 10);
		bt_start = new JButton("����");
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

	//���� ���� �� ����
	public void startServer(){
		bt_start.setEnabled(false);//��ư�� �ѹ������� �������ʹ� ��Ȱ��ȭ, ������� ���� �����°� �ƴ϶� �ǵ����� ��Ȱ��ȭ��
		
		try {
			port=Integer.parseInt(t_port.getText()); //�� ���忡 ���ؼ� Runtime Exception�� �߻� �Ҽ� ����. ������ ����� shift +alt +z ������ try/catch�� ����
			
			server=new ServerSocket(port);
			area.append("���� �غ� \n");
			
			//����
			Socket socket=server.accept(); //����ζ� �Ҹ��� ���ξ������ ���� ���ѷ����� ����, �������¿� ������ �ؼ��� �ȵȴ�. 
			//��? ����δ� �������� �̺�Ʈ�� �����Ѵٰų� ���α׷��� ��ؾ��ϹǷ� ���ѷ����� ��⿡ ������ ������ ������ �� �� ���� �ȴ�.
			//����Ʈ�� ���ߺо߿����� �̿� ���� �ڵ�� �̿� ���� �ڵ�� �̹� ������ Ÿ�Ӻ��� ���� �߻���
			//�̺�Ʈ ������ ���κ����� �����̹Ƿ�, �����带 ����� ������������
			area.append("���� ���� \n");
			
			//Ŭ���̾�Ʈ�� ��ȭ�� �ϱ����� ������ ���̹Ƿ� ������ �Ǵ� ���� ��Ʈ���� ������
			buffr= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						
			String data;
			while(true){
				//Ŭ���̾�Ʈ�� �޽��� �ޱ�!
				data=buffr.readLine(); //������ �ʿ���  +\n �� ���� ���������� �۵���
				//����͸� �������� Ŭ���̾�Ʈ���� ����Ϳ� �Ը���
				area.append("Ŭ���̾�Ʈ�� ��:"+data+"\n");
				
				//���� �޼����� �ٽ� ������ ������
				buffw.write(data+"\n"); //������ ������ �˸��� ���� +\n�� ǥ���ؾ���
				//���۸� ����
				buffw.flush();
			}
		} catch (NumberFormatException e) {
			/*  ������ ����
			 1. Checked Exception : ������ Ÿ�ֿ̹� ��Ƴ��¿���. ����ó�� ����Ǿ� ��Ŭ������ �ڵ����� try/catch�� ��������.
			 2. Runtime Exception(UnChecked Exception) : ���� Ÿ�ֿ̹� �߻��� �� �ִ� ���ܷ� ��Ƴ��� ���ϸ� �����������.
			    ����ó���� ������� �ʾƼ� ����ڰ�  shift +alt +z ������  try/catch���� ���� �ۼ��ؾ���.
			    
			    �� ���������� ����ڰ� ����Ÿ�ֿ̹� ��Ʈ��ȣ�� ���ڴ�� ���ڸ� ������ ������ ����� �� �ֱ� ������ "��Ʈ��ȣ�� ���ڷ� ��������" �̷������� �ȳ��ϵ��� ����. */
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "��Ʈ�� ���ڷ� �־��ּ���");
		} 	catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		thread = new Thread(){
			@Override
			public void run() {//���������Ű�� ���� �ڵ带 run�� ����
				startServer();
			}
		};
		thread.start();
	}

	public static void main(String[] args) {
		new ServerMain();
	}

}
