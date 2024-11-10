package github.eremiyuh.rPGPlugin.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeOffer {
    private final Player sender;
    private final Player recipient;
    private final Material materialToTrade;
    private final int amountToTrade;
    private final Material materialToReceive;
    private final int amountToReceive;

    public TradeOffer(Player sender, Player recipient, Material materialToTrade, int amountToTrade, Material materialToReceive, int amountToReceive) {
        this.sender = sender;
        this.recipient = recipient;
        this.materialToTrade = materialToTrade;
        this.amountToTrade = amountToTrade;
        this.materialToReceive = materialToReceive;
        this.amountToReceive = amountToReceive;
    }

    public Player getSender() {
        return sender;
    }

    public Player getRecipient() {
        return recipient;
    }

    public boolean hasRequiredItems() {
        return hasEnoughItems(sender, materialToTrade, amountToTrade) && hasEnoughItems(recipient, materialToReceive, amountToReceive);
    }

    private boolean hasEnoughItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
                if (count >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    public void executeTrade() {
        removeItems(sender, materialToTrade, amountToTrade);
        removeItems(recipient, materialToReceive, amountToReceive);

        sender.getInventory().addItem(new ItemStack(materialToReceive, amountToReceive));
        recipient.getInventory().addItem(new ItemStack(materialToTrade, amountToTrade));
    }

    private void removeItems(Player player, Material material, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                    break;
                } else {
                    amount -= item.getAmount();
                    player.getInventory().remove(item);
                }
            }
        }
    }
}
