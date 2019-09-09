package org.touchsoft;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserControllerTest {
    private UserSession session;
    private UserController controller = new UserController();

    @Before
    public void setUp() throws Exception {
        session = new UserSession(null);
    }

    @Test
    public void registerAgent() {
        controller.registerUser(session, false, "a");
        assertEquals(false, session.getType());
        assertEquals("a", session.getUser().getNick());
    }

    @Test
    public void registerClient() {
        controller.registerUser(session, true, "b");
        assertEquals(true, session.getType());
        assertEquals("b", session.getUser().getNick());
    }


    @Test
    public void ifRegistered() {
        controller.registerUser(session, true, "b");
        assertTrue(controller.ifRegistered(session));
    }



    @Test
    public void getPair() {
        UserSession pair = new UserSession(null);
        session.setPair(pair);
        assertEquals(pair, controller.getPair(session));
    }

    @Test
    public void getPairAgent() {
        controller.registerUser(session, false, "a");
        controller.registerUser(new UserSession(null), false, "b");
        assertNull(controller.getPair(session));
    }


    @Test
    public void getPairNew() {
        controller.registerUser(session, true, "a");
        UserSession pair1 = new UserSession(null);
        controller.registerUser(pair1, false, "b");
        UserSession pair2 = new UserSession(null);
        controller.registerUser(pair2, false, "c");
        assertEquals(pair1, controller.getPair(session));
    }

    @Test
    public void getPairNull() {
        controller.registerUser(session, true, "a");
        assertNull(controller.getPair(session));
    }

    @Test
    public void leaveClient() {
        UserSession pair = new UserSession(null);
        session.setPair(pair);
        pair.setPair(session);
        session.setType(true);
        pair.setType(false);

        assertEquals(pair, controller.leave(session));
        assertNull(session.getPair());
    }

    @Test
    public void leaveAgent() {
        UserSession pair = new UserSession(null);
        session.setPair(pair);
        pair.setPair(session);
        session.setType(false);
        pair.setType(true);

        assertNull(controller.leave(session));
        assertNotNull(session.getPair());
    }

    @Test
    public void leaveNull() {
        assertNull(controller.leave(session));
        assertNull(session.getPair());
    }

    @Test
    public void removeUser() {
        controller.registerUser(session, false, "a");
        UserSession pair = new UserSession(null);
        controller.registerUser(pair, true, "b");
        controller.getPair(pair);

        assertEquals(pair, controller.removeUser(session));
        assertNull(pair.getPair());
    }


}