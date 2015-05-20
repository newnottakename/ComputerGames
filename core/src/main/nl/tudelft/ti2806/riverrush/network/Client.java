package nl.tudelft.ti2806.riverrush.network;

import nl.tudelft.ti2806.riverrush.controller.Controller;
import nl.tudelft.ti2806.riverrush.domain.event.Event;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.network.event.RenderJoinEvent;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Web socket client for connecting to the backend endpoint.
 */
public class Client extends WebSocketClient {

    private final Protocol protocol;

    private final EventDispatcher eventDispatcher;

    private final Controller controller;

    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the
     * specified URI. The channel does not attampt to connect automatically. You
     * must call {@code connect} first to initiate the socket connection.
     *
     * @param host - The remote hostname of the server.
     */
    public Client(final String host, Protocol protocol,
                  final EventDispatcher dispatcher,
                  final Controller controller) throws URISyntaxException {
        super(new URI("http://" + host + ":" + protocol.getPortNumber()), new Draft_17());
        this.eventDispatcher = dispatcher;
        this.controller = controller;
        this.protocol = protocol;
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        this.sendEvent(new RenderJoinEvent(), null);
    }

    /**
     * Send a domain event to the server.
     *
     * @param event - The event to send.
     * @param d     - The eventDispatcher that dispatched this event.
     */
    private void sendEvent(final Event event, final EventDispatcher d) {
        this.getConnection().send(protocol.serialize(event));
    }

    @Override
    public void onMessage(final String message) {
        Event event = this.protocol.deserialize(message);
        this.controller.onSocketMessage(event);
    }

    @Override
    public void onClose(final int code, final String reason,
                        final boolean remote) {
        System.out.println("Connection closed.");
    }

    @Override
    public void onError(final Exception ex) {
        System.out.println("Connection failed");
        ex.printStackTrace();
    }

}
