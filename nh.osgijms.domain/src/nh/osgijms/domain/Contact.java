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
package nh.osgijms.domain;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class Contact {

  private final String _id;

  private final String _name;

  private final String _address;

  /**
   * @param id
   * @param name
   * @param address
   */
  public Contact(String id, String name, String address) {
    super();
    _id = id;
    _name = name;
    _address = address;
  }

  /**
   * @return the id
   */
  public String getId() {
    return _id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return _name;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return _address;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Contact [_id=" + _id + ", _name=" + _name + ", _address=" + _address + "]";
  }

}
