package io.abhilash.projects.ipldashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import io.abhilash.projects.ipldashboard.model.Match;
import io.abhilash.projects.ipldashboard.model.Team;
import io.abhilash.projects.ipldashboard.repository.MatchRepository;
import io.abhilash.projects.ipldashboard.repository.TeamRepository;

@RestController
@CrossOrigin
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/teams/{teamName}")
    public Team getTeam(@PathVariable String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        team.setMatches(matchRepository.findLatestMatchesByTeam(teamName, 4));
        return team;
    }

    @GetMapping("/teams/{teamName}/matches")
    public List<Match> getMatchesForTeam(@PathVariable String teamName, @RequestParam int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year+1, 1, 1);
        return matchRepository.getMatchesByTeamNameBetweenDates(teamName, startDate, endDate);
    }

    @GetMapping("/teams")
    public Iterable<Team> getTeam() {
        return teamRepository.findAll();
    }

}
