/*_##########################################################################
  _##
  _##  Copyright (C) 2012 Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet;

import java.util.Arrays;
import org.pcap4j.packet.TcpPacket.TcpOption;
import org.pcap4j.packet.namednumber.TcpOptionKind;
import org.pcap4j.util.ByteArrays;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.12
 */
public final class IllegalTcpOption implements TcpOption {

  /**
   *
   */
  private static final long serialVersionUID = 4128600756828920489L;

  private final TcpOptionKind kind;
  private final byte[] rawData;

  /**
   *
   * @param rawData
   * @return
   */
  public static IllegalTcpOption newInstance(byte[] rawData) {
    return new IllegalTcpOption(rawData);
  }

  private IllegalTcpOption(byte[] rawData) {
    if (rawData == null) {
      throw new NullPointerException("rawData may not be null");
    }
    if (rawData.length == 0) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("The raw data has no data. rawData: ")
        .append(ByteArrays.toHexString(rawData, " "));
      throw new IllegalRawDataException(sb.toString());
    }

    this.kind = TcpOptionKind.getInstance(rawData[0]);
    this.rawData = new byte[rawData.length];
    System.arraycopy(rawData, 0, this.rawData, 0, rawData.length);
  }

  private IllegalTcpOption(Builder builder) {
    if (
        builder == null
     || builder.kind == null
     || builder.rawData == null
   ) {
     StringBuilder sb = new StringBuilder();
     sb.append("builder: ").append(builder)
       .append(" builder.kind: ").append(builder.kind)
       .append(" builder.rawData: ").append(builder.rawData);
     throw new NullPointerException(sb.toString());
   }

   this.kind = builder.kind;
   this.rawData = new byte[builder.rawData.length];
   System.arraycopy(
     builder.rawData, 0, this.rawData, 0, builder.rawData.length
   );
  }

  public TcpOptionKind getKind() { return kind; }

  public int length() { return rawData.length; }

  public byte[] getRawData() {
    byte[] copy = new byte[rawData.length];
    System.arraycopy(rawData, 0, copy, 0, copy.length);
    return copy;
  }

  /**
   *
   * @return
   */
  public Builder getBuilder() {
    return new Builder(this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[Kind: ")
      .append(kind)
      .append("] [Illegal Raw Data: 0x")
      .append(ByteArrays.toHexString(rawData, ""))
      .append("]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) { return true; }
    if (!this.getClass().isInstance(obj)) { return false; }
    return Arrays.equals((getClass().cast(obj)).getRawData(), getRawData());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(getRawData());
  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.12
   */
  public static final class Builder {

    private TcpOptionKind kind;
    private byte[] rawData;

    /**
     *
     */
    public Builder() {}

    private Builder(IllegalTcpOption option) {
      this.kind = option.kind;
      this.rawData = option.rawData;
    }

    /**
     *
     * @param kind
     * @return
     */
    public Builder kind(TcpOptionKind kind) {
      this.kind = kind;
      return this;
    }

    /**
     *
     * @param rawData
     * @return
     */
    public Builder rawData(byte[] rawData) {
      this.rawData = rawData;
      return this;
    }

    /**
     *
     * @return
     */
    public IllegalTcpOption build() {
      return new IllegalTcpOption(this);
    }

  }

}
