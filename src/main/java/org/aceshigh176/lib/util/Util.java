package org.aceshigh176.lib.util;

import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.geometry.Translation2d;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

public class Util {

    /**
     * Clamps n between the upper and lower and bound
     *
     * @param n     the value
     * @param lower the lower bound
     * @param upper the upper bound
     * @return The clamped value
     */
    static int clamp(int n, int lower, int upper) {
        if (n < lower) {
            return lower;
        } else if (n > upper) {
            return upper;
        }
        return n;
    }

    /**
     * Clamps n between the upper and lower and bound
     *
     * @param n     the value
     * @param lower the lower bound
     * @param upper the upper bound
     * @return The clamped value
     */
    static double clamp(double n, double lower, double upper) {
        if (n < lower) {
            return lower;
        } else if (n > upper) {
            return upper;
        }
        return n;
    }

    public static boolean valueInTolerance(double targetValue, double actualValue, double tolerance) {
        return Math.abs(targetValue - actualValue) < tolerance;
    }

    /**
     * Converts an integer to a boolean, c-style.
     *
     * @param value The integer to convert
     * @return The c-equivalent bool value
     */
    public static boolean int2bool(int value) {
        return value != 0;
    }

    /**
     * Converts a boolean to an integer, c-style.
     *
     * @param value The boolean to convert
     * @return 1 if true, 0 otherwise
     */
    public static int bool2int(boolean value) {
        return value ? 1 : 0;
    }

    public static long convertLV2JavaTime(double lvTimestamp) {
        return (long) ((lvTimestamp - (24107 /* days */ * 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */)) * 1000);
    }

    public static Date convertLV2JavaDate(String dateString) throws ParseException {
        // 06:43:00.597 PM 04/17/2018
        SimpleDateFormat parser = new SimpleDateFormat("hh:mm:ss.SSS a MM/dd/yyyy");
        Date date = parser.parse(dateString);
        return date;
    }

    //public static IEEE754Number convertJava2LVTime(Date date) {
//		IEEE754Number num = IEEE754Number
    //	return null;
    //}

    public static String getCurrentISO8601Timestamp() {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
        return parser.format(ZonedDateTime.now());
    }

    public static String getCurrentFileCompatibleISO8601Timestamp() {
        return getCurrentISO8601Timestamp().replace(":", "-");
    }

    public static double deadband(double value, double tolerance) {
        if (Math.abs(value) < tolerance) {
            value = 0;
        }
        return value;
    }

    /*
     * Limits the given input to the given magnitude.
     */
    public static double limit(double v, double maxMagnitude) {
        return limit(v, -maxMagnitude, maxMagnitude);
    }

    public static double limit(double v, double min, double max) {
        return Math.min(max, Math.max(min, v));
    }


    public static Translation2d do3dLinePlaneIntersection(Rotation2d horizontal, Rotation2d vertical, double z_plane_distance) {
        // Convert to spherical coordinates https://en.wikipedia.org/wiki/Spherical_coordinate_system
        Rotation2d phi = horizontal;
        Rotation2d theta = Rotation2d.fromDegrees(90).rotateBy(vertical.inverse());

        // Convert to cartesian unit vector (radius r is implicitly 1, inclination theta, azimuth phi)
        double vector_x = theta.sin() * phi.cos();
        double vector_y = theta.sin() * phi.sin();
        double vector_z = theta.cos();

        // Determine the scaling of the z component of the unit vector to get to the plane
        double scaling = z_plane_distance / vector_z;

        // Scale the x and y component by said scaling.
        return new Translation2d(vector_x * scaling, vector_y * scaling);
    }

    public static <T> void runFunctionWithRetry(Function<Void, T> functionCall, T shouldReturn, int retryCount,
                                                boolean printOnEveryFail, String retryString,
                                                String failureString, Logger log) {
        boolean setSucceeded;
        int retryCounter = 0;

        do {
            T result = functionCall.apply(null);
            setSucceeded = result.equals(shouldReturn);
            if(!setSucceeded) {
                log.error(retryString, retryCounter, result);
            }
        } while(!setSucceeded && retryCounter++ < retryCount);

        if (retryCounter >= retryCount || !setSucceeded) {
            log.error(failureString);
        }
    }

    /**
     * Takes a <pre>text</pre> block and indents it by <pre>indent</pre>
     *
     * Example:
     * this
     *     is
     *     some
     *     text
     *
     * becomes
     *     this
     *         is
     *         some
     *         text
     *
     * @return
     */
    public static String indentStringBlock(String text, String indent, String lineSeperator) {
        // Only adds whitespace after newlines that aren't the very last newline
        return indent + text.replaceAll("" + lineSeperator + "(.)", lineSeperator + indent + "$1");
    }

    public static String indentStringBlock(String text, String indent) {
        // Only adds whitespace after newlines that aren't the very last newline
        return indentStringBlock(text, indent, "\n");
    }

    public static String indentStringBlock(String text) {
        // Only adds whitespace after newlines that aren't the very last newline
        return indentStringBlock(text, "    ", "\n");
    }

    public static String indentStringBlockLineSep(String text, String lineSeperator) {
        // Only adds whitespace after newlines that aren't the very last newline
        return indentStringBlock(text, "    ", lineSeperator);
    }

    public static Rotation2d computeAverageAngle(Collection<Rotation2d> rotations) {
//        Math.atan2(/* sum of sines */, /* sum of cosines */)
        return Rotation2d.fromRadians(Math.atan2(
                rotations.stream().mapToDouble(rot -> rot.sin()).sum(),
                rotations.stream().mapToDouble(rot -> rot.cos()).sum()));
    }

    public static double applyVoltageInterceptOffset(double value, double ks) {
        if(value == 0) {
            return 0;
        } else {
		    return value + ks * Math.signum(value);
        }
    }

    /**
     * Squares <code>val</code> while preserving its sign
     *
     * @param val the number to square
     * @return
     */
    public static double squareSigned(double val) {
        return val * val * Math.signum(val);
    }

    public static double convertVelocityToOpenLoop(double desiredVelocity, double kv) {
        // Where open_loop_demand = desired_velocity / kv where kv is (inches/s) /output
        return desiredVelocity / kv;
    }

}
