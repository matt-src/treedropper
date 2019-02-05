package scripts;

import org.powerbot.script.Client;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.ClientContext;

class tofuFuncs {
    static class Tools {

        static String getTreeName(ClientContext ctx) {
        int wcLvl = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
        if (wcLvl < 15) {
            return "Tree";
        } else if (wcLvl < 30) {
            return "Oak";
        }
        return "Willow";
    }

        static long xpHr(ClientContext ctx, long startXp, long startTime) {
            long xpGained = ctx.skills.experience(Constants.SKILLS_WOODCUTTING) - startXp;
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            double hoursElapsed = (float) elapsed / 3600;
            return Math.round((1 / hoursElapsed) * xpGained);
        }

    }
}
