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
import org.magic.api.exports.impl.JsonExport;
import org.magic.game.model.Player;
import org.magic.tools.FileTools;

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
		
		int max = events.stream().mapToInt(MagicEvent::getId).max().getAsInt();
		
		for(MagicEvent e : events)
		{
			if(e.getId()==0)
			{
				e.setId(max+1);
				max++;
			}
		}
		
		FileTools.saveFile(MTGConstants.MTG_EVENTS_FILE, new JsonExport().toJson(events));
	}
	
	public void load() throws IOException
	{
		if(MTGConstants.MTG_EVENTS_FILE.exists())
			events  = new JsonExport().fromJsonList(FileTools.readFile(MTGConstants.MTG_EVENTS_FILE), MagicEvent.class);
	}
	
	public List<MagicEvent> getEvents() {
		return events;
	}
	
	public MagicEvent getEventById(int id)
	{
		return getEvents().stream().filter(e->e.getId()==id).findAny().orElse(null);
	}
	
	
	public void start(MagicEvent e)
	{
		
		Timer t = new Timer(e.getTitle(), true);
		Collections.shuffle(e.getPlayers());
		
		
		Player byes = null;
		List<List<Player>> parties;
		
		if(e.getPlayers().size()% 2 != 0) {
			byes= e.getPlayers().get(e.getPlayers().size()-1);
			parties = ListUtils.partition(e.getPlayers().subList(0, e.getPlayers().size()-1), 2);
			
		}
		else
		{
			parties = ListUtils.partition(e.getPlayers(), 2);
		}
		
		
		for(List<Player> p : parties)
		{
			e.getParties().add(new Party(p.get(0), p.get(1), e.getRounds(),true));
		}
		
		if(byes!=null)
		{
			Party p = new Party(byes, null, 1, false);
			p.getRounds().get(0).getScore().put(byes, 1);
			e.getParties().add(p);
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
