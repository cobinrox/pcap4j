/*_##########################################################################
  _##
  _##  Copyright (C) 2012  Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.pcap4j.packet.factory.ClassifiedDataFactories;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpV6RoutingHeaderType;
import org.pcap4j.util.ByteArrays;
import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.10
 */
public final class IpV6ExtRoutingPacket extends AbstractPacket {

  /**
   *
   */
  private static final long serialVersionUID = -4454872145403587056L;

  private final IpV6ExtRoutingHeader header;
  private final Packet payload;

  /**
   *
   * @param rawData
   * @return
   */
  public static IpV6ExtRoutingPacket newPacket(byte[] rawData) {
    return new IpV6ExtRoutingPacket(rawData);
  }

  private IpV6ExtRoutingPacket(byte[] rawData) {
    this.header = new IpV6ExtRoutingHeader(rawData);

    byte[] rawPayload
      = ByteArrays.getSubArray(
          rawData,
          header.length(),
          rawData.length - header.length()
        );

    this.payload
      = PacketFactories.getFactory(IpNumber.class)
          .newPacket(rawPayload, header.getNextHeader());
  }

  private IpV6ExtRoutingPacket(Builder builder) {
    if (
         builder == null
      || builder.nextHeader == null
      || builder.data == null
      || builder.payloadBuilder == null
    ) {
      StringBuilder sb = new StringBuilder();
      sb.append("builder: ").append(builder)
        .append(" builder.nextHeader: ").append(builder.nextHeader)
        .append(" builder.data: ").append(builder.data)
        .append(" builder.payloadBuilder: ").append(builder.payloadBuilder);
      throw new NullPointerException(sb.toString());
    }

    this.payload = builder.payloadBuilder.build();
    this.header = new IpV6ExtRoutingHeader(builder);
  }

  @Override
  public IpV6ExtRoutingHeader getHeader() {
    return header;
  }

  @Override
  public Packet getPayload() {
    return payload;
  }

  @Override
  public Builder getBuilder() {
    return new Builder(this);
  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.10
   */
  public static final
  class Builder extends AbstractBuilder
  implements LengthBuilder<IpV6ExtRoutingPacket> {

    private IpNumber nextHeader;
    private byte hdrExtLen;
    private IpV6RoutingHeaderType routingType;
    private byte segmentsLeft;
    private IpV6RoutingData data;
    private Packet.Builder payloadBuilder;
    private boolean correctLengthAtBuild;

    /**
     *
     */
    public Builder() {}

    /**
     *
     * @param packet
     */
    public Builder(IpV6ExtRoutingPacket packet) {
      this.nextHeader = packet.header.nextHeader;
      this.hdrExtLen = packet.header.hdrExtLen;
      this.routingType = packet.header.routingType;
      this.segmentsLeft = packet.header.segmentsLeft;
      this.data = packet.header.data;
      this.payloadBuilder = packet.payload.getBuilder();
    }

    /**
     *
     * @param nextHeader
     * @return
     */
    public Builder nextHeader(IpNumber nextHeader) {
      this.nextHeader = nextHeader;
      return this;
    }

    /**
     *
     * @param hdrExtLen
     * @return
     */
    public Builder hdrExtLen(byte hdrExtLen) {
      this.hdrExtLen = hdrExtLen;
      return this;
    }

    /**
     *
     * @param routingType
     * @return
     */
    public Builder routingType(IpV6RoutingHeaderType routingType) {
      this.routingType = routingType;
      return this;
    }

    /**
     *
     * @param segmentsLeft
     * @return
     */
    public Builder segmentsLeft(byte segmentsLeft) {
      this.segmentsLeft = segmentsLeft;
      return this;
    }

    /**
     *
     * @param data
     * @return
     */
    public Builder data(IpV6RoutingData data) {
      this.data = data;
      return this;
    }

    @Override
    public Builder payloadBuilder(Packet.Builder payloadBuilder) {
      this.payloadBuilder = payloadBuilder;
      return this;
    }

    @Override
    public Packet.Builder getPayloadBuilder() {
      return payloadBuilder;
    }

    public Builder correctLengthAtBuild(boolean correctLengthAtBuild) {
      this.correctLengthAtBuild = correctLengthAtBuild;
      return this;
    }

    @Override
    public IpV6ExtRoutingPacket build() {
      return new IpV6ExtRoutingPacket(this);
    }

  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.10
   */
  public static final class IpV6ExtRoutingHeader extends AbstractHeader {

    /*
     *   0                              16                            31
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *  |  Next Header  |  Hdr Ext Len  |  Routing Type | Segments Left |
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *  |                                                               |
     *  .                                                               .
     *  .                       type-specific data                      .
     *  .                                                               .
     *  |                                                               |
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     */

    /**
     *
     */
    private static final long serialVersionUID = 773193868313443164L;

    private static final int NEXT_HEADER_OFFSET
      = 0;
    private static final int NEXT_HEADER_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int HDR_EXT_LEN_OFFSET
      = NEXT_HEADER_OFFSET + NEXT_HEADER_SIZE;
    private static final int HDR_EXT_LEN_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int ROUTING_TYPE_OFFSET
      = HDR_EXT_LEN_OFFSET + HDR_EXT_LEN_SIZE;
    private static final int ROUTING_TYPE_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int SEGMENTS_LEFT_OFFSET
      = ROUTING_TYPE_OFFSET + ROUTING_TYPE_SIZE;
    private static final int SEGMENTS_LEFT_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int TYPE_SPECIFIC_DATA_OFFSET
      = SEGMENTS_LEFT_OFFSET + SEGMENTS_LEFT_SIZE;

    private final IpNumber nextHeader;
    private final byte hdrExtLen;
    private final IpV6RoutingHeaderType routingType;
    private final byte segmentsLeft;
    private final IpV6RoutingData data;

    private IpV6ExtRoutingHeader(byte[] rawData) {
      if (rawData.length < 4) {
        StringBuilder sb = new StringBuilder(110);
        sb.append(
            "The data length of IPv6 routing header is must be more than 3. data: "
           )
          .append(ByteArrays.toHexString(rawData, " "));
        throw new IllegalRawDataException(sb.toString());
      }

      this.nextHeader
        = IpNumber
            .getInstance(ByteArrays.getByte(rawData, NEXT_HEADER_OFFSET));
      this.hdrExtLen
        = ByteArrays.getByte(rawData, HDR_EXT_LEN_OFFSET);

      int headerLength = ((hdrExtLen & 0xFF) + 1) * 8;
      if (rawData.length < headerLength) {
        StringBuilder sb = new StringBuilder(110);
        sb.append("The data is too short to build an IPv6 routing header(")
          .append(headerLength)
          .append(" bytes). data: ")
          .append(ByteArrays.toHexString(rawData, " "));
        throw new IllegalRawDataException(sb.toString());
      }

      this.routingType
        = IpV6RoutingHeaderType.getInstance(
            ByteArrays.getByte(rawData, ROUTING_TYPE_OFFSET)
          );
      this.segmentsLeft
        = ByteArrays.getByte(rawData, SEGMENTS_LEFT_OFFSET);
      this.data
        = ClassifiedDataFactories
            .getFactory(IpV6RoutingData.class, IpV6RoutingHeaderType.class)
              .newData(
                 ByteArrays.getSubArray(
                   rawData, TYPE_SPECIFIC_DATA_OFFSET, headerLength - 4
                 ),
                 routingType
               );
    }

    private IpV6ExtRoutingHeader(Builder builder) {
      if (builder.data.length() < 4) {
        StringBuilder sb = new StringBuilder(100);
        sb.append("data length must be more than 3. data: ")
          .append(builder.data);
        throw new IllegalRawDataException(sb.toString());
      }
      if (((builder.data.length() + 4) % 8) != 0) {
        StringBuilder sb = new StringBuilder(100);
        sb.append("(builder.data.length() + 8 ) % 8 must be 0. data: ")
          .append(builder.data);
        throw new IllegalRawDataException(sb.toString());
      }

      this.nextHeader = builder.nextHeader;
      this.routingType = builder.routingType;
      this.segmentsLeft = builder.segmentsLeft;
      this.data = builder.data;

      if (builder.correctLengthAtBuild) {
        this.hdrExtLen = (byte)((data.length() + 4) / 8 - 1);
      }
      else {
        this.hdrExtLen = builder.hdrExtLen;
      }
    }

    /**
     *
     * @return
     */
    public IpNumber getNextHeader() { return nextHeader; }

    /**
     *
     * @return
     */
    public byte getHdrExtLen() { return hdrExtLen; }

    /**
     *
     * @return
     */
    public int getHdrExtLenAsInt() { return (int)(0xFF & hdrExtLen); }

    /**
     *
     * @return
     */
    public IpV6RoutingHeaderType getRoutingType() { return routingType; }

    /**
     *
     * @return
     */
    public byte getSegmentsLeft() { return segmentsLeft; }

    /**
     *
     * @return
     */
    public int getSegmentsLeftAsInt() { return segmentsLeft & 0xFF; }

    /**
     *
     * @return
     */
    public IpV6RoutingData getData() { return data; }

    @Override
    protected List<byte[]> getRawFields() {
      List<byte[]> rawFields = new ArrayList<byte[]>();
      rawFields.add(ByteArrays.toByteArray(nextHeader.value()));
      rawFields.add(ByteArrays.toByteArray(hdrExtLen));
      rawFields.add(ByteArrays.toByteArray(routingType.value()));
      rawFields.add(ByteArrays.toByteArray(segmentsLeft));
      rawFields.add(data.getRawData());
      return rawFields;
    }

    @Override
    public int measureLength() { return data.length() + 4; }

    @Override
    protected String buildString() {
      StringBuilder sb = new StringBuilder();
      String ls = System.getProperty("line.separator");

      sb.append("[IPv6 Routing Header (")
        .append(length())
        .append(" bytes)]")
        .append(ls);
      sb.append("  Next Header: ")
        .append(nextHeader)
        .append(ls);
      sb.append("  Hdr Ext Len: ")
        .append(getHdrExtLenAsInt())
        .append(" (")
        .append((getHdrExtLenAsInt() + 1) * 8)
        .append(" [bytes])")
        .append(ls);
      sb.append("  Routing Type: ")
        .append(routingType)
        .append(ls);
      sb.append("  Segments Left: ")
        .append(getSegmentsLeftAsInt())
        .append(ls);
      sb.append("  type-specific data: ")
        .append(data)
        .append(ls);

      return sb.toString();
    }

  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.11
   */
  public interface IpV6RoutingData extends Serializable {

    // /* must implement if use DynamicIpV6RoutingDataFactory */
    // public static IpV6RoutingData newInstance(byte[] rawData);

    /**
     *
     * @return
     */
    public int length();

    /**
     *
     * @return
     */
    public byte[] getRawData();

  }

}
