package org.csc.account.tree;

import java.util.concurrent.atomic.AtomicLong;

//import org.csc.crypt.j.ChainEncryptInstancePub;
//import org.csc.crypt.j.utils.enums.LogLevel;
import org.csc.ecrypto.impl.JavaCscImpl;

public class TestMM {

	public static void main(String[] args) {

//		LogLevel cLogLevel = LogLevel.valueOf("ERROR");
//		JavaCscImpl enc = new JavaCscImpl(ChainEncryptInstancePub.cscChainImpl("logs/ecrypto.log", cLogLevel));

//		System.out.println(enc);

		AtomicLong cc = new AtomicLong(0);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte bb[] = new byte[4196];
					while (true) {
//						enc.sha3Encode(bb);
						long ac = cc.incrementAndGet();
						if (ac % 1000000 == 0) {
							System.out.println("ShaPS=" + ac * 1000 / (System.currentTimeMillis() - start));
						}
					}
				}
			}).start();
		}

		// System.out.println("bb="+bb);

	}
}
