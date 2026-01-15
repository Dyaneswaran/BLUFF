package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class RoundedButtonUI extends BasicButtonUI {
	private Color background;
	private Color foreground;

	public RoundedButtonUI(Color background, Color foreground) {
		this.background = background;
		this.foreground = foreground;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		AbstractButton button = (AbstractButton) c;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(background);
		g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);

		g2.setColor(foreground);
		g2.drawRoundRect(0, 0, button.getWidth() - 1, button.getHeight() - 1, 20, 20);

		FontMetrics fm = g.getFontMetrics();
		Rectangle textRect = button.getBounds();
		g2.setColor(foreground);
		g2.drawString(button.getText(), (textRect.width - fm.stringWidth(button.getText())) / 2,
				(textRect.height + fm.getAscent() - fm.getDescent()) / 2);
	}
}