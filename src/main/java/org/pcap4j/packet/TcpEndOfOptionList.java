/*_##########################################################################
  _##
  _##  Copyright (C) 2012 Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet;

import java.io.ObjectStreamException;
import org.pcap4j.packet.TcpPacket.TcpOption;
import org.pcap4j.packet.namednumber.TcpOptionKind;
import org.pcap4j.util.ByteArrays;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.12
 */
public final class TcpEndOfOptionList implements TcpOption {

  /*
   *  +--------+
   *  |00000000|
   *  +--------+
   *    Kind=0
   */


  /**
   *
   */
  private static final long serialVersionUID = -4181756738827638374L;

  private static final TcpEndOfOptionList INSTANCE
    = new TcpEndOfOptionList();

  private static final TcpOptionKind kind = TcpOptionKind.END_OF_OPTION_LIST;

  private TcpEndOfOptionList() {}

  /**
   *
   * @return
   */
  public static TcpEndOfOptionList getInstance() { return INSTANCE; }

  /**
   *
   * @param rawData
   * @return
   */
  public static TcpEndOfOptionList newInstance(byte[] rawData) {
    if (rawData == null) {
      throw new NullPointerException("rawData may not be null");
    }
    if (rawData.length == 0) {
      throw new IllegalRawDataException(
              "The raw data length must be more than 0"
            );
    }
    if (rawData[0] != kind.value()) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("The kind must be: ")
        .append(kind.valueAsString())
        .append(" rawData: ")
        .append(ByteArrays.toHexString(rawData, " "));
      throw new IllegalRawDataException(sb.toString());
    }
    return INSTANCE;
  }

  public TcpOptionKind getKind() { return kind; }

  public int length() { return 1; }

  public byte[] getRawData() { return new byte[1]; }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[Kind: ")
      .append(kind);
    sb.append("]");
    return sb.toString();
  }

  // Override deserializer to keep singleton
  private Object readResolve() throws ObjectStreamException {
    return INSTANCE;
  }

}
