package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import messages.randomMessages;
import muted.MutedPlayers;
import tpa.tpaData;
import tpa.tpaHereData;

final class LoginListener implements org.bukkit.event.Listener {
    
	  public static HashMap<Player, String> LastMessage;
	  @EventHandler(priority = EventPriority.HIGHEST)
	  public void onChat(AsyncPlayerChatEvent event) {
	      
		  Player player = event.getPlayer();
	      if(AtlanticCraft.IsPlayerMuted(player.getDisplayName()))
	      {
	    	  event.setCancelled(true);
	    	  player.sendMessage("<"+player.getDisplayName()+"> "+event.getMessage()+"");
	      }
	      
	      if(LastMessage.get(player)==null) LastMessage.put(player, "");
	      if(LastMessage.get(player).equals(event.getMessage()))
	      {
	    	  event.setCancelled(true);
	    	  player.sendMessage(ChatColor.RED + "Don't repeat yourself");
	      }
	      LastMessage.put(player, event.getMessage());
	      int uppercase = 0;
	      for(int i=0; i<event.getMessage().length(); i++)
	    	  if(Character.isUpperCase(event.getMessage().charAt(i))) uppercase++;
	      
	      if(uppercase>4)
	      {
	    	  event.setCancelled(true);
	    	  Bukkit.broadcastMessage("<"+player.getDisplayName()+"> "+event.getMessage().toLowerCase()+"");
	      }
	          
	  }
}
public class AtlanticCraft
  extends JavaPlugin
  {
  public BukkitTask task;
  int timer;
  public static int index=0;
  public void onEnable()
  {
	 getServer().getPluginManager().registerEvents(new LoginListener(), this);
	 getConfig().options().copyDefaults(true);
	 getConfig().options().copyHeader(true);
	 saveDefaultConfig();
	 this.timer = 600;
	 this.repeatingTask();
	 LoginListener.LastMessage = new HashMap<Player, String>();
	 index=0;
	 
  }
  public static List<MutedPlayers> MutedList = new ArrayList<MutedPlayers>();
  public static List<tpaData> tpaList = new ArrayList<tpaData>();
  public static List<tpaHereData> tpaHereList = new ArrayList<tpaHereData>();
  
  public void repeatingTask() {
      this.task = new BukkitRunnable() {
          public void run() {
        	  Bukkit.broadcastMessage(randomMessages.messages[index]);
        	  index++;
        	  if(index == randomMessages.messages.length)
        		  index=0;
          }
      }.runTaskTimer((Plugin)this, (long)(20 * this.timer), (long)(20 * this.timer));
  }
  public static void MutePlayer(String playername)
  {
	  if(!IsPlayerMuted(playername))
	  {
		  MutedPlayers muted = new MutedPlayers(playername);
		  MutedList.add(muted);  
	  }
  }
  public static boolean IsPlayerMuted(String name)
  {
	  boolean toReturn=false;
	  for(MutedPlayers mutedPlayers : MutedList)
	  {
		  if(mutedPlayers.getName().equals(name))
		  {
			  toReturn = true;
		  }
	  }
	  return toReturn;
  }
  public boolean IsPlayerInvitedByPlayer_tphere(Player p1, Player p2)
  {
	  for(tpaHereData tpaHereData : tpaHereList)
	  {
		  if(((Player)p1).getDisplayName().equals(((Player)tpaHereData.getMyself()).getDisplayName()) && ((Player)p2).getDisplayName().equals(((Player)tpaHereData.getTargetparty()).getDisplayName()))
		  {
			  return true;
		  }
	  }
	  return false;
  }
  
  public boolean IsPlayerInvitedByPlayer_tpa(Player p1, Player p2)
  {
	  for(tpaData tpaData : tpaList)
	  {
		  if(((Player)p1).getDisplayName().equals(((Player)tpaData.getMyself()).getDisplayName()) && ((Player)p2).getDisplayName().equals(((Player)tpaData.getTargetparty()).getDisplayName()))
		  {
			  return true;
		  }
	  }
	  return false;
  }
  
  
  public void DeletetpaRequest(Player p1, Player p2)
  {
	  tpaList.removeIf(tpaData -> ((Player)tpaData.getMyself()).getDisplayName().equals(((Player)p1).getDisplayName()) && ((Player)tpaData.getTargetparty()).getDisplayName().equals(((Player)p2).getDisplayName()));
  }
  public void DeletetpahereRequest(Player p1, Player p2)
  {
	  tpaHereList.removeIf(tpaHereData -> ((Player)tpaHereData.getMyself()).getDisplayName().equals(((Player)p1).getDisplayName()) && ((Player)tpaHereData.getTargetparty()).getDisplayName().equals(((Player)p2).getDisplayName()));
  }
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("TpaFromConsoleError")));
      return true;
    }
    final Player me = (Player)sender;
    if (cmd.getName().equalsIgnoreCase("vote"))
    {
    	me.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
    	me.sendMessage(ChatColor.BOLD.BOLD + "" + ChatColor.AQUA +"AtlanticCraft» "+ChatColor.WHITE+" Vote for free diamonds and food!! Available vote links: https://www.atlanticcraft.net/vote!");
    	me.sendMessage(ChatColor.BOLD.BOLD + "" + ChatColor.AQUA  +"Monthly top voter "+ChatColor.AQUA+"wins one of the following rewards: ");
    	me.sendMessage(ChatColor.BOLD.AQUA + "- Steam gift key\n- Paypal rewards\n- Top Spawner\n- Crystallions (rare tokens)\n- Discord Nitro\n- LordZ Rank");
    	me.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
    }
    if (cmd.getName().equalsIgnoreCase("ask"))
    {
    	if (args.length == 0)
        {
          me.sendMessage(ChatColor.RED + "Syntax: /ask <message>");
          return true;
        }
    	String request = "";
    	for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                request = String.valueOf(request) + " " + args[i];
            }
        }
    	me.sendMessage(ChatColor.RED+"[Ask]"+ChatColor.GREEN+" Your request has successfully been sent!");
    	Bukkit.broadcast(""+ChatColor.RED+"[OpHelp from "+me.getDisplayName()+"]:"+ChatColor.GREEN+" "+request+"", "staffchat.send");
    }
    if (cmd.getName().equalsIgnoreCase("softmute1")) {
    	
    	if (args.length == 0)
        {
          me.sendMessage(ChatColor.RED + "Invalid player specified");
          return true;
        }
    	Player target = Bukkit.getServer().getPlayer(args[0]);
    	if (target == null)
        {
    		me.sendMessage(ChatColor.RED + "Invalid player specified");
    		return true;
        }
    	if(!IsPlayerMuted(target.getDisplayName()))
    	{
    		MutedPlayers muted = new MutedPlayers(target.getDisplayName());
    		MutedList.add(muted);
    		me.sendMessage(ChatColor.GREEN +"Player has been soft-muted1");
    	}
    	else
    	{
    		if(MutedList != null) {
    		MutedList.removeIf(Muted -> Muted.getName().equals(target.getDisplayName()));
    		me.sendMessage(ChatColor.GREEN + "Player has been soft-unmuted1");
    		}
    	}
        return true;
    }
    if (cmd.getName().equalsIgnoreCase("tphere"))
    {
    	
        if(args.length == 0)
        {
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SpecifyPlayerError")));
          return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null)
        {
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerDoesNotExistError")));
          return true;
        }
        if(IsPlayerInvitedByPlayer_tphere(me, target))
        {
        	me.sendMessage(ChatColor.RED + "You have already requested a tphere from that player!");
            return true;
        }
        if(IsPlayerInvitedByPlayer_tpa(me, target))
        {
        	me.sendMessage(ChatColor.RED + "You have already requested a tpa from that player!");
            return true;
        }
        if (me.getWorld() == target.getWorld())
        {
          tpaHereData tpaHereData = new tpaHereData(me, target);
          tpaHereList.add(tpaHereData);
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestSent").replaceAll("/target/", target.getDisplayName())));
          target.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
          target.sendMessage(" ");
          target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Line11").replaceAll("/sender/", me.getDisplayName())));
          target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Line22")));
          target.sendMessage(" ");
          target.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
          return true;
        }
        else
        {
	        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerInAnotherWorldError")));
	        return true;
        }
    }
    if (cmd.getName().equalsIgnoreCase("tpa"))
    {

        if (args.length == 0)
        {
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SpecifyPlayerError")));
          return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null)
        {
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerDoesNotExistError")));
          return true;
        }
        if(IsPlayerInvitedByPlayer_tpa(me, target))
        {
        	me.sendMessage(ChatColor.RED + "You have already requested a tphere from that player!");
            return true;
        }
        if(IsPlayerInvitedByPlayer_tphere(me, target))
        {
        	me.sendMessage(ChatColor.RED + "You have already requested a tpa from that player!");
            return true;
        }
        if (me.getWorld() == target.getWorld())
        {
          tpaData tpaData = new tpaData(me, target);
          tpaList.add(tpaData);
          me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestSent").replaceAll("/target/", target.getDisplayName())));
          target.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
          target.sendMessage(" ");
          target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Line1").replaceAll("/sender/", me.getDisplayName())));
          target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Line2")));
          target.sendMessage(" ");
          target.sendMessage(ChatColor.BOLD.GOLD+ "_____________________________________________");
          return true;
        }else {
        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerInAnotherWorldError")));
        return true;
        }
    }
    if (cmd.getName().equalsIgnoreCase("tpaccept"))
    {
    	
		if (args.length == 0)
		{
		  me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SpecifyPlayerError")));
		  return true;
		}
		Player target = Bukkit.getServer().getPlayer(args[0]);
		if (target == null)
		{
		  me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerDoesNotExistError")));
		  return true;
		}
	  if (!IsPlayerInvitedByPlayer_tphere(target, me) && !IsPlayerInvitedByPlayer_tpa(target, me))
	  {
	    me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoRequestToAccept")));
	    return true;
	  }
	  if(IsPlayerInvitedByPlayer_tpa(target, me))
	  {
		System.out.println("[tpa] Teleported "+target.getDisplayName()+" to "+me.getDisplayName()+"");
		((Player)target).teleport(me);
	    me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestAcceptedTeleport").replaceAll("/sender/", ((Player)target).getDisplayName())));
	    ((Player)target).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestAccepted").replaceAll("/target/", me.getDisplayName())));
	    
	    DeletetpaRequest(target, me);
	    return true;
	  }
	  else if (IsPlayerInvitedByPlayer_tphere(target, me))
      {
		System.out.println("[tpahere] Teleported "+me.getDisplayName()+" to "+target.getDisplayName()+"");
        me.teleport((Player)target);
        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestAcceptedTeleport1").replaceAll("/sender/", ((Player)target).getDisplayName())));
        ((Player)target).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestAccepted").replaceAll("/target/", me.getDisplayName())));
        DeletetpahereRequest(target, me);
        return true;
      }
      return true;
    }
    if (cmd.getName().equalsIgnoreCase("tpdeny"))
    {
    	
    	if (args.length == 0)
		{
		  me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("SpecifyPlayerError")));
		  return true;
		}
		Player target = Bukkit.getServer().getPlayer(args[0]);
		if (target == null)
		{
		  me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PlayerDoesNotExistError")));
		  return true;
		}
		
      if (!IsPlayerInvitedByPlayer_tphere(target, me) && !IsPlayerInvitedByPlayer_tpa(target, me))
      {
        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoRequestToDeny")));
        return true;
      }
      if (IsPlayerInvitedByPlayer_tpa(target, me))
      {
        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestDenied")));
        ((Player)target).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestDeniedTeleport").replaceAll("/target/", me.getDisplayName())));

        DeletetpaRequest(target, me);
        return true;
      }
      else if (IsPlayerInvitedByPlayer_tphere(target, me))
      {
        me.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestDenied")));
        ((Player)target).sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("RequestDeniedTeleport").replaceAll("/target/", me.getDisplayName())));
        DeletetpahereRequest(target, me);
        return true;
      }
      return true;
    }
    return true;
  }
}
