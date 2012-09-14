/*_##########################################################################
  _##
  _##  Copyright (C) 2011  Kaito Yamada
  _##
  _##########################################################################
*/

package org.pcap4j.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.regex.Pattern;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import org.pcap4j.util.MacAddress;

/**
 * @author Kaito Yamada
 * @since pcap4j 0.9.1
 */
public final class ByteArrays {

  /**
   *
   */
  public static final int BYTE_SIZE_IN_BYTES = 1;

  /**
   *
   */
  public static final int SHORT_SIZE_IN_BYTES = 2;

  /**
   *
   */
  public static final int INT_SIZE_IN_BYTES = 4;

  /**
   *
   */
  public static final int LONG_SIZE_IN_BYTES = 8;

  /**
   *
   */
  public static final int INET4_ADDRESS_SIZE_IN_BYTES = 4;

  /**
   *
   */
  public static final int INET6_ADDRESS_SIZE_IN_BYTES = 16;

  /**
   *
   */
  public static final int BYTE_SIZE_IN_BITS = 8;

  private static final Pattern NO_SEPARATOR_HEX_STRING_PATTERN
    = Pattern.compile("\\A([0-9a-fA-F][0-9a-fA-F])+\\z");

  private ByteArrays() { throw new AssertionError(); }

  /**
   *
   * @param array
   * @return
   */
  public static byte[] reverse(byte[] array) {
    byte[] rarray = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
      rarray[i] = array[array.length - i - 1];
    }
    return rarray;
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static byte getByte(byte[] array, int offset) {
    if (array == null) {
      throw new NullPointerException("array may not be null");
    }
    if (offset < 0 || offset + BYTE_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }
    return array[offset];
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(byte value) {
    return new byte[] { value };
  }

  /**
   *
   * @param value
   * @param separator
   * @return
   */
  public static String toHexString(byte value, String separator) {
    return toHexString(toByteArray(value), separator);
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static short getShort(byte[] array, int offset) {
    return getShort(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static short getShort(byte[] array, int offset, ByteOrder bo) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + SHORT_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    if (bo.equals(LITTLE_ENDIAN)) {
      return (short)(
                  ((       array[offset + 1]) << (BYTE_SIZE_IN_BITS * 1))
                | ((0xFF & array[offset    ])                           )
              );
    }
    else {
      return (short)(
                  ((       array[offset    ]) << (BYTE_SIZE_IN_BITS * 1))
                | ((0xFF & array[offset + 1])                           )
              );
    }
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(short value) {
    return toByteArray(value, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param bo
   * @return
   */
  public static byte[] toByteArray(short value, ByteOrder bo) {
    if (bo.equals(LITTLE_ENDIAN)) {
      return new byte[] {
               (byte)(value                         ),
               (byte)(value >> BYTE_SIZE_IN_BITS * 1)
             };
    }
    else {
      return new byte[] {
               (byte)(value >> BYTE_SIZE_IN_BITS * 1),
               (byte)(value                         )
             };
    }
  }

  /**
   *
   * @param value
   * @param separator
   * @return
   */
  public static String toHexString(short value, String separator) {
    return toHexString(value, separator, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param separator
   * @param bo
   * @return
   */
  public static String toHexString(short value, String separator, ByteOrder bo) {
    return toHexString(toByteArray(value, bo), separator);
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static int getInt(byte[] array, int offset) {
    return getInt(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static int getInt(byte[] array, int offset, ByteOrder bo) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + INT_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    if (bo.equals(LITTLE_ENDIAN)) {
      return (int)(
                 ((       array[offset + 3]) << (BYTE_SIZE_IN_BITS * 3))
               | ((0xFF & array[offset + 2]) << (BYTE_SIZE_IN_BITS * 2))
               | ((0xFF & array[offset + 1]) << (BYTE_SIZE_IN_BITS * 1))
               | ((0xFF & array[offset    ])                           )
             );
    }
    else {
      return (int)(
                 ((       array[offset    ]) << (BYTE_SIZE_IN_BITS * 3))
               | ((0xFF & array[offset + 1]) << (BYTE_SIZE_IN_BITS * 2))
               | ((0xFF & array[offset + 2]) << (BYTE_SIZE_IN_BITS * 1))
               | ((0xFF & array[offset + 3])                           )
             );
    }
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(int value) {
    return toByteArray(value, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param bo
   * @return
   */
  public static byte[] toByteArray(int value, ByteOrder bo) {
    if (bo.equals(LITTLE_ENDIAN)) {
      return new byte[] {
               (byte)(value                         ),
               (byte)(value >> BYTE_SIZE_IN_BITS * 1),
               (byte)(value >> BYTE_SIZE_IN_BITS * 2),
               (byte)(value >> BYTE_SIZE_IN_BITS * 3),
             };
    }
    else {
      return new byte[] {
               (byte)(value >> BYTE_SIZE_IN_BITS * 3),
               (byte)(value >> BYTE_SIZE_IN_BITS * 2),
               (byte)(value >> BYTE_SIZE_IN_BITS * 1),
               (byte)(value                         )
             };
    }
  }

  /**
   *
   * @param value
   * @param separator
   * @return
   */
  public static String toHexString(int value, String separator) {
    return toHexString(value, separator, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param separator
   * @param bo
   * @return
   */
  public static String toHexString(int value, String separator, ByteOrder bo) {
    return toHexString(toByteArray(value, bo), separator);
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static long getLong(byte[] array, int offset) {
    return getLong(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static long getLong(byte[] array, int offset, ByteOrder bo) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + LONG_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    if (bo.equals(LITTLE_ENDIAN)) {
      return (long)(
                 ((        (long)array[offset + 7]) << (BYTE_SIZE_IN_BITS * 7))
               | ((0xFFL & (long)array[offset + 6]) << (BYTE_SIZE_IN_BITS * 6))
               | ((0xFFL & (long)array[offset + 5]) << (BYTE_SIZE_IN_BITS * 5))
               | ((0xFFL & (long)array[offset + 4]) << (BYTE_SIZE_IN_BITS * 4))
               | ((0xFFL & (long)array[offset + 3]) << (BYTE_SIZE_IN_BITS * 3))
               | ((0xFFL & (long)array[offset + 2]) << (BYTE_SIZE_IN_BITS * 2))
               | ((0xFFL & (long)array[offset + 1]) << (BYTE_SIZE_IN_BITS * 1))
               | ((0xFFL & (long)array[offset    ])                           )
             );
    }
    else {
      return (long)(
                 ((        (long)array[offset    ]) << (BYTE_SIZE_IN_BITS * 7))
               | ((0xFFL & (long)array[offset + 1]) << (BYTE_SIZE_IN_BITS * 6))
               | ((0xFFL & (long)array[offset + 2]) << (BYTE_SIZE_IN_BITS * 5))
               | ((0xFFL & (long)array[offset + 3]) << (BYTE_SIZE_IN_BITS * 4))
               | ((0xFFL & (long)array[offset + 4]) << (BYTE_SIZE_IN_BITS * 3))
               | ((0xFFL & (long)array[offset + 5]) << (BYTE_SIZE_IN_BITS * 2))
               | ((0xFFL & (long)array[offset + 6]) << (BYTE_SIZE_IN_BITS * 1))
               | ((0xFFL & (long)array[offset + 7])                           )
             );
    }
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(long value) {
    return toByteArray(value, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param bo
   * @return
   */
  public static byte[] toByteArray(long value, ByteOrder bo) {
    if (bo.equals(LITTLE_ENDIAN)) {
      return new byte[] {
               (byte)(value                         ),
               (byte)(value >> BYTE_SIZE_IN_BITS * 1),
               (byte)(value >> BYTE_SIZE_IN_BITS * 2),
               (byte)(value >> BYTE_SIZE_IN_BITS * 3),
               (byte)(value >> BYTE_SIZE_IN_BITS * 4),
               (byte)(value >> BYTE_SIZE_IN_BITS * 5),
               (byte)(value >> BYTE_SIZE_IN_BITS * 6),
               (byte)(value >> BYTE_SIZE_IN_BITS * 7)
             };
    }
    else {
      return new byte[] {
               (byte)(value >> BYTE_SIZE_IN_BITS * 7),
               (byte)(value >> BYTE_SIZE_IN_BITS * 6),
               (byte)(value >> BYTE_SIZE_IN_BITS * 5),
               (byte)(value >> BYTE_SIZE_IN_BITS * 4),
               (byte)(value >> BYTE_SIZE_IN_BITS * 3),
               (byte)(value >> BYTE_SIZE_IN_BITS * 2),
               (byte)(value >> BYTE_SIZE_IN_BITS * 1),
               (byte)(value                         )
             };
    }
  }

  /**
   *
   * @param value
   * @param separator
   * @return
   */
  public static String toHexString(long value, String separator) {
    return toHexString(value, separator, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param separator
   * @param bo
   * @return
   */
  public static String toHexString(long value, String separator, ByteOrder bo) {
    return toHexString(toByteArray(value, bo), separator);
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static MacAddress getMacAddress(byte[] array, int offset) {
    return getMacAddress(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static MacAddress getMacAddress(
    byte[] array, int offset, ByteOrder bo
  ) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + MacAddress.SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    if (bo.equals(LITTLE_ENDIAN)) {
      return MacAddress.getByAddress(
               reverse(getSubArray(array, offset, MacAddress.SIZE_IN_BYTES))
             );
    }
    else {
      return MacAddress.getByAddress(
               getSubArray(array, offset, MacAddress.SIZE_IN_BYTES)
             );
    }
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(MacAddress value) {
    return toByteArray(value, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param bo
   * @return
   */
  public static byte[] toByteArray(MacAddress value, ByteOrder bo) {
    if (bo.equals(LITTLE_ENDIAN)) {
      return reverse(value.getAddress());
    }
    else {
      return value.getAddress();
    }
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static Inet4Address getInet4Address(byte[] array, int offset) {
    return getInet4Address(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static Inet4Address getInet4Address(
    byte[] array, int offset, ByteOrder bo
  ) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + INET4_ADDRESS_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    try {
      if (bo.equals(LITTLE_ENDIAN)) {
        return (Inet4Address)Inet4Address.getByAddress(
                 reverse(
                   getSubArray(
                     array,
                     offset,
                     INET4_ADDRESS_SIZE_IN_BYTES
                   )
                 )
               );
      }
      else {
        return (Inet4Address)Inet4Address.getByAddress(
                 getSubArray(
                   array,
                   offset,
                   INET4_ADDRESS_SIZE_IN_BYTES
                 )
               );
      }
    } catch (UnknownHostException e) {
      throw new AssertionError(e);
    }
  }

  /**
   *
   * @param array
   * @param offset
   * @return
   */
  public static Inet6Address getInet6Address(byte[] array, int offset) {
    return getInet6Address(array, offset, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param array
   * @param offset
   * @param bo
   * @return
   */
  public static Inet6Address getInet6Address(
    byte[] array, int offset, ByteOrder bo
  ) {
    if (array == null || bo == null) {
      StringBuilder sb = new StringBuilder(40);
      sb.append("array: ").append(array)
        .append(" bo: ").append(bo);
      throw new NullPointerException(sb.toString());
    }
    if (offset < 0 || offset + INET6_ADDRESS_SIZE_IN_BYTES > array.length) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" bo: ").append(bo);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    try {
      if (bo.equals(LITTLE_ENDIAN)) {
        return (Inet6Address)Inet6Address.getByAddress(
                 reverse(
                   getSubArray(
                     array,
                     offset,
                     INET6_ADDRESS_SIZE_IN_BYTES
                   )
                 )
               );
      }
      else {
        return (Inet6Address)Inet6Address.getByAddress(
                 getSubArray(
                   array,
                   offset,
                   INET6_ADDRESS_SIZE_IN_BYTES
                 )
               );
      }
    } catch (UnknownHostException e) {
      throw new AssertionError(e);
    }
  }

  /**
   *
   * @param value
   * @return
   */
  public static byte[] toByteArray(InetAddress value) {
    return toByteArray(value, ByteOrder.BIG_ENDIAN);
  }

  /**
   *
   * @param value
   * @param bo
   * @return
   */
  public static byte[] toByteArray(InetAddress value, ByteOrder bo) {
    if (bo.equals(LITTLE_ENDIAN)) {
      return reverse(value.getAddress());
    }
    else {
      return value.getAddress();
    }
  }

  /**
   *
   * @param array
   * @param offset
   * @param length
   * @return
   */
  public static byte[] getSubArray(byte[] array, int offset, int length) {
    if (array == null) {
      throw new NullPointerException("array may not be null");
    }
    if (offset < 0 || length < 0 || offset + length > array.length) {
      StringBuilder sb = new StringBuilder(50);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" length: ").append(length);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    byte[] subArray = new byte[length];
    System.arraycopy(array, offset, subArray, 0, length);
    return subArray;
  }

  /**
   *
   * @param array
   * @param separator
   * @return
   */
  public static String toHexString(byte[] array, String separator) {
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < array.length; i++) {
      buf.append(String.format("%02x", array[i]));
      buf.append(separator);
    }

    if (separator.length() != 0 && array.length > 0) {
      buf.delete(buf.lastIndexOf(separator), buf.length());
    }

    return buf.toString();
  }

  public static String toHexString(
    byte[] array, String separator, int offset, int length
  ) {
    if (offset < 0 || length < 0 || offset + length > array.length) {
      StringBuilder sb = new StringBuilder(100);
      sb.append("array: ").append(toHexString(array, " "))
        .append(" offset: ").append(offset)
        .append(" length: ").append(length);
      throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    StringBuffer buf = new StringBuffer();

    for (int i = offset; i < offset + length; i++) {
      buf.append(String.format("%02x", array[i]));
      buf.append(separator);
    }

    if (separator.length() != 0 && length > 0) {
      buf.delete(buf.lastIndexOf(separator), buf.length());
    }

    return buf.toString();
  }

  /**
   *
   * @param data
   * @return
   */
  public static short calcChecksum(byte[] data) {
    int sum = 0;
    for (int i = 0; i < data.length; i += SHORT_SIZE_IN_BYTES) {
      sum += (0xFFFF) & getShort(data, i);
    }

    sum
      = (0xFFFF & sum)
        + ((0xFFFF0000 & sum) >> (BYTE_SIZE_IN_BITS * SHORT_SIZE_IN_BYTES));
    sum
      = (0xFFFF & sum)
        + ((0xFFFF0000 & sum) >> (BYTE_SIZE_IN_BITS * SHORT_SIZE_IN_BYTES));

    return (short)(0xFFFF & ~sum);
  }

  /**
   *
   * @param hexString
   * @param separator
   * @return
   */
  public static byte[] parseByteArray(String hexString, String separator) {
    if (
         hexString == null
      || separator == null
    ) {
      StringBuilder sb = new StringBuilder();
      sb.append("hexString: ")
        .append(hexString)
        .append(" separator: ")
        .append(separator);
      throw new NullPointerException(sb.toString());
    }

    if (hexString.startsWith("0x")) {
      hexString = hexString.substring(2);
    }

    String noSeparatorHexString;
    if (separator.length() == 0) {
      if (
       !NO_SEPARATOR_HEX_STRING_PATTERN.matcher(hexString).matches()
      ) {
        StringBuilder sb = new StringBuilder(100);
        sb.append("invalid hex string(")
          .append(hexString)
          .append("), not match pattern(")
          .append(NO_SEPARATOR_HEX_STRING_PATTERN.pattern())
          .append(")");
        throw new IllegalArgumentException(sb.toString());
      }
      noSeparatorHexString = hexString;
    }
    else {
      StringBuilder patternSb = new StringBuilder(60);
      patternSb.append("\\A[0-9a-fA-F][0-9a-fA-F](")
               .append(Pattern.quote(separator))
               .append("[0-9a-fA-F][0-9a-fA-F])*\\z");
      String patternString = patternSb.toString();

      Pattern pattern = Pattern.compile(patternString);
      if (!pattern.matcher(hexString).matches()) {
        StringBuilder sb = new StringBuilder(150);
        sb.append("invalid hex string(")
          .append(hexString)
          .append("), not match pattern(")
          .append(patternString)
          .append(")");
        throw new IllegalArgumentException(sb.toString());
      }
      noSeparatorHexString
        = hexString.replaceAll(Pattern.quote(separator), "");
    }

    int arrayLength = noSeparatorHexString.length() / 2;
    byte[] array = new byte[arrayLength];
    for (int i = 0; i < arrayLength; i++) {
      array[i]
        = (byte)Integer.parseInt(
            noSeparatorHexString.substring(i * 2, i * 2 + 2),
            16
          );
    }

    return array;
  }

}
