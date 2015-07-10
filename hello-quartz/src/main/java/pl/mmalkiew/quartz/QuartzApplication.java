package pl.mmalkiew.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Sample application uses quartz library features.
 * 
 * @author mmalkiew
 *
 */
public class QuartzApplication {
	
	/**
	 * The followin code obtains an instance of the scheduler,
	 * start it, then shuts it down.
	 * 
	 * @param main
	 */
	public static void main(String[] main) {
		Scheduler scheduler = null;
		try {
			
			/*
			 * Grab the scheduler instance from the factory
			 */
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			
			//	start the scheduler
			scheduler.start();
			
			// define the job and tie it to our HelloJob class
			JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("myFirstJob", "sampleGroup").build();

			// Trigger the job to run now, and then repeat every 40 seconds
			Trigger trigger = TriggerBuilder.newTrigger()
			    .withIdentity("trigger1", "group1")
			    .startNow().withSchedule(SimpleScheduleBuilder
			    		.simpleSchedule()
			    		.withIntervalInSeconds(1)
			    		.repeatForever())
			    		.build();

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(job, trigger);
			
			//	Main thread go to sleep for 10 seconds... meanwhile quartz is working
			Thread.sleep(10000);
			scheduler.shutdown();
		}
		catch (SchedulerException  e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
