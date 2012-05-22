package org.pcap4j.test;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.AbstractPacket.AbstractBuilder;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.IpV4Helper;

public class DefragmentEchoTest {

  private static final String PCAP_FILE_KEY
    = DefragmentEchoTest.class.getName() + ".pcapFile";
  private static final String PCAP_FILE
    = System.getProperty(PCAP_FILE_KEY, "flagmentedEcho.pcap");

  public static void main(String[] args) throws PcapNativeException {
    PcapHandle handle = Pcaps.openOffline(PCAP_FILE);

    Map<Short, List<IpV4Packet>> ipV4Packets
      = new HashMap<Short, List<IpV4Packet>>();
    Map<Short, Packet> originalPackets = new HashMap<Short, Packet>();

    while (true) {
      try {
        Packet packet = handle.getNextPacketEx();
        Short id
          = packet.get(IpV4Packet.class).getHeader().getIdentification();
        if (ipV4Packets.containsKey(id)) {
          ipV4Packets.get(id).add(packet.get(IpV4Packet.class));
        }
        else {
          List<IpV4Packet> list = new ArrayList<IpV4Packet>();
          list.add(packet.get(IpV4Packet.class));
          ipV4Packets.put(id, list);
          originalPackets.put(id, packet);
        }
      } catch (TimeoutException e) {
        continue;
      } catch (EOFException e) {
        break;
      }
    }

    for (Short id: ipV4Packets.keySet()) {
      List<IpV4Packet> list = ipV4Packets.get(id);
      Collections.shuffle(list);
      final IpV4Packet defragmentedIpV4Packet = IpV4Helper.defragment(list);

      Packet.Builder b = originalPackets.get(id).getBuilder();
      b.getOuterOf(IpV4Packet.Builder.class).payloadBuilder(
        new AbstractBuilder() {
          public Packet build() {
            return defragmentedIpV4Packet;
          }
        }
      );
      System.out.println(b.build());
    }

    handle.close();
  }

}
