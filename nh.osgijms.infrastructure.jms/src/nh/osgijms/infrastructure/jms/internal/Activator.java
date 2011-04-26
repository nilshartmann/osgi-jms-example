package nh.osgijms.infrastructure.jms.internal;

import static java.lang.String.format;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import nh.osgijms.framework.MessageHandler;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  private static BundleContext         context;

  private Connection                   _connection;

  private Session                      _session;

  private MessageHandlerServiceTracker _messageHandlerServiceTracker;

  static BundleContext getContext() {
    return context;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;

    startJms();

    _messageHandlerServiceTracker = new MessageHandlerServiceTracker(bundleContext);
    _messageHandlerServiceTracker.open();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext bundleContext) throws Exception {
    try {
      _messageHandlerServiceTracker.close();
      stopJms();
    } finally {
      Activator.context = null;
    }
  }

  private void stopJms() throws Exception {
    log("Stopping JMS infrastructure...");
    try {
      if (_session != null) {
        _session.close();
      }
    } catch (Exception ex) {
      log("Error while stopping JMS session: %s", ex);
    }
    try {
      if (_connection != null) {
        _connection.close();
      }
    } catch (Exception ex) {
      log("Error while stopping JMS connection: %s", ex);
    }

    _session = null;
    _connection = null;

    log("JMS infrastructure stopped");

  }

  private void startJms() throws Exception {

    log("Starting JMS Infrastructure");
    // Create the connection.
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    _connection = connectionFactory.createConnection();

    // Create the session
    _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    _connection.start();
    log("JMS infrastructure started.");

  }

  /**
   * A {@link ServiceTracker} that tracks {@link MessageHandler} instances.
   * 
   * <p>
   * For each registeres {@link MessageHandler} there will be an appropriate JMS listener started. When a MessageHandler
   * gets unregistered the listener will be removed
   * 
   * @author Nils Hartmann (nils@nilshartmann.net)
   * 
   */
  class MessageHandlerServiceTracker extends ServiceTracker {
    public MessageHandlerServiceTracker(BundleContext bundleContext) {
      super(bundleContext, MessageHandler.class.getName(), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
     */
    @Override
    public Object addingService(ServiceReference reference) {
      MessageHandler messageHandler = (MessageHandler) super.addingService(reference);
      log("*** New MessageHandler registered ***");
      try {

        // Read JMS configuration from Service Properties
        String destinationName = (String) reference.getProperty("listenDestination");
        String selector = (String) reference.getProperty("jmsMessageSelector");

        log("Creating MessageConsumer with destination '%s' and selector '%s' for handler '%s'", destinationName,
            selector, messageHandler);

        // Grab destination
        Queue queue = _session.createQueue(destinationName);

        // Create JMS consumser
        MessageConsumer consumer = _session.createConsumer(queue, selector);

        // Create MessageHandlerAdapter that connects MessageListener and MessageHandler
        MessageHandlerAdapter messageHandlerAdapter = new MessageHandlerAdapter(messageHandler, consumer);
        consumer.setMessageListener(messageHandlerAdapter);

        log("*** MessageHandlerAdapter started ***");

        // Return the MessageHandlerAdapter. This way we will get back the reference from OSGi when the
        // service will be unregistered
        return messageHandlerAdapter;

      } catch (Exception ex) {
        ex.printStackTrace();
        log("Could not handle: %s", ex);
      }
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void removedService(ServiceReference reference, Object service) {
      if (service != null) {
        // A MessageHandler has been unregistered. Stopping the appropriate adapter...
        MessageHandlerAdapter adapter = (MessageHandlerAdapter) service;
        log("MessageHandlerAdapter '%s' unregistered", adapter);
        adapter.shutdown();
      }
      super.removedService(reference, service);
    }

  }

  class MessageHandlerAdapter implements MessageListener {
    private final MessageHandler  _messageHandler;

    private final MessageConsumer _consumer;

    /**
     * @param messageHandler
     * @param consumer
     */
    public MessageHandlerAdapter(MessageHandler messageHandler, MessageConsumer consumer) {
      super();
      _messageHandler = messageHandler;
      _consumer = consumer;
    }

    public void shutdown() {
      log("Shutdown MessageHandlerAdapter...");
      try {
        _consumer.close();
        log("MessageHandlerAdapter - consumer closed!");
      } catch (Exception ex) {
        ex.printStackTrace();
        log("Could not close adapter: %s", ex);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(Message message) {

      try {

        if (!(message instanceof TextMessage)) {
          log("Ignoring non-text message '%s'", message);
          return;
        }

        TextMessage textMessage = (TextMessage) message;
        String messageBody = textMessage.getText();

        String messageVersion = message.getStringProperty("MessageVersion");
        log("Invoking message handler '%s', with message '%s', Version: '%s', body: '%s'", _messageHandler,
            message.getStringProperty("MessageName"), messageVersion, messageBody);
        String result = String.valueOf(_messageHandler.handleMessage(messageBody));
        log("MessageHandler returned '%s", result);

        // Create reply message containing the result from the service invocation and
        // MessageVersion from the request
        Destination replyDestination = textMessage.getJMSReplyTo();
        MessageProducer producer = _session.createProducer(replyDestination);
        TextMessage resultMessage = _session.createTextMessage(result);
        resultMessage.setStringProperty("MessageVersion", messageVersion);

        log("Sending resultMessage '%s' to destination '%s'", resultMessage, replyDestination);
        producer.send(resultMessage);
        producer.close();

        // TODO Send result
      } catch (Exception ex) {
        ex.printStackTrace();
        log("Error whil processing message %s", message);
      }
    }
  }

  public static void log(String message, Object... params) {

    String prefix = format("[%s-%s@%s] ", context.getBundle().getSymbolicName(), context.getBundle().getVersion(),
        Thread.currentThread().getName());
    String logMsg = format(message, params);
    System.out.println(prefix + logMsg);
  }

}
