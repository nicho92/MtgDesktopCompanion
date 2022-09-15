package org.magic.services.jobs;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.Announce.STATUS;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.logging.MTGLogger;
import org.magic.tools.MTG;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AnnouncesUpdaterJob implements Job {
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			var list = MTG.getEnabledPlugin(MTGDao.class).listAnnounces();
			
			list.stream().filter(a->a.getEndDate().before(new Date()) && a.getStatus()==STATUS.ACTIVE).toList().forEach(a->{
				logger.debug("Found {} is expired at {}",a,a.getEndDate());
				a.setStatus(STATUS.EXPIRED);
				try {
					MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
				} catch (SQLException e) {
					logger.error("can't update {}",a,e);
				}
			});
			
			list.stream().filter(a->a.getStartDate().before(new Date()) && a.getStatus()==STATUS.SOON).toList().forEach(a->{
				logger.debug("Found {}  is now online since {}",a, a.getStartDate());
				a.setStatus(STATUS.ACTIVE);
				try {
					MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateAnnounce(a);
				} catch (SQLException e) {
					logger.error("can't update {}",a,e);
				}
				
			});
			
		} catch (SQLException e) {
			logger.error(e);
		}

	}

}
