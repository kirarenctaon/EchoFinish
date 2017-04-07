/*키보드 입력시 마다 서버에 메세지를 보내고 다시 받아오게 처리하면 생기는 문제점?
키보드를 치지 않으면 서버에 메세지를 실시간 받아볼 수 없다. 
해결책:이벤트 발생과 상관없이 언제나 무한루프를 돌면서 서버의 메세지를 청취할 수 있는 별도의 실행부(쓰레드)를 만들자
*/
package echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	boolean flag=true;
	Socket socket;//buffr, buffw를 받기 위해 소켓을 생성자로 넘겨받자 
	BufferedReader buffr;
	BufferedWriter buffw;
	JTextArea area;
	
	public ClientThread(Socket socket, JTextArea area){
		this.area=area;
		this.socket=socket;
		
		try {
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//서버에 메세지 보내기!!(말하기)
	public void send(String msg){	
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//서버의 메세지 받아오기!!(듣기)
	public void listen(){
		String msg=null;	
		try {
			msg=buffr.readLine();//메세지 지연상태가 걸려서 이 상태로는 그렇게 빠르지 않음
			area.append(msg+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(flag){
			//듣자;
			listen();
		}
	}
	
}
