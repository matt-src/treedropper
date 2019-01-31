package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Players;

import java.util.concurrent.Callable;

public class Chop extends Task<ClientContext> {
    String treeName = "";
    public Chop(ClientContext ctx, String treeName) {
        super(ctx);
        this.treeName = treeName;
    }

    @Override
    public boolean activate() {
        String treeName = tofuFuncs.Tools.getTreeName(ctx);
        return !ctx.inventory.isFull()
                && !ctx.objects.select().name(treeName).isEmpty()
                && ctx.players.local().animation() == -1;
    }

    @Override
    public void execute() {
        GameObject tree = ctx.objects.nearest().poll();
        //if(tree.tile().matrix(ctx).reachable()) {
            if (tree.inViewport()) {
                    tree.interact("Chop");
                    long last = System.currentTimeMillis();
                    Condition.sleep(Random.nextGaussian(500, 1500, 1000, 100)); //wait a bit to allow our click to register
                    Condition.wait(() -> (ctx.players.local().animation() == -1), 50, 15); //wait a bit to allow our click to register

            } else {
                if(reachable(tree)) {
                    ctx.movement.step(tree);
                    Condition.sleep(Random.nextGaussian(500, 1500, 1000, 100)); //wait a bit to allow our click to register
                    Condition.wait(() -> (ctx.players.local().animation() == -1), 50, 15); //wait a bit to allow our click to register
                }
            }
        //}
    }

    public boolean reachable(GameObject object) { //From coma
        final Tile t = object.tile();
        final Tile[] tiles = {t.derive(-1, 0), t.derive(1, 0), t.derive(0, -1), t.derive(0, 1)};
        for (Tile tile : tiles) {
            if (tile.matrix(ctx).reachable()) {
                return true;
            }
        }
        return false;
    }
}
