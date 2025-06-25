// === AvatarUtil.java ===
package chat;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AvatarUtil {
	private static final String AVATAR_FOLDER = "resources/avatars/";
	private static final String DEFAULT_AVATAR = "default.png";
	
	public static ImageIcon getAvatarIcon(int userId, int size) {
		String path = AVATAR_FOLDER + userId + ".png";
		File file = new File(path);
		if (!file.exists()) {
			path = AVATAR_FOLDER + DEFAULT_AVATAR;
		}
		ImageIcon icon = new ImageIcon(path);
		Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
