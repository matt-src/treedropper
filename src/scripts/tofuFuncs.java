package scripts;

import org.powerbot.script.Client;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.ClientContext;


class tofuFuncs {
    public static class Tools {
    public  static String getTreeName(ClientContext ctx) {
        int wcLvl = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
        if (wcLvl < 15) {
            return "Tree";
        } else if (wcLvl < 30) {
            return "Oak";
        }
        return "Willow";
    }
    }
}
