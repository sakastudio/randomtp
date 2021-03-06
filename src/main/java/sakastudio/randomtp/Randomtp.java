package sakastudio.randomtp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public final class Randomtp extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        randomtp = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(this, this);
        radius = 500;
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    int radius = 500;
    //プレイヤーのメッセージを格納
    HashMap<String, String> PlayerMessage = new HashMap<String, String>();
    //未発見プレイヤーを格納
    public HashMap<String, PlayerData> UndiscoveryPlayer = new HashMap<String, PlayerData>();
    //初期探索者
    List<String> InitSearcher = new ArrayList<String>();

    //to-do　手動でプレイヤーを開放するコマンド
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(cmd.getName().equalsIgnoreCase("exerandomTp")){
            Player p = (Player)sender;
            Random r = new Random();
            double playerX = p.getLocation().getX();
            double playerZ = p.getLocation().getZ();

            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            for (Player player : players) {
                boolean issercher = false;
                for (String serchername: InitSearcher) {
                    if(serchername.equals(player.getName())){
                        issercher = true;
                        break;
                    }
                }
                if(issercher){continue;}

                double x = r.nextInt(radius * 2) - radius + playerX;
                double z = r.nextInt(radius * 2) - radius + playerZ;

                Location location = new Location(p.getWorld(), x,255,z);
                player.teleport(location);
                UndiscoveryPlayer.put(player.getName(),new PlayerData(player,x,z));
            }
            sender.sendMessage("ランダムTPを実行しました");

            return true;
        }
        if(cmd.getName().equalsIgnoreCase("setRadius")){
            if (args.length != 1){sender.sendMessage("/setRadius <TPの半径>");return false;}

            radius = Integer.parseInt(args[0]);
            sender.sendMessage("半径を" + args[0] + "に設定しました");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("setsearcher")){
            if (args.length != 1){sender.sendMessage("/setsearcher <プレイヤー名>");return false;}
            InitSearcher.add(args[0]);
            sender.sendMessage(args[0]+"を初期探索者として登録しました");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("discovery")){
            if (args.length != 1){sender.sendMessage("/discovery <プレイヤー名>");return false;}
            if(UndiscoveryPlayer.containsKey(args[0])){
                sender.sendMessage(args[0]+"を未発見リストから削除しました");
                UndiscoveryPlayer.remove(args[0]);
            }else{
                sender.sendMessage(args[0]+"は既に発見されたか、存在しません");
            }
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("removesercher")){
            if (args.length != 1){sender.sendMessage("/removesercher <プレイヤー名>");return false;}
            int index = InitSearcher.indexOf(args[0]);
            if(index == -1){
                sender.sendMessage(args[0]+"は初期探索者に登録されてません");
            }else{
                InitSearcher.remove(index);
                sender.sendMessage(args[0]+"を初期探索者から削除しました");
            }
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("setMessage")){
            if (args.length != 1){sender.sendMessage("/setMessage <メッセージ>");return false;}

            PlayerMessage.put(sender.getName(),args[0]);
            sender.sendMessage("メッセージを設定：" + args[0]);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("getMessage")){
            if (args.length != 1){sender.sendMessage("/getMessage <プレイヤー名>");return false;}

            if (PlayerMessage.containsKey(args[0])){
                sender.sendMessage(args[0]+":" + PlayerMessage.get(args[0]));
            }else{
                sender.sendMessage(args[0]+"はメッセージを送信していません");
            }
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("showlist")){
            sender.sendMessage("未発見プレイヤーリスト");
            for(String val : UndiscoveryPlayer.keySet()){
                sender.sendMessage(val);
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if(entity instanceof Player){
            Player target = (Player) entity;
            UndiscoveryPlayer.remove(target.getName());
            getServer().dispatchCommand(getServer().getConsoleSender(),"tellraw @a {\"text\":\""+target.getName()+"が発見されました！\",\"bold\":true}");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(randomtp.UndiscoveryPlayer.containsKey(e.getPlayer().getName())){
            if(isMoved(e)) e.setCancelled(true);
        }
    }

    private boolean isMoved(PlayerMoveEvent e) {
        Location from=e.getFrom();
        Location to=e.getTo();
        int fromXPos = from.getBlockX();
        int fromZPos = from.getBlockZ();
        int toXPos = to.getBlockX();
        int toZPos = to.getBlockZ();
        return !(fromXPos==toXPos && fromZPos==toZPos);
    }

    static Randomtp randomtp;
    public static Randomtp Instanse(){
        return  randomtp;
    }
}
