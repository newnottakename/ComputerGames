package nl.tudelft.ti2806.riverrush.network.protocol;

import nl.tudelft.ti2806.riverrush.domain.entity.AbstractAnimal;
import nl.tudelft.ti2806.riverrush.domain.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Tests for the basic protocol.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicBasicProtocolTest {

    /**
     * Stub for the event to send over the network.
     */
    private Event eventStub;

    /**
     * Mock for an event.
     */
    @Mock
    private Event eventMock;

    /**
     * Class under test.
     */
    private Protocol protocol;

    /**
     * String representation of a valid serialized event.
     */
    private String stubEventSerialized;

    /**
     * String representation of an invalid serialized event.
     */
    private String unknownEventSerialized;

    /**
     * Initialize the protoco.
     */
    @Before
    public void setUp() {
        this.protocol = new BasicProtocol(0);
        this.eventStub = new StubEvent();
        this.stubEventSerialized = this.protocol.getEventTypeFieldKey()
            + this.protocol.getKeyValueSeperator()
            + this.eventStub.getClass().getSimpleName();

        this.unknownEventSerialized = this.protocol.getEventTypeFieldKey()
            + this.protocol.getKeyValueSeperator()
            + "SomeUnknownEventClass";
    }

    /**
     * serialize calls and event.
     *
     * @throws InvalidActionException - If an invalid action occurs.
     */
    @Test
    public void serializeCallsEvent() throws InvalidActionException {
        this.protocol.serialize(this.eventMock);
        verify(this.eventMock).serialize(this.protocol);
    }

    /**
     * Tests a register.
     */
    @Test
    public void testRegister() {
        this.protocol.registerNetworkMessage(StubEvent.class,
            () -> this.eventStub);
        assertTrue(this.protocol.isRegistered(StubEvent.class));
    }

    /**
     * A deserialize test.
     *
     * @throws InvalidProtocolException -
     * @throws InvalidActionException   -
     */
    @Test
    public void testDeserializeActionOnly() throws InvalidProtocolException,
        InvalidActionException {
        Event expected = new StubEvent();
        this.protocol.registerNetworkMessage(StubEvent.class, () -> expected);
        Event actualEvent = this.protocol.deserialize(this.stubEventSerialized);
        assertEquals(expected, actualEvent);
    }

    /**
     * Tests deserialize with a field.
     *
     * @throws InvalidProtocolException -
     * @throws InvalidActionException   -
     */
    @Test
    public void testDeserializeWithField() throws InvalidProtocolException,
        InvalidActionException {
        this.protocol.registerNetworkMessage(StubEvent.class, StubEvent::new);

        final String expectedField = "field"
            + this.protocol.getKeyValueSeperator() + "HelloWorld"
            + this.protocol.getPairSeperator();
        Event networkMessage = this.protocol.deserialize(expectedField
            + this.stubEventSerialized);
        assertTrue(networkMessage instanceof StubEvent);
        assertEquals("HelloWorld", ((StubEvent) networkMessage).getField());
    }

    /**
     * Tests serialize with a field.
     */
    @Test
    public void testSerializeWithField() {
        StubEvent event = new StubEvent();
        event.setField("HelloWorld");

        final String expectedField = "field"
            + this.protocol.getKeyValueSeperator() + "HelloWorld"
            + this.protocol.getPairSeperator();

        final String actualField = protocol.serialize(event);

        assertTrue(actualField.contains(expectedField));
        assertTrue(actualField.contains(this.stubEventSerialized));
    }

    /**
     * Tests deserialize on an invalid protocol.
     *
     * @throws InvalidProtocolException -
     * @throws InvalidActionException   -
     */
    @Test(expected = InvalidProtocolException.class)
    public void testDeserializeInvalidProtocol()
        throws InvalidProtocolException, InvalidActionException {
        this.protocol.deserialize("key=a=value");
    }

    /**
     * Tests deserialize on an invalid action.
     *
     * @throws InvalidProtocolException -
     * @throws InvalidActionException   -
     */
    @Test(expected = InvalidActionException.class)
    public void testDeserializeInvalidAction() throws InvalidProtocolException,
        InvalidActionException {
        this.protocol.deserialize(this.unknownEventSerialized);
    }

    /**
     * Stub event for testing.
     */
    private class StubEvent implements Event {
        /**
         * A dummy field.
         */
        private String field;


        @Override
        public String serialize(final Protocol p) {
            return "field" + p.getKeyValueSeperator() + this.field;
        }

        @Override
        public Event deserialize(final Map<String, String> keyValuePairs) {
            this.field = keyValuePairs.get("field");
            return this;
        }

        @Override
        public void setAnimal(Integer anPlayerID) {
         //FIXME
        }

        @Override
        public Integer getAnimal() {
            // Has to be empty
            return null;
        }

        public String getField() {
            return this.field;
        }

        public void setField(final String f) {
            this.field = f;
        }
    }
}
