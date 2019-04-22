package org.csc.account.tree;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.brewchain.core.crypto.jni.IPPCrypto;
import org.csc.bcapi.EncAPI;
import org.csc.ecrypto.impl.EncInstance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;
import onight.tfw.outils.conf.PropHelper;

@Slf4j
public class EHelper {
	public final static String AlphbetMap = "+/ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public final static char []AlphbetArray = AlphbetMap.toCharArray();
	public static EncAPI encAPI = null;
	public final static int radix = AlphbetMap.length();
	public final static BigInteger modx = new BigInteger("" + radix);
	public final static HashMap<Character, Byte> charAtIdx = new HashMap<>();
	public static Cache<byte[], String> base58cache = CacheBuilder.newBuilder().build();

	public static void init() {
		if (encAPI == null) {
			encAPI = new EncInstance();
			try {
				((EncInstance) encAPI).startup();
			} catch (Throwable e) {
				log.error("cannot init EHelper:" + e.getMessage(), e);
			}
		}
		byte i = 0;
		for (char c : AlphbetMap.toCharArray()) {
			charAtIdx.put(c, i);
			i++;
		}
		PropHelper props = new PropHelper(null);
		base58cache = CacheBuilder.newBuilder().initialCapacity(10000).expireAfterWrite(300, TimeUnit.SECONDS)
				.maximumSize(props.get("org.csc.account.base62cache.maxsize", 1000000))
				.concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();

	}

	public static int findIdx(char ch) {
		return charAtIdx.get(ch);
	}

	private static int getZeroHeadSize(byte[] bb) {
		int ret = 0;
		while (ret < bb.length && bb[ret] == 0) {
			ret++;
		}
		// System.out.println("getZeroHeadSize="+ret);
		return ret;
	}

	private static int getZeroHeadSize(String str) {
		int ret = 0;
		while (ret < str.length() - 1 && str.charAt(ret) == '0' && str.charAt(ret + 1) == '0') {
			ret += 2;
		}
		return ret;
	}

	public static String bytesToMapping(byte[] bb) {
		String mapstr = base58cache.getIfPresent(bb);
		if (mapstr == null) {
			mapstr = encodeBase62(bb).toString();//bigIntToMapping(bb, getZeroHeadSize(bb));
			base58cache.put(bb, mapstr);
		}
		return mapstr;
	}

	public static String bytesToMapping(String hexEnc) {
		try {
			return bytesToMapping(Hex.decodeHex(hexEnc));
//			return bigIntToMapping(Hex.decodeHex(hexEnc), getZeroHeadSize(hexEnc));
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int getHeaderLength(String base62) {
		int offset = base62.length() - 2;
		int lenlen = charAtIdx.get(base62.charAt(offset));
		if (lenlen < 0) {
			throw new RuntimeException("over flow exception for :" + base62 + ",length=" + lenlen);
		}
		offset--;
		BigInteger targegtByteLength = BigInteger.ZERO;
		for (int i = 0; i < lenlen; i++) {
			byte _bb = charAtIdx.get(base62.charAt(offset - i));
			BigInteger bi = BigInteger.valueOf(_bb);
			targegtByteLength = targegtByteLength.multiply(modx).add(bi);
		}
		// System.out.println("lenlen=" + lenlen + ",zerolen=" +
		// targegtByteLength.intValue());
		return targegtByteLength.intValue();
	}

	public static byte[] bytesFrom(String base62) {
		return decodeBase62(base62.toCharArray());
	}
	
	private static byte[] bytesFromDeprecated(String base62) {
		int len = base62.length();
		// byte bb[] = new byte[len];
		BigInteger v = BigInteger.ZERO;
		int zeroHeadLength = 0;
		int signum = 0;
		if (base62.charAt(len - 1) == 'P') {
			zeroHeadLength = getHeaderLength(base62);
			len -= (charAtIdx.get(base62.charAt(len - 2)) + 1);
		} else if (base62.charAt(len - 1) == 'p') {
			signum = -1;
		} else if (base62.charAt(len - 1) == 'B') {
			signum = 1;
		}
		// read byte length
		for (int i = 0; i < len - 1; i++) {
			byte bb = charAtIdx.get(base62.charAt(i));
			BigInteger bi = BigInteger.valueOf(bb);
			v = v.multiply(modx).add(bi);
		}
		if (zeroHeadLength == 0) {
			if (signum == -1) {
				byte notzeros[] = v.toByteArray();
				byte arrays[] = new byte[notzeros.length - 1];
				System.arraycopy(notzeros, 1, arrays, 0, arrays.length);
				return arrays;
			}
			return v.toByteArray();
		} else {
			byte notzeros[] = v.toByteArray();
			int zsize = getZeroHeadSize(notzeros);
			if (zsize > 0) {
				byte arrays[] = new byte[zeroHeadLength + notzeros.length - zsize];
				System.arraycopy(notzeros, zsize, arrays, zeroHeadLength, notzeros.length - zsize);
				return arrays;
			} else {
				byte arrays[] = new byte[zeroHeadLength + notzeros.length];
				System.arraycopy(notzeros, 0, arrays, zeroHeadLength, notzeros.length);
				return arrays;
			}
		}
	}

	public static byte[] mapStringToByte(String base62) {
		return base62.getBytes();
		// int len = base62.length();
		// byte bb[] = new byte[len];
		// for (int i = 0; i < len; i++) {
		// bb[i] = charAtIdx.get(base62.charAt(i));
		// }
		// return bb;
	}

	public static String mapByteToString(byte bbs[]) {
		return new String(bbs);
		// StringBuffer sb = new StringBuffer();
		// for (byte bb : bbs) {
		// sb.append(AlphbetMap.charAt(bb));
		// }
		// return sb.toString();
	}

	public static StringBuilder encodeBase62(byte[] data) {
		StringBuilder sb = new StringBuilder(data.length * 2);
	    int pos = 0, val = 0;
	    for (int i = 0; i < data.length; i++) {
	        val = (val << 8) | (data[i] & 0xFF);
	        pos += 8;
	        while (pos > 5) {
	            char c = AlphbetArray[(val >> (pos -= 6))%64];
	            sb.append(
	            /**/c == 'i' ? "ia" :
	            /**/c == '+' ? "ib" :
	            /**/c == '/' ? "ic" : String.valueOf(c));
	            val &= ((1 << pos) - 1);
	        }
	    }
	    if (pos > 0) {
	        char c = AlphbetArray[val << (6 - pos)];
	        sb.append(
	        /**/c == 'i' ? "ia" :
	        /**/c == '+' ? "ib" :
	        /**/c == '/' ? "ic" : String.valueOf(c));
	    }
	    return sb;
	}

	public static byte[] decodeBase62(char[] data) {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
	    int pos = 0, val = 0;
	    for (int i = 0; i < data.length; i++) {
	        char c = data[i];
	        if (c == 'i') {
	            c = data[++i];
	            c =
	            /**/c == 'a' ? 'i' :
	            /**/c == 'b' ? '+' :
	            /**/c == 'c' ? '/' : data[--i];
	        }
	        val = (val << 6) | charAtIdx.get(c);
	        pos += 6;
	        while (pos > 7) {
	            baos.write(val >> (pos -= 8));
	            val &= ((1 << pos) - 1);
	        }
	    }
	    return baos.toByteArray();
	}
	@Deprecated
	private static String bigIntToMapping(byte[] bb, int zeroHeadLength) {
		StringBuffer sb = new StringBuffer();
		BigInteger v;
		if (zeroHeadLength > 0) {
			sb.append('P');

			int lv = zeroHeadLength;
			while (lv > 0) {
				sb.append(AlphbetMap.charAt(lv % radix));
				lv = lv / radix;
			}
			char lenV = AlphbetMap.charAt(sb.length() - 1);

			sb.insert(1, lenV);
			// System.out.println("zero-encode=" + sb.toString());
			v = new BigInteger(bb);
		} else if (bb[0] < 0) {
			sb.append('p');
			byte[] nbb = new byte[bb.length + 1];
			System.arraycopy(bb, 0, nbb, 1, bb.length);
			v = new BigInteger(nbb);
		} else {
			sb.append('B');
			v = new BigInteger(bb);
		}
		
		while (v.bitCount() > 0) {
			sb.append(AlphbetMap.charAt(v.mod(modx).intValue()));
			v = v.divide(modx);
		}

		return sb.reverse().toString();

	}

	public static void main(String[] args) {

		try {
			IPPCrypto.setLibraryPath("/Users/brew/Documents/BC/codes/ccks/zruntime/clib/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String test = "fafklajlfjjlQQFAFA239";
		init();
		byte[] bb = mapStringToByte(test);
		System.out.println("originlen=" + test.length());
		System.out.println("hex=" + bb.length + "" + "=>" + Hex.encodeHexString(bb));
		String ttest = mapByteToString(bb);
		String ttest2 = mapByteToString(bb);
		System.out.println("equals=" + test.equals(ttest) + "::" + ttest + "::" + ttest2);
		String hex = "00008282d1501200110025f341b020633232a0c330a052a2b350c173d0b0f1d040604170a2c10042f2074e2e01f8cfaf21560eeea8f86fed573000";
		byte bytebb[] = encAPI.hexDec(hex);
		String base62 = bytesToMapping(bytebb);
		System.out.println("mhex=" + base62);
		byte outbb[] = bytesFrom(base62);
		System.out.println("hex.len=" + hex.length() + "==>mhex.len" + base62.length());
		System.out.println("backup==" + encAPI.hexEnc(outbb));
		System.out.println("equal==" + Arrays.equals(bytebb, outbb));
		
		String nbase62=encodeBase62(bytebb).toString();
		System.out.println("nbase62.str="+nbase62);
		byte nbb[]=decodeBase62(nbase62.toCharArray());
		System.out.println("nbase62.bb="+encAPI.hexEnc(nbb));
		Random rand = new Random(System.currentTimeMillis());
//		for(int i=0;i<1000000;i++) {
//			int len = (int)(Math.abs(Math.random()*16)+10);
//			byte ranbb[]=new byte[len];
//			rand.nextBytes(ranbb);
//			String bstr = bytesToMapping(ranbb);
//			byte newranbb[] = bytesFrom(bstr);
//			if(!Arrays.equals(ranbb, newranbb)) {
//				System.out.println("not equals:"+bstr+".bb="+encAPI.hexEnc(ranbb)+"==>"+encAPI.hexEnc(newranbb));
//			}
//			
//		}

	}

}