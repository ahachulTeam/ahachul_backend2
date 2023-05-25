package backend.team.ahachul_backend.common.schedule

import jakarta.annotation.PostConstruct
import org.quartz.*
import org.springframework.context.annotation.Configuration


@Configuration
class ScheduleConfig(
    private val scheduler: Scheduler
){

    @PostConstruct
    fun init() {
        setTriggerListener()
        scheduler.scheduleJob(getJobDetail(), getTrigger())
    }

    private fun setTriggerListener() {
        val listenerManager = scheduler.listenerManager
        listenerManager.addTriggerListener(JobFailureHandlingListener())
    }

    private fun getJobDetail(): JobDetail {
        return JobBuilder.newJob()
            .ofType(UpdateLostDataJob::class.java)
            .usingJobData(getJobDataMap())
            .withIdentity(JobKey("UPDATE_LOST_DATA_JOB", "LOST"))
            .build()
    }

    private fun getJobDataMap(): JobDataMap {
        val jobDataMap = JobDataMap()
        jobDataMap.put(UpdateLostDataJob.RETRY_KEY, 0)
        return jobDataMap
    }

    private fun getTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .withIdentity(TriggerKey("UPDATE_LOST_DATA_TRIGGER ", "LOST"))
            .startNow()
            .withSchedule(
                CronScheduleBuilder.cronSchedule("0 * * ? * *")
                    .withMisfireHandlingInstructionFireAndProceed()
            )
            .build()
    }
}