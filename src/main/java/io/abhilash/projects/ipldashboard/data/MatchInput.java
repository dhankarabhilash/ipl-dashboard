package io.abhilash.projects.ipldashboard.data;

import lombok.Data;

@Data
public class MatchInput {

    private String id;
    private String city;
    private String date;
    private String player_of_match;
    private String venue;
    private String team1;
    private String team2;
    private String toss_winner;
    private String toss_decision;
    private String match_winner;
    private String result;
    private String result_margin;
    private String umpire1;
    private String umpire2;

}
