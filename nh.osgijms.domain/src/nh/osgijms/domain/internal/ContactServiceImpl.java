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
package nh.osgijms.domain.internal;

import nh.osgijms.domain.Contact;
import nh.osgijms.domain.ContactService;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class ContactServiceImpl implements ContactService {

  /*
   * (non-Javadoc)
   * 
   * @see nh.osgijms.domain.ContactService#getContactById(java.lang.String)
   */
  @Override
  public Contact getContactById(String contactId) {

    return new Contact(contactId, "Mein Name", "Meine Anschrift");
  }

  /*
   * (non-Javadoc)
   * 
   * @see nh.osgijms.domain.ContactService#getContactByIdAndName(java.lang.String, java.lang.String)
   */
  @Override
  public Contact getContactByIdAndName(String contactId, String name) {
    return new Contact(contactId, name, "Meine andere Anschrift");
  }

}
