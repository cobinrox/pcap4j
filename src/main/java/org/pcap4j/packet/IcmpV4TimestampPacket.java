/*_##########################################################################
  _##
  _##  Copyright (C) 2012  Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet;

import static org.pcap4j.util.ByteArrays.INT_SIZE_IN_BYTES;
import java.util.List;
import org.pcap4j.util.ByteArrays;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.11
 */
public final class IcmpV4TimestampPacket extends IcmpIdentifiablePacket {

  /**
   *
   */
  private static final long serialVersionUID = -122451430580609855L;

  private final IcmpV4TimestampHeader header;

  /**
   *
   * @param rawData
   * @return
   */
  public static IcmpV4TimestampPacket newPacket(byte[] rawData) {
    return new IcmpV4TimestampPacket(rawData);
  }

  private IcmpV4TimestampPacket(byte[] rawData) {
    this.header = new IcmpV4TimestampHeader(rawData);
  }

  private IcmpV4TimestampPacket(Builder builder) {
    super(builder);
    this.header = new IcmpV4TimestampHeader(builder);
  }

  @Override
  public IcmpV4TimestampHeader getHeader() {
    return header;
  }

  @Override
  public Builder getBuilder() {
    return new Builder(this);
  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.11
   */
  public static
  final class Builder extends org.pcap4j.packet.IcmpIdentifiablePacket.Builder {

    private int originateTimestamp;
    private int receiveTimestamp;
    private int transmitTimestamp;

    /**
     *
     */
    public Builder() {}

    private Builder(IcmpV4TimestampPacket packet) {
      super(packet);
      this.originateTimestamp = packet.header.originateTimestamp;
      this.receiveTimestamp = packet.header.receiveTimestamp;
      this.transmitTimestamp = packet.header.transmitTimestamp;
    }

    @Override
    public Builder identifier(short identifier) {
      super.identifier(identifier);
      return this;
    }

    @Override
    public Builder sequenceNumber(short sequenceNumber) {
      super.sequenceNumber(sequenceNumber);
      return this;
    }

    /**
     *
     * @param originateTimestamp
     * @return
     */
    public Builder originateTimestamp(int originateTimestamp) {
      this.originateTimestamp = originateTimestamp;
      return this;
    }

    /**
     *
     * @param receiveTimestamp
     * @return
     */
    public Builder receiveTimestamp(int receiveTimestamp) {
      this.receiveTimestamp = receiveTimestamp;
      return this;
    }

    /**
     *
     * @param transmitTimestamp
     * @return
     */
    public Builder transmitTimestamp(int transmitTimestamp) {
      this.transmitTimestamp = transmitTimestamp;
      return this;
    }

    @Override
    public IcmpV4TimestampPacket build() {
      return new IcmpV4TimestampPacket(this);
    }

  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.11
   */
  public static final class IcmpV4TimestampHeader extends IcmpIdentifiableHeader {

    /*
     * 0                               16                              32
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         Identifier            |       Sequence Number         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                 Originate Timestamp                           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                   Receive Timestamp                           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                  Transmit Timestamp                           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */

    /**
     *
     */
    private static final long serialVersionUID = -5997732668989705976L;

    private static final int ORIGINATE_TIMESTAMP_OFFSET
      = ICMP_IDENTIFIABLE_HEADER_SIZE;
    private static final int ORIGINATE_TIMESTAMP_SIZE
      = INT_SIZE_IN_BYTES;
    private static final int RECEIVE_TIMESTAMP_OFFSET
      = ORIGINATE_TIMESTAMP_OFFSET + ORIGINATE_TIMESTAMP_SIZE;
    private static final int RECEIVE_TIMESTAMP_SIZE
      = INT_SIZE_IN_BYTES;
    private static final int TRANSMIT_TIMESTAMP_OFFSET
      = RECEIVE_TIMESTAMP_OFFSET + RECEIVE_TIMESTAMP_SIZE;
    private static final int TRANSMIT_TIMESTAMP_SIZE
      = INT_SIZE_IN_BYTES;
    private static final int ICMPV4_TIMESTAMP_HEADER_SIZE
      = TRANSMIT_TIMESTAMP_OFFSET + TRANSMIT_TIMESTAMP_SIZE;

    private final int originateTimestamp;
    private final int receiveTimestamp;
    private final int transmitTimestamp;

    private IcmpV4TimestampHeader(byte[] rawData) {
      super(rawData);

      if (rawData.length < ICMPV4_TIMESTAMP_HEADER_SIZE) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("The data is too short to build an ")
          .append(getHeaderName())
          .append("(")
          .append(ICMPV4_TIMESTAMP_HEADER_SIZE)
          .append(" bytes). data: ")
          .append(ByteArrays.toHexString(rawData, " "));
        throw new IllegalRawDataException(sb.toString());
      }

      this.originateTimestamp
        = ByteArrays.getInt(rawData, ORIGINATE_TIMESTAMP_OFFSET);
      this.receiveTimestamp
        = ByteArrays.getInt(rawData, RECEIVE_TIMESTAMP_OFFSET);
      this.transmitTimestamp
        = ByteArrays.getInt(rawData, TRANSMIT_TIMESTAMP_OFFSET);
    }

    private IcmpV4TimestampHeader(Builder builder) {
      super(builder);
      this.originateTimestamp = builder.originateTimestamp;
      this.receiveTimestamp = builder.receiveTimestamp;
      this.transmitTimestamp = builder.transmitTimestamp;
    }

    /**
     *
     * @return
     */
    public int getOriginateTimestamp() {
      return originateTimestamp;
    }

    /**
     *
     * @return
     */
    public int getReceiveTimestamp() {
      return receiveTimestamp;
    }

    /**
     *
     * @return
     */
    public int getTransmitTimestamp() {
      return transmitTimestamp;
    }

    @Override
    protected List<byte[]> getRawFields() {
      List<byte[]> rawFields = super.getRawFields();
      rawFields.add(ByteArrays.toByteArray(originateTimestamp));
      rawFields.add(ByteArrays.toByteArray(receiveTimestamp));
      rawFields.add(ByteArrays.toByteArray(transmitTimestamp));
      return rawFields;
    }

    @Override
    public int length() {
      return ICMPV4_TIMESTAMP_HEADER_SIZE;
    }

    @Override
    protected String buildString() {
      StringBuilder sb = new StringBuilder();
      String ls = System.getProperty("line.separator");

      sb.append(super.buildString());
      sb.append("  Originate Timestamp: ")
        .append(originateTimestamp)
        .append(ls);
      sb.append("  Receive Timestamp: ")
        .append(receiveTimestamp)
        .append(ls);
      sb.append("  Transmit Timestamp: ")
        .append(transmitTimestamp)
        .append(ls);

      return sb.toString();
    }

    @Override
    protected String getHeaderName() {
      return "ICMPv4 Timestamp Header";
    }

  }

}
