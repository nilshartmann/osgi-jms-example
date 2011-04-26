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
package nh.osgijms.framework;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public interface MessageHandler {

  /**
   * Behandelt eine Text-Message. Der Return-Wert (bzw. dessen toString()-Repräsentation wird als Antwort gesendet)
   * 
   * @param message
   */
  public Object handleMessage(String message);

}
