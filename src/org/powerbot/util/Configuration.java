package org.powerbot.util;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.Adler32;

import org.powerbot.util.google.analytics.Tracker;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot";
	public static final boolean FROMJAR;
	public static boolean DEVMODE = false;
	public static final boolean SUPERDEV;
	public static final int VERSION = 4011;
	public static final String STORE;
	public static final File LOCK;
	public static final OperatingSystem OS;
	private static volatile Tracker tracker;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public interface URLs {
		public static final String DOMAIN = "powerbot.org";
		public static final String CONTROL = "http://links." + DOMAIN + "/control";

		public static final String GAME = "runescape.com";
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.ICON) != null;
		SUPERDEV = !Configuration.FROMJAR && new File(Resources.Paths.SERVER).exists();

		final String appdata = System.getenv("APPDATA"), home = System.getProperty("user.home");
		final String root = appdata != null && new File(appdata).isDirectory() ? appdata : home == null ? "~" : home;
		STORE = root + File.separator + NAME + ".db";

		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			OS = OperatingSystem.MAC;
		} else if (os.contains("Windows")) {
			OS = OperatingSystem.WINDOWS;
		} else if (os.contains("Linux")) {
			OS = OperatingSystem.LINUX;
		} else {
			OS = OperatingSystem.UNKNOWN;
		}

		LOCK = new File(System.getProperty("java.io.tmpdir"), String.format("%s.tmp", Long.toHexString(getUID())));
	}

	public static long getUID() {
		final Adler32 c = new Adler32();
		c.update(StringUtil.getBytesUtf8(Configuration.NAME));
		try {
			final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				final byte[] a = e.nextElement().getHardwareAddress();
				if (a == null || a.length == 0) {
					c.update(0x7f);
				} else {
					c.update(a);
				}
			}
		} catch (final SocketException ignored) {
		}
		return c.getValue();
	}

	public static void trackPageView(final String page, final String title) {
		if (tracker == null) {
			tracker = new Tracker("UA-5170375-18");
		}
		tracker.trackPageView("/" + page, title, "");
	}
}
