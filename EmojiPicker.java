// === EmojiPicker.java ===
package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class EmojiPicker extends JDialog {
	private static final String[] EMOJIS = {
			"😀", "😂", "😊", "😍", "😎", "😢", "😭", "😡", "👍", "👎", "❤️", "🔥", "🎉", "💯"
	};
	
	public EmojiPicker(JFrame parent, Consumer<String> onEmojiSelected) {
		super(parent, false);
		setUndecorated(true);
		setLayout(new GridLayout(3, 5, 4, 4));
		
		for (String emoji : EMOJIS) {
			JButton button = new JButton(emoji);
			button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
			button.setFocusPainted(false);
			button.addActionListener(e -> {
				onEmojiSelected.accept(emoji);
				dispose();
			});
			add(button);
		}
		
		pack();
		setLocationRelativeTo(parent);
	}
	
	public static void showPicker(JFrame parent, Component anchor, Consumer<String> onSelected) {
		EmojiPicker picker = new EmojiPicker(parent, onSelected);
		Point location = anchor.getLocationOnScreen();
		picker.setLocation(location.x, location.y - picker.getHeight());
		picker.setVisible(true);
	}
}
