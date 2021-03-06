/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.generallycloud.nio.common;

public class MathUtil {

	public static byte binaryString2byte(String string) {

		Assert.notNull(string, "null binary string");
		char c0 = '0';
		char c1 = '1';
		if (string.length() != 8) {
			throw new IllegalArgumentException("except length 8");
		}
		char[] cs = string.toCharArray();
		byte result = 0;
		for (int i = 0; i < 8; i++) {
			char c = cs[i];
			int x = 0;
			if (c0 == c) {
			} else if (c1 == c) {
				x = 1;
			} else {
				throw new IllegalArgumentException(String.valueOf(c));
			}
			result = (byte) ((x << (7 - i)) | result);
		}
		return result;
	}

	public static String binaryString2HexString(String string) {
		byte b = binaryString2byte(string);
		return byte2HexString(b);
	}

	public static String byte2BinaryString(byte b) {
		StringBuilder builder = new StringBuilder();
		for (int i = 7; i > -1; i--) {
			builder.append(getBoolean(b, i) ? '1' : '0');
		}
		return builder.toString();
	}

	public static String byte2HexString(byte b) {
		return Integer.toHexString(b & 0xFF);
	}

	public static int byte2Int(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 3] & 0xff);
		int v1 = (bytes[offset + 2] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 1] & 0xff) << 8 * 2;
		int v3 = (bytes[offset + 0] & 0xff) << 8 * 3;
		return v0 | v1 | v2 | v3;

	}

	public static int byte2Int31(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 3] & 0xff);
		int v1 = (bytes[offset + 2] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 1] & 0xff) << 8 * 2;
		int v3 = (bytes[offset + 0] & 0x7f) << 8 * 3;
		return v0 | v1 | v2 | v3;
	}

	public static int byte2Int31LE(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 0] & 0xff);
		int v1 = (bytes[offset + 1] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 2] & 0xff) << 8 * 2;
		int v3 = (bytes[offset + 3] & 0x7f) << 8 * 3;
		return v0 | v1 | v2 | v3;

	}

	public static int byte2IntLE(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 0] & 0xff);
		int v1 = (bytes[offset + 1] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 2] & 0xff) << 8 * 2;
		int v3 = (bytes[offset + 3] & 0xff) << 8 * 3;
		return v0 | v1 | v2 | v3;
	}

	public static long byte2Long(byte[] bytes, int offset) {

		checkLength(bytes, 8, offset);

		long v0 = bytes[offset + 7] & 0xff;
		long v1 = (long) (bytes[offset + 6] & 0xff) << 8 * 1;
		long v2 = (long) (bytes[offset + 5] & 0xff) << 8 * 2;
		long v3 = (long) (bytes[offset + 4] & 0xff) << 8 * 3;
		long v4 = (long) (bytes[offset + 3] & 0xff) << 8 * 4;
		long v5 = (long) (bytes[offset + 2] & 0xff) << 8 * 5;
		long v6 = (long) (bytes[offset + 1] & 0xff) << 8 * 6;
		long v7 = (long) (bytes[offset + 0] & 0xff) << 8 * 7;
		return (v0 | v1 | v2 | v3 | v4 | v5 | v6 | v7);

	}

	public static long byte2LongLE(byte[] bytes, int offset) {

		checkLength(bytes, 8, offset);

		long v0 = bytes[offset + 0] & 0xff;
		long v1 = (long) (bytes[offset + 1] & 0xff) << 8 * 1;
		long v2 = (long) (bytes[offset + 2] & 0xff) << 8 * 2;
		long v3 = (long) (bytes[offset + 3] & 0xff) << 8 * 3;
		long v4 = (long) (bytes[offset + 4] & 0xff) << 8 * 4;
		long v5 = (long) (bytes[offset + 5] & 0xff) << 8 * 5;
		long v6 = (long) (bytes[offset + 6] & 0xff) << 8 * 6;
		long v7 = (long) (bytes[offset + 7] & 0xff) << 8 * 7;
		return (v0 | v1 | v2 | v3 | v4 | v5 | v6 | v7);
	}
	
	public static short byte2Short(byte[] bytes, int offset) {

		checkLength(bytes, 2, offset);
		
		int v0 = (bytes[offset + 1] & 0xff);
		int v1 = ((bytes[offset + 0] & 0xff) << 8 * 1);
		return (short)(v0 | v1);
	}

	public static short byte2ShortLE(byte[] bytes, int offset) {

		checkLength(bytes, 2, offset);

		int v0 = (bytes[offset + 0] & 0xff);
		int v1 = ((bytes[offset + 1] & 0xff) << 8 * 1);
		return (short)(v0 | v1);
	}
	
	public static long byte2UnsignedInt(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 3] & 0xff);
		int v1 = (bytes[offset + 2] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 1] & 0xff) << 8 * 2;
		long v3 = (long) (bytes[offset + 0] & 0xff) << 8 * 3;
		return v0 | v1 | v2 | v3;

	}
	
	public static long byte2UnsignedIntLE(byte[] bytes, int offset) {

		checkLength(bytes, 4, offset);

		int v0 = (bytes[offset + 0] & 0xff);
		int v1 = (bytes[offset + 1] & 0xff) << 8 * 1;
		int v2 = (bytes[offset + 2] & 0xff) << 8 * 2;
		long v3 = (long) (bytes[offset + 3] & 0xff) << 8 * 3;
		return v0 | v1 | v2 | v3;

	}
	
	public static int byte2UnsignedShort(byte[] bytes, int offset) {

		checkLength(bytes, 2, offset);

		int v0 = (bytes[offset + 1] & 0xff);
		int v1 = (bytes[offset + 0] & 0xff) << 8 * 1;
		return v0 | v1;

	}
	

	public static int byte2UnsignedShortLE(byte[] bytes, int offset) {

		checkLength(bytes, 2, offset);

		int v0 = (bytes[offset + 0] & 0xff);
		int v1 = (bytes[offset + 1] & 0xff) << 8 * 1;
		return v0 | v1;

	}

	public static String bytes2HexString(byte[] array) {

		if (array == null || array.length == 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[");

		for (int i = 0; i < array.length; i++) {

			builder.append("0x");

			builder.append(byte2HexString(array[i]));

			builder.append(",");
		}

		builder.deleteCharAt(builder.length() - 1);

		builder.append("]");

		return builder.toString();
	}

	private static void checkLength(byte[] bytes, int length, int offset) {

		if (bytes == null) {
			throw new IllegalArgumentException("null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException("invalidate offset " + offset);
		}

		if (bytes.length - offset < length) {
			throw new IllegalArgumentException("invalidate length " + bytes.length);
		}
	}

	public static int compare(long x, long y) {
		return (x < y) ? -1 : (x > y) ? 1 : 0;
	}

	public static int findNextPositivePowerOfTwo(final int value) {
		assert value > Integer.MIN_VALUE && value < 0x40000000;
		return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
	}

	/**
	 * 右起0 ~ 7
	 * 
	 * @param b
	 * @param pos
	 * @return
	 */
	public static boolean getBoolean(byte b, int pos) {
		if (pos < 0 || pos > 8) {
			throw new IllegalArgumentException("illegal pos," + pos);
		}
		return (b & (1 << pos)) >> pos == 1;
	}

	public static void int2Byte(byte[] bytes, int value, int offset) {

		checkLength(bytes, 4, offset);

		bytes[offset + 3] = (byte) ((value & 0xff));
		bytes[offset + 2] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 1] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 0] = (byte) ((value >> 8 * 3));
	}

	public static void int2ByteLE(byte[] bytes, int value, int offset) {

		checkLength(bytes, 4, offset);

		bytes[offset + 0] = (byte) ((value & 0xff));
		bytes[offset + 1] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 2] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 3] = (byte) ((value >> 8 * 3));
	}
	
	public static void unsignedInt2Byte(byte[] bytes, long value, int offset) {

		checkLength(bytes, 4, offset);

		bytes[offset + 3] = (byte) ((value & 0xff));
		bytes[offset + 2] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 1] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 0] = (byte) ((value >> 8 * 3));
	}

	public static void unsignedInt2ByteLE(byte[] bytes, long value, int offset) {

		checkLength(bytes, 4, offset);

		bytes[offset + 0] = (byte) ((value & 0xff));
		bytes[offset + 1] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 2] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 3] = (byte) ((value >> 8 * 3));
	}

	/* -------------------------------------------------------------------------------*/

	public static boolean isOutOfBounds(int index, int length, int capacity) {
		return (index | length | (index + length) | (capacity - (index + length))) < 0;
	}

	public static void long2Byte(byte[] bytes, long value, int offset) {

		checkLength(bytes, 8, offset);

		bytes[offset + 7] = (byte) ((value & 0xff));
		bytes[offset + 6] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 5] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 4] = (byte) ((value >> 8 * 3) & 0xff);
		bytes[offset + 3] = (byte) ((value >> 8 * 4) & 0xff);
		bytes[offset + 2] = (byte) ((value >> 8 * 5) & 0xff);
		bytes[offset + 1] = (byte) ((value >> 8 * 6) & 0xff);
		bytes[offset + 0] = (byte) ((value >> 8 * 7));

	}

	public static void long2ByteLE(byte[] bytes, long value, int offset) {

		checkLength(bytes, 8, offset);

		bytes[offset + 0] = (byte) ((value & 0xff));
		bytes[offset + 1] = (byte) ((value >> 8 * 1) & 0xff);
		bytes[offset + 2] = (byte) ((value >> 8 * 2) & 0xff);
		bytes[offset + 3] = (byte) ((value >> 8 * 3) & 0xff);
		bytes[offset + 4] = (byte) ((value >> 8 * 4) & 0xff);
		bytes[offset + 5] = (byte) ((value >> 8 * 5) & 0xff);
		bytes[offset + 6] = (byte) ((value >> 8 * 6) & 0xff);
		bytes[offset + 7] = (byte) ((value >> 8 * 7));

	}

	/*----------------------------------------------------------------------------*/

	public static int safeFindNextPositivePowerOfTwo(final int value) {
		return value <= 0 ? 1 : value >= 0x40000000 ? 0x40000000 : findNextPositivePowerOfTwo(value);
	}

	public static void short2Byte(byte[] bytes, short value, int offset) {

		checkLength(bytes, 2, offset);

		bytes[offset + 1] = (byte) (value & 0xff);
		bytes[offset + 0] = (byte) (value >> 8 * 1);
	}

	public static void short2ByteLE(byte[] bytes, short value, int offset) {

		checkLength(bytes, 2, offset);

		bytes[offset + 0] = (byte) (value & 0xff);
		bytes[offset + 1] = (byte) (value >> 8 * 1);
	}
	
	public static void unsignedShort2Byte(byte[] bytes, int value, int offset) {

		checkLength(bytes, 2, offset);

		bytes[offset + 1] = (byte) (value & 0xff);
		bytes[offset + 0] = (byte) (value >> 8 * 1);
	}

	public static void unsignedShort2ByteLE(byte[] bytes, int value, int offset) {

		checkLength(bytes, 2, offset);

		bytes[offset + 0] = (byte) (value & 0xff);
		bytes[offset + 1] = (byte) (value >> 8 * 1);
	}
	
	public static int int2int31(int value){
		if (value < 0) {
			return value & 0x7FFFFFFF;
		}
		return value;
	}

}
