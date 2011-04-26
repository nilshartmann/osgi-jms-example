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
package nh.osgijms.infrastructure.osgi;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class MessageHandlerDescriptor {

  public static MessageHandlerDescriptor fromString(String string) {
    String[] items = string.split(";");
    String handlerClassName = items[0];
    String destination = null;
    String messageName = null;
    String messageVersion = null;
    for (int i = 1; i < items.length; i++) {
      String tuple = items[i];
      String[] split = tuple.split("=\"|\"");
      String name = split[0].trim();
      String param = split[1].trim();

      if ("destination".equals(name)) {
        destination = param;
      } else if ("messageName".equals(name)) {
        messageName = param;

      } else if ("messageVersion".equals(name)) {
        messageVersion = param;
      }
    }
    return new MessageHandlerDescriptor(handlerClassName, messageName, messageVersion, destination);
  }

  private final String _handlerClassName;

  private final String _messageName;

  private final String _messageVersion;

  private final String _destination;

  /**
   * @param handlerClassName
   * @param messageName
   * @param messageVersion
   * @param destination
   */
  private MessageHandlerDescriptor(String handlerClassName, String messageName, String messageVersion,
      String destination) {
    super();
    _handlerClassName = handlerClassName;
    _messageName = messageName;
    _messageVersion = messageVersion;
    _destination = destination;
  }

  /**
   * @return the handlerClassName
   */
  public String getHandlerClassName() {
    return _handlerClassName;
  }

  /**
   * @return the messageName
   */
  public String getMessageName() {
    return _messageName;
  }

  /**
   * @return the messageVersion
   */
  public String getMessageVersion() {
    return _messageVersion;
  }

  /**
   * @return the destination
   */
  public String getDestination() {
    return _destination;
  }

}
