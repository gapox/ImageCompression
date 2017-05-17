package peripheral;

import configs.Config;

import java.time.LocalDateTime;

public class Logger {


    public static long logFunctional(String s) {
        long now= System.currentTimeMillis();
        if (Config.LOG_FUNC & Config.WRITE_LOGS) {
            System.out.println(LocalDateTime.now() + ";	FUNC:	" + s);
        }
        return now;
    }

    public static long logMath(String s) {
        long now= System.currentTimeMillis();
        if (Config.LOG_MATH & Config.WRITE_LOGS) {
            System.out.println(LocalDateTime.now() + ";	MATH:	" + s);
        }
        return now;
    }

    public static long logRead(String s) {
        long now= System.currentTimeMillis();
        if (Config.LOG_READ & Config.WRITE_LOGS) {
            System.out.println(LocalDateTime.now() + ";	READ:	" + s);
        }
        return now;
    }

    public static long logWrite(String s) {
        long now= System.currentTimeMillis();
        if (Config.LOG_WRITE & Config.WRITE_LOGS) {
            System.out.println(LocalDateTime.now() + ";	WRITE:	" + s);
        }
        return now;
    }

    public static long logOverride(String s) {
        long now= System.currentTimeMillis();
        System.out.println((char) 27 + ("[31;4m" + LocalDateTime.now() + ";	OVERRIDDEN:	" + s));
        return now;
    }

    public static long logTime(String s, long from) {
        long now= System.currentTimeMillis();
        if (Config.LOG_TIME & Config.WRITE_LOGS) {
            long durationMillis=now - from;
            System.out.println(LocalDateTime.now() + ";	TIME:	" + s+" "+(durationMillis/1000.0)+" seconds.");
        }
        return now;
    }


    public static void warnFunctional(String s) {
        if (Config.LOG_FUNC & Config.WRITE_WARNS) {
            System.out.println((char) 27 + ("[33m" + LocalDateTime.now() + ";	WARN-FUNC:	" + s));
            System.out.print((char) 27 + "[0m");
        }
    }


    public static void warnMath(String s) {
        if (Config.LOG_MATH & Config.WRITE_WARNS) {
            System.out.println((char) 27 + ("[33m" + LocalDateTime.now() + ";	WARN-MATH:	" + s));
            System.out.print((char) 27 + "[0m");
        }
    }

    public static void warnRead(String s) {
        if (Config.LOG_READ & Config.WRITE_WARNS) {
            System.out.println((char) 27 + ("[33m" + LocalDateTime.now() + ";	WARN-READ:	" + s));
            System.out.print((char) 27 + "[0m");
        }
    }

    public static void warnWrite(String s) {
        if (Config.LOG_WRITE & Config.WRITE_WARNS) {
            System.out.println((char) 27 + ("[33m" + LocalDateTime.now() + ";	WARN-WRITE:	" + s));
            System.out.print((char) 27 + "[0m");
        }
    }


    public static void logSize(long bytes) {
        if (Config.LOG_SIZE & Config.WRITE_LOGS) {
            System.out.println(LocalDateTime.now() + ";	SIZE:	Size of written file:" + humanReadableByteCount(bytes, false));
        }
    }

    public static void logErr(String s, Exception e) {
        System.out.println((char) 27 + ("[31;4m" + LocalDateTime.now() + ";	ERROR:	" + s));
        System.out.print((char) 27 + "[0m");
        e.printStackTrace();
    }


    public static void logTest(String s){
        System.out.println((char) 27 + ("[32m" + LocalDateTime.now() + ";	TEST:	" + s));
        System.out.print((char) 27 + "[0m");

    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
