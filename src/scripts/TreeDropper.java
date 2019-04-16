package scripts;

import java.awt.*;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static scripts.tofuFuncs.Tools.*;

@Script.Manifest(name="Tree Dropchopper", description="drops n chops the highest lvl trees available")
public class TreeDropper extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private List<Task> taskList = new ArrayList<>();
    private long startTime, abStart, abWait;
    private long abElapsed = 0;
    private long startXp = 0;
    private long lastInputTime = 0;
    private long lastInputTimeElapsed = 0;
    private int wcLvl = 0;
    private String treeName = "";
    private boolean shiftDrop = true;

    @Override
    public void start() {
        wcLvl = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
        startXp = ctx.skills.experience(Constants.SKILLS_WOODCUTTING);
        if(wcLvl < 15){
            treeName = "Tree";
        } else if(wcLvl < 30){
            treeName = "Oak";
        } else if (wcLvl < 60) {
            treeName = "Willow";
        }
        startTime = System.currentTimeMillis();
        abStart = startTime;
        lastInputTime = startTime;
        abWait = Random.nextGaussian(5, 280, 60, 100);
        taskList.addAll(Arrays.asList(new Chop(ctx, treeName), new Drop(ctx)));
    }

    @Override
    public void poll() {
        lastInputTimeElapsed = (System.currentTimeMillis() - lastInputTime) / 1000;
        antiLogout(); //Makes sure we don't get logged out for inactivity if we end up chopping the same tree for a long time
        //Handle antiban
        abElapsed = (System.currentTimeMillis() - abStart) / 1000;
        if (abElapsed > abWait) {
            doAntiban();
            abStart = System.currentTimeMillis();
            abWait = Random.nextGaussian(5, 280, 60, 100);
        }
        for(Task task : taskList) {
            if(task.activate()) {
                task.execute();
                wcLvl = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
                lastInputTime = System.currentTimeMillis();
            }
        }
    }

    private void antiLogout() { //Makes sure we don't get logged out for inactivity if we end up chopping the same tree for a long time
        if (lastInputTimeElapsed > 250) {
            System.out.println("anti logout movement");
            if (ctx.game.tab(Game.Tab.INVENTORY)) { //Switch to inventory tab if we're not already on it, to prevent auto logout
                ctx.camera.angle(Random.nextInt(0, 300)); //We're already on inventory tab, we'll move camera instead to prevent auto logout
            }
            lastInputTime = System.currentTimeMillis();
        }
    }

    private void doAntiban() {
        int decision = Random.nextGaussian(0, 6, 1, 50);
        decision = Math.round(decision);
        System.out.println("Decision is: " + decision);
        switch(decision){
            case 0:
                moveMouseRandom();
                break;
            case 1:
                moveMouseOffscreenRandom();
                break;
            case 2:
                if(!ctx.objects.select().name(treeName).isEmpty()){
                    ctx.camera.turnTo(ctx.objects.nearest().poll());
                    lastInputTime = System.currentTimeMillis();
                }
                break;
            case 3:
                ctx.camera.angle(Random.nextInt(0, 300));
                lastInputTime = System.currentTimeMillis();
                break;
            case 4:
                ctx.game.tab(Game.Tab.STATS);
                break;
            case 5:
                afkBreak();
                break;
        }

    }

    private void afkBreak() { //Take a break to simulate going AFK or getting distracted
        System.out.println("Going afk for a bit");
        Condition.sleep(Random.nextGaussian(20000, 60000, 45000, 100)); //Simulate a break/distraction (TODO: check for anti logout timer while breaking or have auto relog if possible)
    }

    private String formatTime(long _elapsed) { //Format time to an hours, minutes, seconds string
        long elapsed = _elapsed / 1000;
        String elapsedSeconds = Long.toString(elapsed % 60);
        String elapsedMinutes = Long.toString((elapsed / 60) % 60);
        String elapsedHours = Long.toString((elapsed / 3600) % 24);
        return elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds;
    }

    private void moveMouseRandom() {
        int x = Random.nextInt(0, ctx.game.dimensions().width - 1);
        int y = Random.nextInt(0, ctx.game.dimensions().height - 1);
        ctx.input.move(new Point(x, y));
    }

    private void moveMouseOffscreenRandom() {
        System.out.println("moving mouse off screen random");
        int direction = Random.nextGaussian(0, 4, 1, 50);
        int x, y;
        x = y = 0;
        switch(direction){
            case 0:
                System.out.println("Moving up");
                x = Random.nextInt(0, ctx.game.dimensions().width - 1);
                y = -10;
                break;
            case 1:
                System.out.println("Moving left");
                x = -10;
                y = Random.nextInt(0, ctx.game.dimensions().height - 1);
                break;

                case 2:
                System.out.println("Moving right");
                x = ctx.game.dimensions().width + 10;
                y = Random.nextInt(0, ctx.game.dimensions().height - 1);
                break;
            case 3:
                System.out.println("Moving down");
                x = Random.nextInt(0, ctx.game.dimensions().width - 1);
                y = Random.nextInt(0, ctx.game.dimensions().height + 10);
                break;
        }
        ctx.input.move(new Point(x, y));

    }

    @Override
    public void repaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        long elapsed = System.currentTimeMillis() - startTime;
        abElapsed = (System.currentTimeMillis() - abStart) / 1000;
        String elapsedString = formatTime(elapsed);
        String abWaitString = Long.toString(abWait);
        String treeName = getTreeName(ctx);
        long xphr = xpHr(ctx, startXp, startTime);
        g.drawString("Time Running: " + elapsedString, 50, 75);
        g.drawString("XP per hour: " + xphr, 50, 100);
        g.drawString("Time Elapsed Since Last Antiban: " + abElapsed, 50, 125);
        g.drawString("Next antiban at: " + abWaitString + " elapsed", 50, 150);
        g.drawString("Current level: " + wcLvl, 50, 175);
        g.drawString("Target tree: " + treeName, 50, 200);
        g.drawString("Shift Drop? " + shiftDrop, 50, 225);
        g.drawString("Time Elapsed Since Last Input: " + lastInputTimeElapsed, 50, 250);
    }

    @Override
    public void messaged(MessageEvent e) {
        if (e.text().toLowerCase().contains("advanced your Woodcutting")) {
            wcLvl = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
            if (wcLvl < 15) {
                treeName = "Tree";
            } else if (wcLvl < 30) {
                treeName = "Oak";
            } else if (wcLvl < 60) {
                treeName = "Willow";
            }
        }
    }
}

