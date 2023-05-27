/* Log Values; log-values.com 2023 */
package org.emdepub.common.ui;

import java.util.StringJoiner;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.emdepub.common.resources.CR;
import org.emdepub.common.resources.CR.Colors;
import org.emdepub.common.utils.CU;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Color with foreground and background */
//@SuppressWarnings({ "java:S3776", "java:S1319", "java:S1066", "java:S5663", "java:S1612", "java:S135", "java:S125" })
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UIColors {

	Color foreground;

	Color background;

	
	public static UIColors create(Color foreground, Color background) {

		return new UIColors(foreground, background);
	}

	public static Color deserializeColor(String serializedColor) {

		String[] splitValues = serializedColor.split(",");

		return new Color(new RGB(Integer.valueOf(CU.trimStrip(splitValues[0])), Integer.valueOf(CU.trimStrip(splitValues[1])),
				Integer.valueOf(CU.trimStrip(splitValues[2]))));
	}

	public static Color deserializeColor(String serializedColors, int index) {

		return deserializeColor(findSerializedColor(serializedColors, index));
	}

	public static UIColors deserializeColors(String serializedColors) {

		String[] splitColors = serializedColors.split(";");

		return UIColors.create(deserializeColor(CU.trimStrip(splitColors[0])), deserializeColor(CU.trimStrip(splitColors[1])));
	}

	public static String findSerializedColor(String serializedColors, int index) {

		String[] splitColors = serializedColors.split(";");

		return CU.trimStrip(splitColors[index]);
	}

	public static String replaceSerializedColor(String serializedColors, String serializedColor, int index) {

		if (index == 0) {
			String otherSerializedColor = UIColors.findSerializedColor(serializedColors, 1);
			return serializeSerializedColors(serializedColor, otherSerializedColor);
		}

		String otherSerializedColor = UIColors.findSerializedColor(serializedColors, 0);
		return serializeSerializedColors(otherSerializedColor, serializedColor);
	}

	public static String serializeRGB(RGB rgb) {

		StringJoiner stringJoiner = new StringJoiner(", ");
		stringJoiner.add(rgb.red + "");
		stringJoiner.add(rgb.green + "");
		stringJoiner.add(rgb.blue + "");

		return stringJoiner.toString();
	}

	public static String serializeColor(Color color) {

		return serializeRGB(color.getRGB());
	}

	public static String serializeSerializedColors(String serializedForegroundColor, String serializedBackgroundColor) {

		return serializedForegroundColor + " ; " + serializedBackgroundColor;
	}

	public static String serializeColors(Color foregroundColor, Color backgroundColor) {

		return serializeSerializedColors(serializeColor(foregroundColor), serializeColor(backgroundColor));
	}

	public static String serializeUIColors(UIColors colors) {

		return serializeColors(colors.getForeground(), colors.getBackground());
	}

	public static String serializeResourceColors(Colors resourceForegroundColor, Colors resourceBackgroundColor) {

		return serializeColors(CR.getColor(resourceForegroundColor), CR.getColor(resourceBackgroundColor));
	}

	public static String serializeResourceColor(Colors resourceColor) {

		return serializeColor(CR.getColor(resourceColor));
	}
}
