package net.novauniverse.paperdownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class WaterfallDownloader {
	private String useragent;

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public WaterfallDownloader(String useragent) {
		this.useragent = useragent;
	}
	
	public WaterfallDownloader() {
		this("NovaWaterfallDownloader/1.0.0");
	}

	public static void main(String[] args) {
		File output;
		if (args.length == 0) {
			output = new File("waterfall.jar").getAbsoluteFile();
		} else {
			String fileName = String.join(" ", args).trim();
			if (fileName.length() == 0 || fileName.endsWith("\\") || fileName.endsWith("/")) {
				System.err.println("Invalid output file name: " + fileName);
				System.exit(1);
				return;
			}
			output = new File(fileName).getAbsoluteFile();
		}
		System.out.println("Output file is " + output.getPath());

		if (!output.getParentFile().exists()) {
			System.err.println("Parent file not found of path " + output.getPath());
			System.exit(1);
			return;
		}

		WaterfallDownloader downloader = new WaterfallDownloader();

		try {
			downloader.downloadLatestWaterfall(output, true);
			if (output.exists()) {
				System.out.println("Waterfall jar downloaded to " + output.getPath());
				System.exit(0);
			} else {
				System.err.println("Something went wrong while downloading and the output file could not be found");
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("An error occured while trying to download latest version of waterfall");
			System.exit(1);
		}
	}

	public void downloadLatestWaterfall(File output, boolean log) throws IOException {
		if (log) {
			System.out.println("Fetching latest waterfall version...");
		}
		String version = getLatestVersion();
		if (log) {
			System.out.println("Latest version: " + version);
			System.out.println("Fetching latest build number...");
		}
		int buildNumber = getLatestBuildNumber(version);
		if (log) {
			System.out.println("Latest build number: " + buildNumber);
			System.out.println("Downloading jar");
		}
		downloadJar(version, buildNumber, output);
		if (log) {
			System.out.println("Completed");
		}
	}

	public void downloadJar(String version, int buildNumber, File output) throws MalformedURLException, IOException {
		String url = "https://api.papermc.io/v2/projects/waterfall/versions/" + version + "/builds/" + buildNumber + "/downloads/waterfall-" + version + "-" + buildNumber + ".jar";
		FileUtils.copyURLToFile(new URL(url), output);
	}

	public int getLatestBuildNumber(String version) throws IOException {
		JSONObject json = makeGetReuest("https://api.papermc.io/v2/projects/waterfall/versions/" + version);
		JSONArray builds = json.getJSONArray("builds");
		return builds.getInt(builds.length() - 1);
	}

	public String getLatestVersion() throws IOException {
		JSONObject json = makeGetReuest("https://api.papermc.io/v2/projects/waterfall");
		JSONArray versions = json.getJSONArray("versions");
		return versions.getString(versions.length() - 1);
	}

	public JSONObject makeGetReuest(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", useragent);
		connection.setRequestProperty("accept", "application/json");

		connection.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new JSONObject(response.toString());
	}
}
