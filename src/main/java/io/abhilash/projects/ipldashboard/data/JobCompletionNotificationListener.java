package io.abhilash.projects.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.abhilash.projects.ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager entityManager;

    @Autowired
    public JobCompletionNotificationListener(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Map<String, Team> teamData = new HashMap<>();

            entityManager
                     .createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
                     .getResultList()
                     .stream()
                     .map(result -> new Team((String) result[0], (long) result[1]))
                     .forEach(team -> teamData.put(team.getTeamName(), team));

            entityManager
                    .createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
                    .getResultList()
                    .forEach(result -> {
//                        Team team = teamData.get((String) result[0]);
//                        team.setTotalMatches(team.getTotalMatches() + (long) result[1]);
                        String teamName = (String) result[0];
                        long totalMatches = (long) result[1];
                        Team team;
                        if (!teamData.containsKey(teamName)) {
                            team = new Team(teamName, totalMatches);
                            teamData.put(teamName, team);
                        } else {
                            team = teamData.get(teamName);
                            team.setTotalMatches(team.getTotalMatches() + totalMatches);
                        }
                    });

            entityManager
                    .createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                    .getResultList()
                    .forEach(result -> {
                        Team team = teamData.get((String) result[0]);
                        if(Objects.nonNull(team)) team.setTotalWins((long) result[1]);
                    });

            teamData.values().forEach(entityManager::persist);
            teamData.values().forEach(System.out::println);

        }
    }
}
