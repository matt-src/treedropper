package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

public class Drop extends Task<ClientContext>{


    public Drop(ClientContext ctx){
        super(ctx);
    }
    String[] logNames = {"Logs", "Oak logs", "Willow logs"};
    @Override
    public boolean activate() {
        //System.out.println(ctx.inventory.isFull());
        ctx.inventory.select();
        return ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        ctx.inventory.select().name(logNames).each(new Filter<Item>() {

            @Override
            public boolean accept(Item item) {
                if( item.interact("Drop") ){
                    Condition.sleep(Random.nextGaussian(0, 1500, 100, 20));
                    return true;
                } else {
                    return false;
                }
            }

        });
    }
}