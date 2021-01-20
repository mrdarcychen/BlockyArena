/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.games.MatchRules;

/**
 * A TeamMode represents the mode of a Team based on the number of gamers on a Team.
 */
public class TeamMode implements MatchRules {

    private static final TeamMode ONE_VERSUS_ONE = new TeamMode(1, 2);
    private static final TeamMode TWO_VERSUS_TWO = new TeamMode(2, 2);
    private final int teamSize; // the capacity of each team in this mode
    private final int teamCount;

    /**
     * Create a team mode with uniform configuration.
     *
     * @param teamSize the number of players on each team
     * @param teamCount the number of teams
     */
    public TeamMode(int teamSize, int teamCount) {
        assert teamSize >= 1;
        assert teamCount >= 2;
        this.teamSize = teamSize;
        this.teamCount = teamCount;
    }

    @Override
    public int getTotalCapacity() {
        return teamSize * teamCount;
    }

    @Override
    public int getTeamCount() {
        return teamCount;
    }

    public String toString() {
        if (teamSize == 1 && teamCount == 2) {
            return "1v1";
        }
        if (teamSize == 2 && teamCount == 2) {
            return "2v2";
        }
        return "ffa";
    }

    public static TeamMode parse(String mode, int startPointCount) {
        switch (mode) {
            case "1v1":
            case "solo":
                return ONE_VERSUS_ONE;
            case "2v2":
                return TWO_VERSUS_TWO;
            case "ffa":
            default:
                return new TeamMode(1, startPointCount);
        }
    }
}
