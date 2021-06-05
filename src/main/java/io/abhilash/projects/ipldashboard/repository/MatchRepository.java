package io.abhilash.projects.ipldashboard.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.abhilash.projects.ipldashboard.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable);

    @Query(
            "select m from Match m where (m.team1 = :teamName or m.team2 = :teamName) and (m.date between :startDate and :endDate)"
    )
    List<Match> getMatchesByTeamNameBetweenDates(
            @Param("teamName") String teamName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate
    );

//    List<Match> getByTeam1OrTeam2AndDateBetweenOrderByDateDesc(String team1, String team2, LocalDate date1, LocalDate date2);

    default List<Match> findLatestMatchesByTeam(String teamName, int matchCount) {
        return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, matchCount));
    }
}
