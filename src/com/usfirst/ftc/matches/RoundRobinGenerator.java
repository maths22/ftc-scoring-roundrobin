package com.usfirst.ftc.matches;

import com.usfirst.ftc.logging.FTCLogger;
import com.usfirst.ftc.matches.Alliance;
import com.usfirst.ftc.matches.Match;
import com.usfirst.ftc.matches.MatchGenerationDialog;
import com.usfirst.ftc.teams.Team;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by jburroughs on 6/13/17.
 */
public class RoundRobinGenerator {

    private final Logger logger = FTCLogger.getInstance();
    public static void main(String args[]) {

        int count = 7;

        int[] teamNums = {3507,7738,8817,10091,10138,11848,10635,5037};
        ArrayList<Team> teams = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Team team = new Team();
            team.setNumber(teamNums[i]);
            teams.add(team);
        }

        RoundRobinGenerator generator = new RoundRobinGenerator();
        String scheduleString = generator.generateRoundRobinSchedule(teams, 16).stream()
                .map((m) -> m.getNumber() + " " + m.getRedTeam(0).toString() + (m.getRedSurrogate(0)?'*':' ') + " " + m.getBlueTeam(0).toString() + (m.getBlueSurrogate(0)?'*':' ')).collect(Collectors.joining("\n"));


        System.out.println(scheduleString);
    }

    public List<Match> generateRoundRobinSchedule(List<Team> teams, int maxMatchCount) {
        logger.logp(Level.INFO, "RoundRobinGenerator", "generateRoundRobinSchedule", "Generating at most " + maxMatchCount + " matches for " + teams.size() + " teams");

        int roundSize = teams.size() / 2;
        int roundCount = maxMatchCount / roundSize;

        List<List<Match>> matches = listMatches(teams, roundCount);

        //Surrogates are only an issue for odd numbers of teams
        if(teams.size() % 2 == 1) {
            List<Team> byeTeams = matches.stream().flatMap(Collection::stream).filter((m) -> {
                Team redTeam = m.getRedTeam(0);
                Team blueTeam = m.getBlueTeam(0);
                return redTeam.getName().equals("⚠") || blueTeam.getName().equals("⚠");
            }).map((m) -> {
                Team redTeam = m.getRedTeam(0);
                Team blueTeam = m.getBlueTeam(0);
                return redTeam.getName().equals("⚠") ? blueTeam : redTeam;
            }).collect(Collectors.toList());

            int surrogateRound = Math.min(3, matches.size());

            matches.get(surrogateRound - 1).forEach((m) -> {
                Team redTeam = m.getRedTeam(0);
                Team blueTeam = m.getBlueTeam(0);
                if (!byeTeams.contains(redTeam)) {
                    m.setRedSurrogate(true, 0);
                }
                if (!byeTeams.contains(blueTeam)) {
                    m.setBlueSurrogate(true, 0);
                }
            });
        }

        AtomicInteger index = new AtomicInteger();


        return matches.stream().flatMap(Collection::stream).filter((m) -> {
            Team redTeam = m.getRedTeam(0);
            Team blueTeam = m.getBlueTeam(0);
            return !redTeam.getName().equals("⚠") && !blueTeam.getName().equals("⚠");
        }).map((m) -> {
            m.setNumber(index.incrementAndGet());
            return m;
        }).collect(Collectors.toList());
    }

    //Adapted from https://stackoverflow.com/questions/26471421/round-robin-algorithm-implementation-java
    private List<List<Match>> listMatches(List<Team> teamList, int roundCount)
    {
        teamList = new ArrayList<>(teamList);
        Collections.shuffle(teamList);

        if (teamList.size() % 2 != 0)
        {
            Team dummy = new Team();
            dummy.setName("⚠");
            teamList.add(dummy); // If odd number of teams add a dummy
        }

        int teamCount = teamList.size();

        if(roundCount == 0) {
            roundCount = (teamCount - 1); // Days needed to complete tournament
        }
        int halfSize = teamCount / 2;

        List<Team> teams = new ArrayList<>();

        teams.addAll(teamList); // Add teams to List and remove the first team
        teams.remove(0);

        int teamsSize = teams.size();

        List<List<Match>> ret = new ArrayList<>();

        roundCount = Math.min(roundCount, teamsSize);

        for (int round = 0; round < roundCount; round++)
        {
            List<Match> roundList = new ArrayList<>();
            ret.add(roundList);
            int teamIdx = round % teamsSize;

            roundList.add(generateMatch(teamList.get(0), teams.get(teamIdx)));

            for (int idx = 1; idx < halfSize; idx++)
            {
                int firstTeam = (round + idx) % teamsSize;
                int secondTeam = (round  + teamsSize - idx) % teamsSize;
                roundList.add(generateMatch(teams.get(firstTeam), teams.get(secondTeam)));
            }
        }

        return ret;
    }

    private static Match generateMatch(Team redTeam, Team blueTeam) {
        Match match = new Match();
        match.setRedTeam(redTeam,0 );
        match.setBlueTeam(blueTeam, 0);
        return match;
    }
}
