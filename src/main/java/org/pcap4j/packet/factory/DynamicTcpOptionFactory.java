/*_##########################################################################
  _##
  _##  Copyright (C) 2012 Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IllegalTcpOption;
import org.pcap4j.packet.PacketPropertiesLoader;
import org.pcap4j.packet.TcpPacket.TcpOption;
import org.pcap4j.packet.namednumber.TcpOptionKind;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.11
 */
public final class
DynamicTcpOptionFactory
implements ClassifiedDataFactory<TcpOption, TcpOptionKind> {

  private static final DynamicTcpOptionFactory INSTANCE
    = new DynamicTcpOptionFactory();

  private DynamicTcpOptionFactory() {}

  /**
   *
   * @return
   */
  public static DynamicTcpOptionFactory getInstance() { return INSTANCE; }

  public TcpOption newData(byte[] rawData, TcpOptionKind number) {
    if (number == null) {
      throw new NullPointerException(" number: " + number);
    }

    Class<? extends TcpOption> dataClass
      = PacketPropertiesLoader.getInstance().getTcpOptionClass(number);
    return newData(rawData, dataClass);
  }

  public TcpOption newData(byte[] rawData) {
    Class<? extends TcpOption> dataClass
      = PacketPropertiesLoader.getInstance().getUnknownTcpOptionClass();
    return newData(rawData, dataClass);
  }

  /**
   *
   * @param rawData
   * @param dataClass
   * @return
   */
  public TcpOption newData(byte[] rawData, Class<? extends TcpOption> dataClass) {
    if (rawData == null || dataClass == null) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("rawData: ")
        .append(rawData)
        .append(" dataClass: ")
        .append(dataClass);
      throw new NullPointerException(sb.toString());
    }

    try {
      Method newInstance = dataClass.getMethod("newInstance", byte[].class);
      return (TcpOption)newInstance.invoke(null, rawData);
    } catch (SecurityException e) {
      throw new IllegalStateException(e);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof IllegalRawDataException) {
        return IllegalTcpOption.newInstance(rawData);
      }
      throw new IllegalStateException(e.getTargetException());
    }
  }

}
