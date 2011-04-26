package nh.osgijms.infrastructure.osgi;

import static java.lang.String.format;

import java.util.Properties;

import nh.osgijms.framework.MessageHandler;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

public class Activator implements BundleActivator {

  private static BundleContext context;

  static BundleContext getContext() {
    return context;
  }

  private MessageHandlerBundleTracker _messageHandlerBundleTracker;

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
    _messageHandlerBundleTracker = new MessageHandlerBundleTracker(bundleContext);
    _messageHandlerBundleTracker.open();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext bundleContext) throws Exception {
    _messageHandlerBundleTracker.close();
    Activator.context = null;
  }

  class MessageHandlerBundleTracker extends BundleTracker {
    public MessageHandlerBundleTracker(BundleContext bundleContext) {
      super(bundleContext, Bundle.ACTIVE, null);
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
      // A new bundle has been activated. Let's see if it contains MessageHandlers
      // that should be registered

      String messageHandlerHeader = (String) bundle.getHeaders().get("MessageHandler");
      if (messageHandlerHeader != null) {
        log("Detected MessageHandlers header in bundle %s-%s: %s", bundle.getSymbolicName(), bundle.getVersion(),
            messageHandlerHeader);

        try {
          MessageHandlerDescriptor descriptor = MessageHandlerDescriptor.fromString(messageHandlerHeader);

          // Instantiate class

          Class<?> clazz = bundle.loadClass(descriptor.getHandlerClassName());
          MessageHandler messageHandler = (MessageHandler) clazz.newInstance();

          // Create message selector expression as required by listener

          String messageSelector = format("MessageName='%s' AND MessageVersion='%s'", descriptor.getMessageName(),
              descriptor.getMessageVersion());

          // Create service properties
          Properties serviceProperties = new Properties();
          serviceProperties.put("jmsMessageSelector", messageSelector);
          serviceProperties.put("listenDestination", descriptor.getDestination());

          log("Registering MessageHandler %s with properties: %s", messageHandler, serviceProperties);

          // Register service
          bundle.getBundleContext().registerService(MessageHandler.class.getName(), messageHandler, serviceProperties);

        } catch (Exception ex) {
          ex.printStackTrace();
          log("Could not process header '%s': %s", messageHandlerHeader, ex);
        }
      }

      // TODO Auto-generated method stub
      return super.addingBundle(bundle, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTracker#removedBundle(org.osgi.framework.Bundle, org.osgi.framework.BundleEvent,
     * java.lang.Object)
     */
    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
      // TODO Auto-generated method stub
      super.removedBundle(bundle, event, object);
    }

  }

  public static void log(String message, Object... params) {

    String prefix = format("[%s-%s@%s] ", context.getBundle().getSymbolicName(), context.getBundle().getVersion(),
        Thread.currentThread().getName());
    String logMsg = format(message, params);
    System.out.println(prefix + logMsg);
  }

}
