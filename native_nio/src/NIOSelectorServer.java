import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectorServer {
    public static void main(String[] args) throws IOException {
        // 打开服务管道
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 绑定端口号，监听地址为：0.0.0.0
        channel.bind(new InetSocketAddress(9999));
        // 设置为非阻塞
        channel.configureBlocking(false);
        // 多路复用器
        Selector selector = Selector.open();
        // 将该channel注册到该selector上，并监听accept事件
        channel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端启动");
        while(true){
            // 检查事件是否就绪，非阻塞式(阻塞2000秒)
            if (selector.select(2000) == 0){
                continue;
            }
            // 获取触发事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    // 新的连接
                    SocketChannel socketChannel = channel.accept();
                    System.out.println("建立客户端链接:" + socketChannel);
                    socketChannel.configureBlocking(false);
                    // 将新链接也注册到selector
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                // 读事件
                if (selectionKey.isReadable()){
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int readCount = 1024;
                    String readStr = "";
                    while (readCount == 1024){
                        readCount = socketChannel.read(byteBuffer);
                        readStr += byteBuffer.toString();
                    }
                    System.out.println("客户端：" + readStr);
                    socketChannel.write(ByteBuffer.wrap("hello man !!!".getBytes(StandardCharsets.UTF_8)));
                }
                //防止同一个读事件被重复触发
                iterator.remove();
            }
        }
    }
}
