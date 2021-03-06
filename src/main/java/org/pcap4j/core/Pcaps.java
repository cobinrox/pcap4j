/*_##########################################################################
  _##
  _##  Copyright (C) 2011-2012  Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pcap4j.core.NativeMappings.PcapErrbuf;
import org.pcap4j.core.NativeMappings.pcap_if;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.1
 */
public final class Pcaps {

//  #define PCAP_ERROR      -1  /* generic error code */
//  #define PCAP_ERROR_BREAK    -2  /* loop terminated by pcap_breakloop */
//  #define PCAP_ERROR_NOT_ACTIVATED  -3  /* the capture needs to be activated */
//  #define PCAP_ERROR_ACTIVATED    -4  /* the operation can't be performed on already activated captures */
//  #define PCAP_ERROR_NO_SUCH_DEVICE -5  /* no such device exists */
//  #define PCAP_ERROR_RFMON_NOTSUP   -6  /* this device doesn't support rfmon (monitor) mode */
//  #define PCAP_ERROR_NOT_RFMON    -7  /* operation supported only in monitor mode */
//  #define PCAP_ERROR_PERM_DENIED    -8  /* no permission to open the device */
//  #define PCAP_ERROR_IFACE_NOT_UP   -9  /* interface isn't up */
//  #define PCAP_WARNING      1 /* generic warning code */
//  #define PCAP_WARNING_PROMISC_NOTSUP 2 /* this device doesn't support promiscuous mode */

  private static final Logger logger = LoggerFactory.getLogger(Pcaps.class);

  private Pcaps() { throw new AssertionError(); }

  /**
   *
   * @return
   * @throws PcapNativeException
   */
  public static
  List<PcapNetworkInterface> findAllDevs() throws PcapNativeException {
    PointerByReference alldevsPP = new PointerByReference();
    PcapErrbuf errbuf = new PcapErrbuf();

    //int rc = PcapLibrary.INSTANCE.pcap_findalldevs(alldevsPP, errbuf);
    int rc = NativeMappings.pcap_findalldevs(alldevsPP, errbuf);
    if (rc != 0) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("Return code: ")
        .append(rc)
        .append(", Message: ")
        .append(errbuf);
      throw new PcapNativeException(sb.toString());
    }
    if (errbuf.length() != 0) {
      logger.warn("{}", errbuf);
    }

    Pointer alldevsp = alldevsPP.getValue();
    if (alldevsp == null) {
      logger.info("No NIF was found.");
      return Collections.<PcapNetworkInterface>emptyList();
    }

    pcap_if pcapIf = new pcap_if(alldevsp);

    List<PcapNetworkInterface> ifList = new ArrayList<PcapNetworkInterface>();
    for (pcap_if pif = pcapIf; pif != null; pif = pif.next) {
      ifList.add(PcapNetworkInterface.newInstance(pif, true));
    }

    // PcapLibrary.INSTANCE.pcap_freealldevs(pcapIf.getPointer());
    NativeMappings.pcap_freealldevs(pcapIf.getPointer());

    logger.info("{} NIF(s) found.", ifList.size());
    return ifList;
  }

  /**
   *
   * @param addr
   * @return
   * @throws PcapNativeException
   */
  public static PcapNetworkInterface getNifBy(
    InetAddress addr
  ) throws PcapNativeException {
    List<PcapNetworkInterface> allDevs = findAllDevs();

    for (PcapNetworkInterface pif: allDevs) {
      for (PcapAddress paddr: pif.getAddresses()) {
        if (paddr.getAddress().equals(addr)) {
          return pif;
        }
      }
    }

    return null;
  }

  /**
   *
   * @param name
   * @return
   * @throws PcapNativeException
   */
  public static PcapNetworkInterface getNifBy(
    String name
  ) throws PcapNativeException {
    List<PcapNetworkInterface> allDevs = findAllDevs();

    for (PcapNetworkInterface pif: allDevs) {
      if (pif.getName().equals(name)) {
        return pif;
      }
    }

    return null;
  }

  /**
   *
   * @return
   * @throws PcapNativeException
   */
  public static String lookupDev() throws PcapNativeException {
    PcapErrbuf errbuf = new PcapErrbuf();
    // Pointer result = PcapLibrary.INSTANCE.pcap_lookupdev(errbuf);
    Pointer result = NativeMappings.pcap_lookupdev(errbuf);

    if (result == null || errbuf.length() != 0) {
      throw new PcapNativeException(errbuf.toString());
    }

    return result.getString(0, true);
  }

  /**
   *
   * @param filePath "-" means stdin
   * @return
   * @throws PcapNativeException
   */
  public static PcapHandle openOffline(
    String filePath
  ) throws PcapNativeException {
    PcapErrbuf errbuf = new PcapErrbuf();
//    Pointer handle
//      = PcapLibrary.INSTANCE.pcap_open_offline(filePath, errbuf);
    Pointer handle
      = NativeMappings.pcap_open_offline(filePath, errbuf);

    if (handle == null || errbuf.length() != 0) {
      throw new PcapNativeException(errbuf.toString());
    }

    return new PcapHandle(handle, true);
  }

  /**
   *
   * @param dlt
   * @param maxCaptureLength
   * @return
   * @throws PcapNativeException
   */
  public static PcapHandle openDead(
    DataLinkType dlt, int maxCaptureLength
  ) throws PcapNativeException {
//    Pointer handle
//      = PcapLibrary.INSTANCE.pcap_open_dead(dlt.value(), maxCaptureLength);
    Pointer handle
      = NativeMappings.pcap_open_dead(dlt.value(), maxCaptureLength);
    if (handle == null) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("Failed to open a PcapHandle. dlt: ").append(dlt)
        .append(" maxCaptureLength: ").append(maxCaptureLength);
      throw new PcapNativeException(sb.toString());
    }

    return new PcapHandle(handle, false);
  }

  /**
   *
   * @param inetAddr Inet4Address or Inet6Address
   * @return
   */
  public static String toBpfString (InetAddress inetAddr){
    String strAddr = inetAddr.toString();
    return strAddr.substring(strAddr.lastIndexOf("/") + 1);
  }

  /**
   *
   * @param macAddr
   * @return
   */
  public static String toBpfString(MacAddress macAddr) {
    StringBuffer buf = new StringBuffer();
    byte[] address = macAddr.getAddress();

    for (int i = 0; i < address.length; i++) {
      buf.append(String.format("%02x", address[i]));
      buf.append(":");
    }
    buf.deleteCharAt(buf.length() - 1);

    return buf.toString();
  }

}

