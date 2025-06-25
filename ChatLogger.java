// === ChatLogger.java ===
package chat;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
	private static final String LOG_DIR = "logs";
	
	public static void log(int userId, String sender, String content) {
		try {
			Files.createDirectories(Paths.get(LOG_DIR));
			String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
			String filename = LOG_DIR + "/chat_user_" + userId + "_" + date + ".txt";
			String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			
			String line = String.format("[%s] %s: %s%n", time, sender, content);
			Files.write(Paths.get(filename), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
