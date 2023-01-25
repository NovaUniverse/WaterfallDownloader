package net.novauniverse.paperdownloader;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class Tests {
	@Test
	public void testGetVersion() {
		WaterfallDownloader downloader = new WaterfallDownloader();
		try {
			System.out.println("Trying to get latest version");
			String version = downloader.getLatestVersion();
			System.out.println("Returned data: " + version);
			assert version.length() > 0 : "Version string returned empty value";
		} catch (IOException e) {
			e.printStackTrace();
			fail("An error occured while fetching latest version. Error: " + e.getClass().getName() + " " + e.getMessage());
		}
	}

	@Test
	public void testGetBuildNumber() {
		WaterfallDownloader downloader = new WaterfallDownloader();
		try {
			System.out.println("Trying to get latest build of 1.19");
			int build = downloader.getLatestBuildNumber("1.19");
			System.out.println("Returned data: " + build);
			assert build > 0 : "Build number seems to be invalid (" + build + ")";
		} catch (IOException e) {
			e.printStackTrace();
			fail("An error occured while fetching build number for 1.19. Error: " + e.getClass().getName() + " " + e.getMessage());
		}
	}
	
	@Test
	public void testDownloadLatest() {
		File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID() + ".jar");
		System.out.println("Random temp directory for download test: " + tempFile.getAbsolutePath());
		WaterfallDownloader downloader = new WaterfallDownloader();
		try {
			downloader.downloadLatestWaterfall(tempFile, true);
			assert tempFile.exists() : "Temporary downloaded file not found";
			tempFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
			fail("An error occured while downloading jar. Error: " + e.getClass().getName() + " " + e.getMessage());
		}
	}
}