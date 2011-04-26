/**********************************************************************
 * Copyright (c) 2011 Nils Hartmann and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann initial implementation
 **********************************************************************/
package nh.osgijms.message;

import static java.lang.String.format;
import nh.osgijms.domain.Contact;
import nh.osgijms.domain.ContactService;
import nh.osgijms.framework.MessageHandler;
import nh.osgijms.message.internal.Activator;

/**
 * Version 1 of <b>FindContactMessageHandler</b>. This version interprets the message as the contactId of a contact that
 * should be returned
 * 
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class FindContactMessageHandler implements MessageHandler {

  /*
   * (non-Javadoc)
   * 
   * @see nh.osgijms.framework.MessageHandler#handleMessage(java.lang.String)
   */
  @Override
  public Object handleMessage(String message) {
    Activator.log("Message received '%s'", message);

    // Convert message to Message-Object
    FindContactMessage findContactMessage = new FindContactMessageConverter().convert(message);

    // Acquire ContactService
    ContactService contactService = getContactService();

    // Interpret message as conactId
    Contact contact = contactService.getContactById(findContactMessage.getContactId());

    Activator.log("Returning contact found: %s", contact);

    // Return contact to sender
    return contact;

  }

  /**
   * Returns a ContactService. If no ContactService is currently available (registered) it waits (forever) until a
   * ContactService gets available.
   * 
   * @return
   */
  public ContactService getContactService() {
    // This would normally handled by SpringIntegration or OSGi dynamic service
    // or another declarative dependency management handler

    return Activator.getContactService();
  }

  public String toString() {
    return format("[%s Version: 1]", getClass().getName());
  }

}
