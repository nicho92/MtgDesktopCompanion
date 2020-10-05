package org.magic.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.magic.api.beans.MagicEvent;
import org.magic.tools.FileTools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EventsManager {
	
	private List<MagicEvent> events;
	
	private Map<MagicEvent , Timer> startedEvent;
	
	
	
	public EventsManager() {
		events = new ArrayList<>();
		startedEvent = new HashMap<>();
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
		startedEvent.put(e, new Timer(e.getTitle(), true));
		
		
		startedEvent.get(e).schedule(new TimerTask() {
			
			@Override
			public void run() {
				e.setDuration(e.getDuration()-1);
				System.out.println(e.getDuration());
			}
		}, 0,1000);
		
	}
	
	
}
