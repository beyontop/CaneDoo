package com.jetoff.web;

//import javax.websocket.Encoder;

/**
 * Created by Alain on 13/02/2018.
 * @OnMessage - public void handleCounter(int newvalue) {...}
 * @OnMessage - public void handleBoolean(Boolean b) {...}
 * @OnMessage - public void handleMessageAsStream( InputStream messageStream, Session session)
 * 				{
 * 					read from the messageStream
 *					until you ave consumed the
 * 					whole binary message
 * 				}
 *
 * @OnMessage - public void handleMessageInChunks(
 * String chunk, boolean isLast) {
 * reconstitute the message
 * from the chunks as they arrive
 *}
 * In general, an endpoint is accessible at:
 * -----------------------------------------
 * <ws or wss>://<hostname>:<port>/
 * web-app-context-path>/<websocket-path>?
 * query-string>
 *
 */
public class WebSoc
{
	WebSoc() {
	}

	public void sendObject( Object message ) //throws IOException, EncodeException
	{

	}
	public String encode( Object o ) //throws EncodeException
	{
		return "";
	}
}
