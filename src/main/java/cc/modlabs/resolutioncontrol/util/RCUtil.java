package cc.modlabs.resolutioncontrol.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class RCUtil {
    private static final String[] UNITS = "KMGTPE".split("");
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * Format number with metric conversion (K, M, G etc)
     */
    public static String formatMetric(long n) {
        if (n < 1000) {
            return n + " ";
        }

        int log10 = (int) Math.log10(n);
        int decimalPlace = (int) Math.pow(10, 2 - log10 % 3);
        int displayDigits = (int) (n / Math.pow(10, log10 - 2));

        float result = (float) displayDigits / decimalPlace;
        return String.format("%s %s", FORMAT.format(result), UNITS[log10 / 3 - 1]);
    }

    public static File getScreenshotFilename(File directory) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;

        while (true) {
            File file = new File(new File(""), "fb" + string + (i == 1 ? "" : "_" + i) + ".png");
            File entireDirectory = new File(new File(directory, "screenshots"), file.toString());
            if (!entireDirectory.exists()) {
                try {
                    entireDirectory.createNewFile();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                return file;
            }

            ++i;
        }
    }
}
