package com.googlecode.mycontainer.test.ejb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.googlecode.mycontainer.test.SumService;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue") })
public class MessageDrivenBean implements MessageListener {

    @EJB
    private SumService sumService;

    @Resource(mappedName = "ConnectionFactory")
    private ConnectionFactory connectionFactory;

    public void onMessage(Message message) {
        ObjectMessage msg = (ObjectMessage) message;

        try {
            Integer[] values = (Integer[]) msg.getObject();
            Integer ret = sumService.divide(values[0], values[1]);

            sendReply(ret, msg.getJMSReplyTo());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendReply(Integer ret, Destination dest) throws JMSException {
        Connection conn = null;
        Session session = null;
        try {
            conn = connectionFactory.createConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(dest);
            producer.send(session.createObjectMessage((ret)));
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
    }
}
