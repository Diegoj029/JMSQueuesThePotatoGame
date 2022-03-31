package mx.itam.packages.potato;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Jugador extends Thread{

    // A static manner to link jms resources with java objects
    //@Resource(mappedName = "jms/TestConnectionFactory")
    //private static ConnectionFactory connectionFactory;
    //@Resource(mappedName = "jms/TestQueue")
    //private static Queue queue;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    // default broker URL is : tcp://localhost:61616"
    private String subjectIN = "QUEUE"; // Queue Name. You can create any/many queue names as per your requirement.
    private String subjectOUT = "QUEUE"; // Queue Name. You can create any/many queue names as per your requirement.

    private Papa papa;
    private boolean perdi = false;

    public Jugador(int envio, int recibe){
        this.subjectIN = this.subjectIN + recibe;
        this.subjectOUT = this.subjectOUT + envio;
        this.papa = new Papa("papa"+envio+recibe);
    }

    public void run(){
        enviaPapa(this.papa);
        loopJuego();
    }

    public void enviaPapa(Papa papa) {
        MessageProducer messageProducer;
        ObjectMessage objectMessage;
        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(this.subjectOUT);

            messageProducer = session.createProducer(destination);
            objectMessage = session.createObjectMessage();

            objectMessage.setObject(papa);
            System.out.println("Sending the following message: " + objectMessage.getObject().toString());
            messageProducer.send(objectMessage);

            messageProducer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void loopJuego() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connectionFactory.setTrustAllPackages(true);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(this.subjectIN);

            MessageConsumer messageConsumer = session.createConsumer(destination);

            while (!this.perdi) {
                System.out.println("Waiting for messages...");
                ObjectMessage objectMessage = (ObjectMessage) messageConsumer.receive();
                Papa papa = null;
                if (objectMessage != null) {
                    papa = (Papa) objectMessage.getObject();
                    System.out.print("Received the following message: ");
                    System.out.println(papa.toString());
                    System.out.println();
                    papa.restarTiempo(1);
                    enviaPapa(papa);
                    Thread.sleep(100);
                }
                if (objectMessage.getObject() != null && papa.cayoPapa()) {
                    this.perdi = true;
                }
            }

            messageConsumer.close();
            session.close();
            connection.close();

        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
