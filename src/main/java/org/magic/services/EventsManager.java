package org.magic.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.ListUtils;
import org.magic.api.beans.MagicEvent;
import org.magic.api.beans.Party;
import org.magic.game.model.Player;
import org.magic.tools.FileTools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EventsManager {
	
	private List<MagicEvent> events;
	
	public EventsManager() {
		events = new ArrayList<>();
	}
	
	public void addEvent(MagicEvent e)
	{
		events.add(e);
	}
	
	
	public void saveEvents() throws IOException
	{
		FileTools.saveFile(MTGConstants.MTG_EVENTS_FILE, new Gson().toJson(events));
	}
	
	public void load() throws IOException
	{
		if(MTGConstants.MTG_EVENTS_FILE.exists())
			events  = new Gson().fromJson(FileTools.readFile(MTGConstants.MTG_EVENTS_FILE), new TypeToken<List<MagicEvent>>() {}.getType());
	}
	
	public List<MagicEvent> getEvents() {
		return events;
	}
	
	public void start(MagicEvent e)
	{
		
		Timer t = new Timer(e.getTitle(), true);
		Collections.shuffle(e.getPlayers());
		
		
		List<List<Player>> parties = ListUtils.partition(e.getPlayers(), 2);
		
		
		for(List<Player> p : parties)
		{
			e.getParties().add(new Party(p.get(0), p.get(1), e.getRounds(),true));
		}
		
		
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				e.setRoundTime(e.getRoundTime()-1);
				
				if(e.getRoundTime()<=0)
					this.cancel();
				
			}
		}, 0,60000);
		
	}
	
	
}
