package nh.osgijms.domain.internal;

import static java.lang.String.format;
import nh.osgijms.domain.ContactService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  private static BundleContext _bundleContext;

  @Override
  public void start(BundleContext context) throws Exception {
    _bundleContext = context;

    ContactServiceImpl contactService = new ContactServiceImpl();
    log("Registering ContactService '%s'", contactService);
    context.registerService(ContactService.class.getName(), contactService, null);

  }

  @Override
  public void stop(BundleContext context) throws Exception {
    _bundleContext = null;
  }

  public static void log(String message, Object... params) {

    String prefix = format("[%s-%s@%s] ", _bundleContext.getBundle().getSymbolicName(), _bundleContext.getBundle()
        .getVersion(), Thread.currentThread().getName());
    String logMsg = format(message, params);
    System.out.println(prefix + logMsg);
  }

}
