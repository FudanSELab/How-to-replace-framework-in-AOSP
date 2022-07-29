package android.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @hide
 */
public class DelayMap {
    public static class DelayPoint {
        public String className;
        public String methodName;
        public Integer loc;
        public Integer delay;

        public String key() {
            return className + "#" + methodName + ":" + loc;
        }

        public String toString() {
            return key() + "(" + delay + ")";
        }

        public static String composeKey(String className, String methodName, Integer loc) {
            return className + "#" + methodName + ":" + loc;
        }

        public static DelayPoint newInstance(String className, String methodName, Integer loc, Integer delay) {
            DelayPoint dp = new DelayPoint();
            dp.className = className;
            dp.methodName = methodName;
            dp.loc = loc;
            dp.delay = delay;
            return dp;
        }
    }

    // store apps->threads->delay points
    public static Map<String, Map<String, Map<String, DelayPoint>>> M = new HashMap<>();


    /**
     * insertDelayPoint only insert DelayPoint.
     * 1. if identical DelayPoint already exists, update it.
     * 2. if app name not existed, create a k-v.
     * 3. if thread name not existed, craete a k-v.
     *
     * this method don't throw exception.
     * @param aName
     * @param tName
     * @param dp
     * @return whether input DelayPoint already existed
     */
    public static boolean insertDelayPoint(String aName, String tName, DelayPoint dp) {
        boolean existed = false;
        if (!DelayMap.M.containsKey(aName)) {
            DelayMap.M.put(aName, new HashMap<>());
        }
        Map<String, Map<String, DelayPoint>> appConfig = DelayMap.M.get(aName);
        if (!appConfig.containsKey(tName)) {
            appConfig.put(tName, new HashMap<>());
        }
        Map<String, DelayPoint> threadConfig = appConfig.get(tName);
        existed = threadConfig.containsKey(dp.key());
        // update even if key already existed
        threadConfig.put(dp.key(), dp);
        return existed;
    }

    /**
     * getDelayTime (millisecond)
     * 1. if app not existed, return 0.
     * 2. if thread not existed, return 0.
     * 3. if dp not existed, return 0.
     *
     * this method don't throw exception.
     * @param aName
     * @param tName
     * @param className
     * @param methodName
     * @param loc
     * @return delay time (Integer)
     */
    public static Integer getDelayTime(
            String aName, String tName, String className, String methodName, Integer loc) {
        if (!DelayMap.M.containsKey(aName)) {
            return 0;
        }
        Map<String, Map<String, DelayPoint>> appConfig = DelayMap.M.get(aName);
        if (!appConfig.containsKey(tName)) {
            return 0;
        }
        Map<String, DelayPoint> threadConfig = appConfig.get(tName);
        String key = DelayPoint.composeKey(className, methodName, loc);
        if (!threadConfig.containsKey(key)) {
            return 0;
        }
        return threadConfig.get(key).delay;
    }

    /**
     * alternative API
     * @param aName
     * @param tName
     * @param dp
     * @return
     */
    public static Integer getDelayTime(String aName, String tName, DelayPoint dp) {
        return getDelayTime(aName, tName, dp.className, dp.methodName, dp.loc);
    }

    public static String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        builder.append("<apps>\n");

        for (Map.Entry<String, Map<String, Map<String, DelayPoint>>> entry : M.entrySet()) {
            builder.append("\t<app>\n");
            String aname = entry.getKey();
            builder.append("\t\t<aname>" + aname + "</aname>\n");
            builder.append("\t\t<threads>\n");

            for (Map.Entry<String, Map<String, DelayPoint>> entry1 : entry.getValue().entrySet()) {
                builder.append("\t\t\t<thread>\n");
                String tname = entry1.getKey();
                builder.append("\t\t\t\t<tname>" + tname + "</tname>\n");
                builder.append("\t\t\t\t<delaypoints>\n");

                for (Map.Entry<String, DelayPoint> entry2 : entry1.getValue().entrySet()) {
                    builder.append("\t\t\t\t\t<delaypoint>\n");
                    DelayPoint dp = entry2.getValue();
                    builder.append("\t\t\t\t\t\t<class>" + dp.className + "</class>\n");
                    builder.append("\t\t\t\t\t\t<method>" + dp.methodName + "</method>\n");
                    builder.append("\t\t\t\t\t\t<loc>" + dp.loc + "</loc>\n");
                    builder.append("\t\t\t\t\t\t<delay>" + dp.delay + "</delay>\n");
                    builder.append("\t\t\t\t\t</delaypoint>\n");
                }

                builder.append("\t\t\t\t</delaypoints>\n");
                builder.append("\t\t\t</thread>\n");
            }

            builder.append("\t\t</threads>\n");
            builder.append("\t</app>\n");
        }

        builder.append("</apps>\n");
        return builder.toString();
    }
}
