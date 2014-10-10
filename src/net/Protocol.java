package net;

import java.io.Serializable;

/**
 * Protocols in use by both server and client
 * @author Muli Yulzary
 *
 */
public class Protocol implements Serializable {
    private static final long serialVersionUID = -274869821440460706L;

    public enum Type {
	ADD_LAUNCHER,
	FIRE_MISSILE,
	EXIT,
	REQUEST_AVAILABLE_LAUNCHERS,
	REQUEST_AVAILABLE_DESTINATIONS, 
	REQUEST_ENEMY_INVENTORY
    }

    private Type type;
    private Object[] content;

    public Protocol(Type type) {
	this.type = type;
    }

    public Protocol(Type type, Object... o) {
	this(type);
	this.content = o;
    }

    public void setContent(Object... content) {
	this.content = content;
    }

    public Type getType() {
	return type;
    }

    public Object[] getContent() {
	return content;
    }
}
