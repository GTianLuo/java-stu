import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        boolean isConnected = socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
        if (!isConnected){
            System.out.println("连接失败");
        }

        for (int i = 0; i < 5; i++){
            socketChannel.write(ByteBuffer.wrap("你好！！！".getBytes(StandardCharsets.UTF_8)));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer);
            System.out.println(byteBuffer.toString());
        }
        socketChannel.close();
    }
}
