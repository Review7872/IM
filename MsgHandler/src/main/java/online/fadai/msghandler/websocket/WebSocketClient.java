package online.fadai.msghandler.websocket;

import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

@ClientEndpoint
@Slf4j
public class WebSocketClient {
    private String path;
    public WebSocketClient(String path) {
        this.path = path;
    }

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    public void connect(URI uri) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, uri);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void closeConnection() {
        try {
            session.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        WebSocketMap.getWebSocketClientMap().remove(this.path);
        log.info("{}的连接已经断开",this.path);
    }


}

