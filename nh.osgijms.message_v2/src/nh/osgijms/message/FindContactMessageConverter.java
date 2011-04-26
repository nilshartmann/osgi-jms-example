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

import nh.osgijms.message.internal.Activator;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class FindContactMessageConverter {

  public FindContactMessage convert(String message) {

    String[] parts = message.split("!");

    FindContactMessage convertedMessage = new FindContactMessage(parts[0], parts[1]);
    Activator.log("Converted '%s' to Message Object '%s'", message, convertedMessage);
    return convertedMessage;
  }

}
