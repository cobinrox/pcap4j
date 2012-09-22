/*_##########################################################################
  _##
  _##  Copyright (C) 2012  Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.packet;

import java.util.ArrayList;
import java.util.List;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.util.ByteArrays;
import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;
import static org.pcap4j.util.ByteArrays.SHORT_SIZE_IN_BYTES;
import static org.pcap4j.util.ByteArrays.INT_SIZE_IN_BYTES;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.10
 */
public final class IpV6ExtFragmentPacket extends AbstractPacket {

  /**
   *
   */
  private static final long serialVersionUID = 8789423734186381406L;

  private final IpV6ExtFragmentHeader header;
  private final Packet payload;

  /**
   *
   * @param rawData
   * @return
   */
  public static IpV6ExtFragmentPacket newPacket(byte[] rawData) {
    return new IpV6ExtFragmentPacket(rawData);
  }

  private IpV6ExtFragmentPacket(byte[] rawData) {
    this.header = new IpV6ExtFragmentHeader(rawData);

    byte[] rawPayload
      = ByteArrays.getSubArray(
          rawData,
          header.length(),
          rawData.length - header.length()
        );

    if (header.m || header.fragmentOffset != 0) {
      this.payload = FragmentedPacket.newPacket(rawPayload);
    }
    else {
      this.payload
        = PacketFactories.getFactory(IpNumber.class)
            .newPacket(rawPayload, header.getNextHeader());
    }
  }

  private IpV6ExtFragmentPacket(Builder builder) {
    if (
         builder == null
      || builder.nextHeader == null
      || builder.payloadBuilder == null
    ) {
      StringBuilder sb = new StringBuilder();
      sb.append("builder: ").append(builder)
        .append(" builder.nextHeader: ").append(builder.nextHeader)
        .append(" builder.payloadBuilder: ").append(builder.payloadBuilder);
      throw new NullPointerException(sb.toString());
    }

    this.payload = builder.payloadBuilder.build();
    this.header = new IpV6ExtFragmentHeader(builder);
  }

  @Override
  public IpV6ExtFragmentHeader getHeader() {
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
  public static final class Builder extends AbstractBuilder {

    private IpNumber nextHeader;
    private byte reserved;
    private short fragmentOffset;
    private byte res;
    private boolean m;
    private int identification;
    private Packet.Builder payloadBuilder;

    /**
     *
     */
    public Builder() {}

    /**
     *
     * @param packet
     */
    public Builder(IpV6ExtFragmentPacket packet) {
      this.nextHeader = packet.header.nextHeader;
      this.reserved = packet.header.reserved;
      this.fragmentOffset = packet.header.fragmentOffset;
      this.res = packet.header.res;
      this.m = packet.header.m;
      this.identification = packet.header.identification;
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
     * @param reserved
     * @return
     */
    public Builder reserved(byte reserved) {
      this.reserved = reserved;
      return this;
    }

    /**
     *
     * @param fragmentOffset
     * @return
     */
    public Builder fragmentOffset(short fragmentOffset) {
      this.fragmentOffset = fragmentOffset;
      return this;
    }

    /**
     *
     * @param res
     * @return
     */
    public Builder res(byte res) {
      this.res = res;
      return this;
    }

    /**
     *
     * @param m
     * @return
     */
    public Builder m(boolean m) {
      this.m = m;
      return this;
    }

    /**
     *
     * @param identification
     * @return
     */
    public Builder identification(int identification) {
      this.identification = identification;
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

    @Override
    public IpV6ExtFragmentPacket build() {
      return new IpV6ExtFragmentPacket(this);
    }

  }

  /**
   * @author Kaito Yamada
   * @since pcap4j 0.9.10
   */
  public static final class IpV6ExtFragmentHeader extends AbstractHeader {


    /*
     *  0                              16                            31
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |  Next Header  |   Reserved    |      Fragment Offset    |Res|M|
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         Identification                        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */

    /**
     *
     */
    private static final long serialVersionUID = 3488980383672562461L;

    private static final int NEXT_HEADER_OFFSET
      = 0;
    private static final int NEXT_HEADER_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int RESERVED_OFFSET
      = NEXT_HEADER_OFFSET + NEXT_HEADER_SIZE;
    private static final int RESERVED_SIZE
      = BYTE_SIZE_IN_BYTES;
    private static final int FRAGMENT_OFFSET_AND_RES_AND_M_OFFSET
      = RESERVED_OFFSET + RESERVED_SIZE;
    private static final int FFRAGMENT_OFFSET_AND_RES_AND_M_SIZE
      = SHORT_SIZE_IN_BYTES;
    private static final int IDENTIFICATION_OFFSET
      = FRAGMENT_OFFSET_AND_RES_AND_M_OFFSET
          + FFRAGMENT_OFFSET_AND_RES_AND_M_SIZE;
    private static final int IDENTIFICATION_SIZE
      = INT_SIZE_IN_BYTES;
    private static final int IPV6_EXT_FRAGMENT_HEADER_SIZE
      = IDENTIFICATION_OFFSET + IDENTIFICATION_SIZE;

    private final IpNumber nextHeader;
    private final byte reserved;
    private final short fragmentOffset;
    private final byte res;
    private final boolean m;
    private final int identification;

    private IpV6ExtFragmentHeader(byte[] rawData) {
      if (rawData.length < IPV6_EXT_FRAGMENT_HEADER_SIZE) {
        StringBuilder sb = new StringBuilder(110);
        sb.append("The data is too short to build an IPv6 fragment header(")
          .append(IPV6_EXT_FRAGMENT_HEADER_SIZE)
          .append(" bytes). data: ")
          .append(ByteArrays.toHexString(rawData, " "));
        throw new IllegalRawDataException(sb.toString());
      }

      this.nextHeader
        = IpNumber
            .getInstance(ByteArrays.getByte(rawData, NEXT_HEADER_OFFSET));
      this.reserved
        = ByteArrays.getByte(rawData, RESERVED_OFFSET);
      short fragmentOffsetAndResAndM
        = ByteArrays.getShort(rawData, FRAGMENT_OFFSET_AND_RES_AND_M_OFFSET);
      this.fragmentOffset
        = (short)((fragmentOffsetAndResAndM & 0xFFF8) >> 3);
      this.res
        = (byte)((fragmentOffsetAndResAndM & 0x0006) >> 1);
      this.m
        = (fragmentOffsetAndResAndM & 0x0001) == 1;
      this.identification
        = ByteArrays.getInt(rawData, IDENTIFICATION_OFFSET);
    }

    private IpV6ExtFragmentHeader(Builder builder) {
      if ((builder.fragmentOffset & 0xE000) != 0) {
        throw new IllegalArgumentException(
                    "Invalid fragmentOffset: " + builder.fragmentOffset
                  );
      }
      if ((builder.res & 0xFFFC) != 0) {
        throw new IllegalArgumentException(
                    "Invalid res: " + builder.res
                  );
      }

      this.nextHeader = builder.nextHeader;
      this.reserved = builder.reserved;
      this.fragmentOffset = builder.fragmentOffset;
      this.res = builder.res;
      this.m = builder.m;
      this.identification = builder.identification;
    }

    /**
     *
     * @return
     */
    public IpNumber getNextHeader() {
      return nextHeader;
    }

    /**
     *
     * @return
     */
    public byte getReserved() {
      return reserved;
    }

    /**
     *
     * @return
     */
    public short getFragmentOffset() {
      return fragmentOffset;
    }

    /**
     *
     * @return
     */
    public byte getRes() {
      return res;
    }

    /**
     *
     * @return
     */
    public boolean getM() {
      return m;
    }

    /**
     *
     * @return
     */
    public int getIdentification() {
      return identification;
    }

    @Override
    protected List<byte[]> getRawFields() {
      List<byte[]> rawFields = new ArrayList<byte[]>();
      rawFields.add(ByteArrays.toByteArray(nextHeader.value()));
      rawFields.add(ByteArrays.toByteArray(reserved));
      rawFields.add(
        ByteArrays.toByteArray(
          (short)((fragmentOffset << 3) | (res << 1) | (m ? 1 : 0))
        )
      );
      rawFields.add(ByteArrays.toByteArray(identification));
      return rawFields;
    }

    @Override
    public int length() {
      return IPV6_EXT_FRAGMENT_HEADER_SIZE;
    }

    @Override
    protected String buildString() {
      StringBuilder sb = new StringBuilder();
      String ls = System.getProperty("line.separator");

      sb.append("[IPv6 Fragment Header (")
        .append(length())
        .append(" bytes)]")
        .append(ls);
      sb.append("  Next Header: ")
        .append(nextHeader)
        .append(ls);
      sb.append("  Reserved: ")
        .append(ByteArrays.toHexString(reserved, " "))
        .append(ls);
      sb.append("  Fragment Offset: ")
        .append(fragmentOffset)
        .append(ls);
      sb.append("  Res: ")
        .append(ByteArrays.toHexString(res, " "))
        .append(ls);
      sb.append("  M: ")
        .append(m)
        .append(ls);
      sb.append("  Identification: ")
        .append(identification)
        .append(ls);

      return sb.toString();
    }

  }

}
