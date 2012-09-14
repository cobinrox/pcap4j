package org.pcap4j.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV6ExtFragmentPacket.IpV6ExtFragmentHeader;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.packet.namednumber.UdpPort;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kaito
 *
 */
public class IpV6ExtFragmentPacketTest {

  private static final Logger logger
    = LoggerFactory.getLogger(IpV6ExtFragmentPacketTest.class);

  private final IpNumber nextHeader;
  private final byte reserved;
  private final short fragmentOffset;
  private final byte res;
  private final boolean m;
  private final int identification;
  private final IpV6ExtFragmentPacket packet1;
  private final IpV6ExtFragmentPacket packet2;

  private final Inet6Address srcAddr;
  private final Inet6Address dstAddr;

  public IpV6ExtFragmentPacketTest() throws Exception {
    this.nextHeader = IpNumber.UDP;
    this.reserved = (byte)99;
    this.fragmentOffset = (short)0;
    this.res = (byte)1;
    this.m = true;
    this.identification = 654321;

    try {
      srcAddr
        = (Inet6Address)InetAddress.getByName("aa:bb:cc::3:2:1");
      dstAddr
        = (Inet6Address)InetAddress.getByName("aa:bb:cc::3:2:2");
    } catch (UnknownHostException e) {
      throw new AssertionError();
    }

    UnknownPacket.Builder unknownb = new UnknownPacket.Builder();
    unknownb.rawData(
      new byte[] {
        (byte)0, (byte)1, (byte)2, (byte)3,
        (byte)4, (byte)5, (byte)6, (byte)7
      }
    );

    UdpPacket.Builder udpb = new UdpPacket.Builder();
    udpb.dstPort(UdpPort.getInstance((short)0))
        .srcPort(UdpPort.SNMP_TRAP)
        .dstAddr(dstAddr)
        .srcAddr(srcAddr)
        .payloadBuilder(unknownb)
        .correctChecksumAtBuild(true)
        .correctLengthAtBuild(true);

    byte[] rawPayload = udpb.build().getRawData();

    IpV6ExtFragmentPacket.Builder b
      = new IpV6ExtFragmentPacket.Builder();
    b.nextHeader(nextHeader)
     .reserved(reserved)
     .fragmentOffset(fragmentOffset)
     .res(res)
     .m(m)
     .identification(identification)
     .payloadBuilder(
        new FragmentedPacket.Builder()
          .rawData(ByteArrays.getSubArray(rawPayload, 0, 8)
        )
      );

    this.packet1 = b.build();

    b.fragmentOffset((short)1)
     .m(false)
     .payloadBuilder(
       new FragmentedPacket.Builder()
         .rawData(ByteArrays.getSubArray(rawPayload, 8, 8)
       )
     );

    this.packet2 = b.build();
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    logger.info(
      "########## " + IpV6ExtFragmentPacketTest.class.getSimpleName() + " START ##########"
    );
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    logger.info(
      "=================================================="
    );
  }

  /**
   * {@link org.pcap4j.packet.IpV6ExtFragmentPacket#getBuilder()} のためのテスト・メソッド。
   */
  @Test
  public void testGetBuilder() {
    IpV6ExtFragmentPacket.Builder builder = packet1.getBuilder();
    assertEquals(packet1, builder.build());
  }

  /**
   * {@link org.pcap4j.packet.IpV6ExtFragmentPacket#newPacket(byte[])} のためのテスト・メソッド。
   */
  @Test
  public void testNewPacket() {
    IpV6ExtFragmentPacket p
      = IpV6ExtFragmentPacket.newPacket(packet1.getRawData());
    assertEquals(packet1, p);
  }

  /**
   * {@link org.pcap4j.packet.IpV6ExtFragmentPacket#getHeader()} のためのテスト・メソッド。
   */
  @Test
  public void testGetHeader() {
    IpV6ExtFragmentHeader h = packet1.getHeader();
    assertEquals(nextHeader, h.getNextHeader());
    assertEquals(reserved, h.getReserved());
    assertEquals(fragmentOffset, h.getFragmentOffset());
    assertEquals(res, h.getRes());
    assertEquals(m, h.getM());
    assertEquals(identification, h.getIdentification());
  }

  /**
   * {@link org.pcap4j.packet.AbstractPacket#length()} のためのテスト・メソッド。
   */
  @Test
  public void testLength() {
    assertEquals(packet1.getRawData().length, packet1.length());
  }

  /**
   * {@link org.pcap4j.packet.AbstractPacket#toString()} のためのテスト・メソッド。
   */
  @Test
  public void testToString() throws Exception {
    FileReader fr
      = new FileReader(
          "src/test/resources/" + getClass().getSimpleName() + ".log"
        );
    BufferedReader fbr = new BufferedReader(fr);
    StringReader sr = new StringReader(packet1.toString());
    BufferedReader sbr = new BufferedReader(sr);

    String line;
    while ((line = sbr.readLine()) != null) {
      assertEquals(fbr.readLine(), line);
    }

    sr.close();
    sbr.close();

    sr = new StringReader(packet2.toString());
    sbr = new BufferedReader(sr);

    while ((line = sbr.readLine()) != null) {
      assertEquals(fbr.readLine(), line);
    }

    assertNull(sbr.readLine());

    fbr.close();
    fr.close();
    sr.close();
    sbr.close();
  }

  @Test
  public void testDump() throws Exception {
    String dumpFile = "test/" + this.getClass().getSimpleName() + ".pcap";

    IpV6Packet.Builder ipV6b = new IpV6Packet.Builder();
    ipV6b.version(IpVersion.IPV6)
         .trafficClass(IpV6SimpleTrafficClass.newInstance((byte)0x12))
         .flowLabel(IpV6SimpleFlowLabel.newInstance(0x12345))
         .nextHeader(IpNumber.IPV6_FRAG)
         .hopLimit((byte)100)
         .srcAddr(srcAddr)
         .dstAddr(dstAddr)
         .payloadBuilder(packet1.getBuilder())
         .correctLengthAtBuild(true);

    EthernetPacket.Builder eb = new EthernetPacket.Builder();
    eb.dstAddr(MacAddress.getByName("fe:00:00:00:00:02"))
      .srcAddr(MacAddress.getByName("fe:00:00:00:00:01"))
      .type(EtherType.IPV6)
      .payloadBuilder(ipV6b)
      .paddingAtBuild(true);

    EthernetPacket ep1 = eb.build();

    ipV6b.payloadBuilder(packet2.getBuilder());
    EthernetPacket ep2 = eb.build();


    PcapHandle handle = Pcaps.openDead(DataLinkType.EN10MB, 65536);
    PcapDumper dumper = handle.dumpOpen(dumpFile);
    dumper.dump(ep1, 0, 0);
    dumper.dump(ep2, 0, 0);
    dumper.close();
    handle.close();

    PcapHandle reader = Pcaps.openOffline(dumpFile);
    assertEquals(ep1, reader.getNextPacket());
    assertEquals(ep2, reader.getNextPacket());
    reader.close();

    FileInputStream in1
      = new FileInputStream(
          "src/test/resources/" + getClass().getSimpleName() + ".pcap"
        );
    FileInputStream in2 = new FileInputStream(dumpFile);

    byte[] buffer1 = new byte[100];
    byte[] buffer2 = new byte[100];
    int size;
    while ((size = in1.read(buffer1)) != -1) {
      assertEquals(size, in2.read(buffer2));
      assertArrayEquals(buffer1, buffer2);
    }

    in1.close();
    in2.close();
  }

  @Test
  public void testWriteRead() throws Exception {
    String objFile = "test/" + this.getClass().getSimpleName() + ".obj";

    ObjectOutputStream oos
      = new ObjectOutputStream(
          new FileOutputStream(new File(objFile))
        );
    oos.writeObject(packet1);
    oos.close();

    ObjectInputStream ois
      = new ObjectInputStream(new FileInputStream(new File(objFile)));
    assertEquals(packet1, ois.readObject());
    ois.close();

    FileInputStream in1
      = new FileInputStream(
          "src/test/resources/" + getClass().getSimpleName() + ".obj"
        );
    FileInputStream in2 = new FileInputStream(objFile);

    byte[] buffer1 = new byte[100];
    byte[] buffer2 = new byte[100];
    int size;
    while ((size = in1.read(buffer1)) != -1) {
      assertEquals(size, in2.read(buffer2));
      assertArrayEquals(buffer1, buffer2);
    }

    in1.close();
    in2.close();
  }

}
