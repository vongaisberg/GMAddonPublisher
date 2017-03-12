/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import essentials.Essentials;

/**
 * @author Maximilian
 *
 */
public class Tools {

	/**
	 * @throws IOException
	 * @throws MalformedURLException
	 * 
	 */
	static String[][] getAddons(String user) throws MalformedURLException,
			IOException {
		final String regex = "<a href=\"http:\\/\\/steamcommunity\\.com\\/sharedfiles\\/filedetails\\/\\?id=\\d*\"><div class=\"workshopItemTitle ellipsis\">.*<\\/div><\\/a>";
		String page = "";

		for (int i = 1; i < 5; i++) {
			page = page
					+ Essentials.sendHTTPRequest(new URL(user
							+ "/myworkshopfiles/?appid=4000&p=" + i
							+ "&numperpage=30"));
		}
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(page);

		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		while (matcher.find()) {
			String match = matcher.group(0);
			String id = match.substring(63, match.indexOf("\"", 63));
			String name = match.substring(match.indexOf(">", 113) + 1,
					match.indexOf("<", 114));
			ids.add(id);
			names.add(name);
		}

		String[][] result = new String[ids.size()][2];
		for (int i = 0; i < ids.size(); i++) {
			result[i][0] = ids.get(i);
			result[i][1] = names.get(i);
		}
		return result;
	}

	static BufferedReader createGMA(String path) {
		String command = "cmd /c \"cd \"" + Main.path
				+ "\" && gmad.exe create -folder \"" + path + "\" -out \""
				+ path + ".gma\"\"";
		Main.log.info("Running command: "+command);

		try {
			Process p = Runtime.getRuntime().exec(command);
			// p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			return reader;

		} catch (IOException e1) {
			Main.log.logStackTrace(e1);
			return null;
		}

	}

	static BufferedReader uploadAddon(String path, String icon) {
		String command = "cmd /c \"cd \"" + Main.path
				+ "\" && gmpublish create -addon \"" + path + ".gma\" -icon \""
				+ icon + "\"\"";
		Main.log.info("Running command: "+command);

		try {
			Process p = Runtime.getRuntime().exec(command);
			// p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			return reader;

		} catch (IOException e1) {
			Main.log.logStackTrace(e1);
			return null;
		}

	}

	static BufferedReader updateAddon(String path, String id, String message) {
		String command = "cmd /c \"cd \"" + Main.path
				+ "\" && gmpublish.exe update -addon \"" + path
				+ ".gma\" -id \"" + id + "\" -changes \"" + message + "\"";
		Main.log.info("Running command: "+command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			// p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			return reader;

		} catch (IOException e1) {
			Main.log.logStackTrace(e1);
			return null;
		}

	}

	static BufferedReader updateIcon(String id, String path) {
		String command = "cmd /c \"cd \"" + Main.path
				+ "\" && gmpublish.exe update -icon \"" + path + "\" -id \""
				+ id + "\" \"";
		Main.log.info("Running command: " + command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			// p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			return reader;

		} catch (IOException e1) {
			Main.log.logStackTrace(e1);
			return null;
		}

	}

}
