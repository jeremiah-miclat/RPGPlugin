package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AbyssAutoConverter {

    private static final int ATTRIBUTE_CAP = 20000;

    /**
     * Rewards abyss points and auto-converts into attributes.
     *
     * @return true if the profile was modified (needs saving)
     */
    public static boolean rewardAndConvert(Player player, UserProfile profile, int abyssGained) {
        if (abyssGained <= 0) return false;

        boolean changed = false;

        // 1. Add abyss points
        profile.setAbysspoints(profile.getAbysspoints() + abyssGained);
//        player.sendMessage(ChatColor.AQUA +
//                "You gained " + abyssGained + " Abyss Points!");
        changed = true;

        // 2. Auto convert
        double totalAttrib =
                profile.getTotalAllocatedPoints() + profile.getCurrentAttributePoints();

        int abyssAvailable = (int) profile.getAbysspoints();
        int converted = 0;

        while (totalAttrib < ATTRIBUTE_CAP) {
            int cost = getLevelsRequiredForConversion(totalAttrib);
            if (abyssAvailable < cost) break;

            abyssAvailable -= cost;
            totalAttrib++;
            converted++;
        }

        if (converted > 0) {
            profile.setCurrentAttributePoints(
                    profile.getCurrentAttributePoints() + converted
            );
            profile.setAbysspoints(abyssAvailable);

            player.sendMessage(ChatColor.GREEN +
                    "Your power grows! +" + converted + " Attribute Point(s).");
        }

        return changed;
    }

    /**
     * Scaling curve
     */
    public static int getLevelsRequiredForConversion(double totalAttrib) {
        if (totalAttrib < 200) return 400;
        else if (totalAttrib < 400) return 800;
        else if (totalAttrib < 600) return 1200;
        else if (totalAttrib < 800) return 1600;
        else if (totalAttrib < 1000) return 2000;
        else if (totalAttrib < 1200) return 4000;
        else if (totalAttrib < 1400) return 8000;
        else if (totalAttrib < 1600) return 12000;
        else if (totalAttrib < 1800) return 16000;
        else if (totalAttrib < 2000) return 20000;
        else if (totalAttrib < 4000) return 40000;
        else if (totalAttrib < 6000) return 60000;
        else if (totalAttrib < 8000) return 80000;
        else if (totalAttrib < 10000) return 100000;
        else if (totalAttrib < 11000) return 200000;
        else if (totalAttrib < 12000) return 300000;
        else if (totalAttrib < 13000) return 400000;
        else if (totalAttrib < 14000) return 500000;
        else if (totalAttrib < 15000) return 600000;
        else if (totalAttrib < 16000) return 700000;
        else if (totalAttrib < 17000) return 800000;
        else if (totalAttrib < 18000) return 900000;
        else if (totalAttrib < 19000) return 1000000;
        else return 1100000;
    }
}
