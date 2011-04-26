package nh.osgijms.message.internal;

import static java.lang.String.format;
import nh.osgijms.domain.ContactService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  private static Activator      _instance;

  private BundleContext         _context;

  private ContactServiceTracker _contactServiceTracker;

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext bundleContext) throws Exception {
    Activator._instance = this;
    _context = bundleContext;

    // Create ServiceTracker to access ContactService
    _contactServiceTracker = new ContactServiceTracker(bundleContext);
    _contactServiceTracker.open();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext bundleContext) throws Exception {
    Activator._instance = null;
    _context = null;
    _contactServiceTracker.close();
  }

  public static void log(String message, Object... params) {

    String prefix = format("[%s-%s@%s] ", _instance._context.getBundle().getSymbolicName(), _instance._context
        .getBundle().getVersion(), Thread.currentThread().getName());
    String logMsg = format(message, params);
    System.out.println(prefix + logMsg);
  }

  public static ContactService getContactService() {
    return _instance._contactServiceTracker.getContactService();
  }

  class ContactServiceTracker extends ServiceTracker {
    public ContactServiceTracker(BundleContext context) {
      super(context, ContactService.class.getName(), null);
    }

    /**
     * This methods returns the registered contactService. It never returns null. If no contact service is available, it
     * waits (forever) until a contact service gets registered
     * 
     * @return the contactService. never null
     */
    public ContactService getContactService() {
      try {
        log("Waiting for ContactService");
        ContactService contactService = (ContactService) waitForService(0);
        log("...ContactService received: %s", contactService);
        return contactService;
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
