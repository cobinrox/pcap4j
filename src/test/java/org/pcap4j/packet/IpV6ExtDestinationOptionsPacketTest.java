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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV6ExtDestinationOptionsPacket.IpV6ExtDestinationOptionsHeader;
import org.pcap4j.packet.IpV6ExtOptionsPacket.IpV6Option;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.packet.namednumber.UdpPort;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kaito
 *
 */
public class IpV6ExtDestinationOptionsPacketTest {

  private static final Logger logger
    = LoggerFactory.getLogger(IpV6ExtDestinationOptionsPacketTest.class);

  private final IpNumber nextHeader;
  private final byte hdrExtLen;
  private final List<IpV6Option> options;
  private final IpV6ExtDestinationOptionsPacket packet;

  private final Inet6Address srcAddr;
  private final Inet6Address dstAddr;

  public IpV6ExtDestinationOptionsPacketTest() throws Exception {
    this.nextHeader = IpNumber.UDP;
    this.hdrExtLen = (byte)0;
    this.options = new ArrayList<IpV6Option>();
    options.add(IpV6Pad1Option.getInstance());
    options.add(
      new IpV6PadNOption.Builder()
        .data(new byte[] { 0, 0, 0 })
        .dataLen((byte)3)
        .build()
    );

    try {
      srcAddr
        = (Inet6Address)InetAddress.getByName("aa:bb:cc::3:2:1");
      dstAddr
        = (Inet6Address)InetAddress.getByName("aa:bb:cc::3:2:2");
    } catch (UnknownHostException e) {
      throw new AssertionError();
    }

    UnknownPacket.Builder anonb = new UnknownPacket.Builder();
    anonb.rawData(new byte[] { (byte)0, (byte)1, (byte)2, (byte)3 });

    UdpPacket.Builder udpb = new UdpPacket.Builder();
    udpb.dstPort(UdpPort.getInstance((short)0))
        .srcPort(UdpPort.SNMP_TRAP)
        .dstAddr(dstAddr)
        .srcAddr(srcAddr)
        .payloadBuilder(anonb)
        .correctChecksumAtBuild(true)
        .correctLengthAtBuild(true);

    IpV6ExtDestinationOptionsPacket.Builder b
      = new IpV6ExtDestinationOptionsPacket.Builder();
    b.nextHeader(nextHeader)
     .hdrExtLen(hdrExtLen)
     .options(options)
     .correctLengthAtBuild(false)
     .payloadBuilder(udpb);

    this.packet = b.build();
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    logger.info(
      "########## " + IpV6ExtDestinationOptionsPacketTest.class.getSimpleName() + " START ##########"
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
   * {@link org.pcap4j.packet.IpV6ExtDestinationOptionsPacket#getBuilder()} のためのテスト・メソッド。
   */
  @Test
  public void testGetBuilder() {
    IpV6ExtDestinationOptionsPacket.Builder builder = packet.getBuilder();
    assertEquals(packet, builder.build());
  }

  /**
   * {@link org.pcap4j.packet.IpV6ExtDestinationOptionsPacket#newPacket(byte[])} のためのテスト・メソッド。
   */
  @Test
  public void testNewPacket() {
    IpV6ExtDestinationOptionsPacket p
      = IpV6ExtDestinationOptionsPacket.newPacket(packet.getRawData());
    assertEquals(packet, p);
  }

  /**
   * {@link org.pcap4j.packet.IpV6ExtDestinationOptionsPacket#getHeader()} のためのテスト・メソッド。
   */
  @Test
  public void testGetHeader() {
    IpV6ExtDestinationOptionsHeader h = packet.getHeader();
    assertEquals(nextHeader, h.getNextHeader());
    assertEquals(hdrExtLen, h.getHdrExtLen());
    assertEquals(options.size(), h.getOptions().size());
    Iterator<IpV6Option> iter = h.getOptions().iterator();
    for (IpV6Option opt: options) {
      assertEquals(opt, iter.next());
    }

    IpV6ExtDestinationOptionsPacket.Builder b = packet.getBuilder();
    IpV6ExtDestinationOptionsPacket p;

    b.hdrExtLen((byte)0);
    p = b.build();
    assertEquals((byte)0, (byte)p.getHeader().getHdrExtLenAsInt());

    b.hdrExtLen((byte)-1);
    p = b.build();
    assertEquals((byte)-1, (byte)p.getHeader().getHdrExtLenAsInt());

    b.hdrExtLen((byte)127);
    p = b.build();
    assertEquals((byte)127, (byte)p.getHeader().getHdrExtLenAsInt());

    b.hdrExtLen((byte)-128);
    p = b.build();
    assertEquals((byte)-128, (byte)p.getHeader().getHdrExtLenAsInt());
  }

  /**
   * {@link org.pcap4j.packet.AbstractPacket#length()} のためのテスト・メソッド。
   */
  @Test
  public void testLength() {
    assertEquals(packet.getRawData().length, packet.length());
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
    StringReader sr = new StringReader(packet.toString());
    BufferedReader sbr = new BufferedReader(sr);

    String line;
    while ((line = fbr.readLine()) != null) {
      assertEquals(line, sbr.readLine());
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

    IpV6Packet.Builder IpV6b = new IpV6Packet.Builder();
    IpV6b.version(IpVersion.IPV6)
         .trafficClass(IpV6SimpleTrafficClass.newInstance((byte)0x12))
         .flowLabel(IpV6SimpleFlowLabel.newInstance(0x12345))
         .nextHeader(IpNumber.IPV6_DST_OPTS)
         .hopLimit((byte)100)
         .srcAddr(srcAddr)
         .dstAddr(dstAddr)
         .payloadBuilder(packet.getBuilder().correctLengthAtBuild(true))
         .correctLengthAtBuild(true);

    EthernetPacket.Builder eb = new EthernetPacket.Builder();
    eb.dstAddr(MacAddress.getByName("fe:00:00:00:00:02"))
      .srcAddr(MacAddress.getByName("fe:00:00:00:00:01"))
      .type(EtherType.IPV6)
      .payloadBuilder(IpV6b)
      .paddingAtBuild(true);

    eb.get(UdpPacket.Builder.class)
      .dstAddr(dstAddr)
      .srcAddr(srcAddr);

    EthernetPacket ep = eb.build();

    PcapHandle handle = Pcaps.openDead(DataLinkType.EN10MB, 65536);
    PcapDumper dumper = handle.dumpOpen(dumpFile);
    dumper.dump(ep, 0, 0);
    dumper.close();
    handle.close();

    PcapHandle reader = Pcaps.openOffline(dumpFile);
    assertEquals(ep, reader.getNextPacket());
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
    oos.writeObject(packet);
    oos.close();

    ObjectInputStream ois
      = new ObjectInputStream(new FileInputStream(new File(objFile)));
    assertEquals(packet, ois.readObject());
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
