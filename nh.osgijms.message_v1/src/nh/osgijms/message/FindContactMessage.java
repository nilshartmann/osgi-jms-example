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

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class FindContactMessage {

  private final String _contactId;

  /**
   * @param contactId
   */
  public FindContactMessage(String contactId) {
    super();
    _contactId = contactId;
  }

  /**
   * @return the contactId
   */
  public String getContactId() {
    return _contactId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FindContactMessage [_contactId=" + _contactId + "]";
  }

}
