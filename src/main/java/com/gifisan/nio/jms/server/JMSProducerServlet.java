package com.gifisan.nio.jms.server;

import java.io.OutputStream;

import com.gifisan.nio.component.BufferedOutputStream;
import com.gifisan.nio.jms.Message;
import com.gifisan.nio.server.Request;
import com.gifisan.nio.server.Response;
import com.gifisan.nio.server.session.Session;

public class JMSProducerServlet extends JMSServlet {

	private final byte	TRUE		= 'T';
	private final byte	FALSE	= 'F';

	public void accept(Request request, Response response, JMSSessionAttachment attachment) throws Exception {

		Session session = request.getSession();

		MQContext context = getMQContext();

		if (context.isLogined(session)) {
			
			OutputStream outputStream = session.getServerOutputStream();
			
			if (outputStream == null) {
				session.setServerOutputStream(new BufferedOutputStream());
				return;
			}
			
			Message message = context.parse(request);

			byte result = context.offerMessage(message) ? TRUE : FALSE;

			response.write(result);

		} else {

			response.write("用户未登录！");

		}

		response.flush();

	}

}
