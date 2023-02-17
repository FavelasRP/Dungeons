package me.elixirrpg.net.dungeons.extras;


import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_19_R1.EntityArmorStand;
import net.minecraft.server.v1_19_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_19_R1.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FloatingText {

    private EntityArmorStand entity;
    private String text;
    private List<String> lines;

    public FloatingText(Location loc, String text) {
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        this.text = text;
        this.lines = new ArrayList<String>();

        // Split the text into multiple lines based on newline characters
        String[] splitText = text.split("\n");

        for (String line : splitText) {
            this.lines.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        // Create an armor stand entity
        this.entity = new EntityArmorStand(world, loc.getX(), loc.getY(), loc.getZ());

        // Set the armor stand as invisible and invulnerable
        this.entity.setInvisible(true);
        this.entity.setInvulnerable(true);
        this.entity.setNoGravity(true);

        // Set the custom name visible to players
        this.entity.setCustomNameVisible(true);

        // Set the custom name of the armor stand to the first line of the text
        this.entity.setCustomName(lines.get(0));

        // Spawn the armor stand in the world
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.entity);
        for (Player player : loc.getWorld().getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void show(Player player) {
        // Send a packet to the player to show the armor stand
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.entity);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void hide(Player player) {
        // Send a packet to the player to hide the armor stand
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(this.entity.getId()));
    }

    public void update() {
        // Update the custom name of the armor stand to the current text
        this.entity.setCustomName(this.lines.get(0));

        // If there is more than one line, update the custom name with the remaining lines as well
        if (this.lines.size() > 1) {
            for (int i = 1; i < this.lines.size(); i++) {
                this.entity.setCustomName(this.entity.getCustomName() + "\n" + this.lines.get(i));
            }
        }

        // Send a packet to all players to update the armor stand
        for (Player player : this.entity.world.getWorld().getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.entity.getId(), this.entity.getDataWatcher(), true));
        }
    }

    public void setText(String text) {
        this.text = text;
        this.lines
