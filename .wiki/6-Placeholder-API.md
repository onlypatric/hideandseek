### **PlaceholderAPI**

This plugin tracks player wins, losses, kills, deaths, etc. This is all stored in a database, and can be queried by placeholder api. 

The supported placeholders are:

```
%hs_stats_<stat-name>%
%hs_stats_<stat-name>_<player-name>%
%hs_rank-score_<stat-name>_<place>%
%hs_rank-name_<stat-name>_<place>%
```

The following state names are:

```
total-wins
hider-wins
seeker-wins
total-games
hider-games
seeker-games
total-kills
hider-kills
seeker-kills
total-deaths
hider-deaths
seeker-deaths
```

#### %hs_stats_\<stat-name>%

This gives the current statistic for the current player viewing it.

Example: `%hs_stats_hider-kills` // Shows your kills as hider count

#### %hs_stats_\<stat-name>_\<player-name>%

This gives the current statistic for the player selected in the placeholder

Example: `%hs_stats_total-kills_JohnDoe` // Shows JohnDoe's total kill count

#### %hs_rank-score_\<stat-name>_\<place>%

This shows the current score at the given place in the given ranking of the stat

Example: `%hs_rank-score_seeker-wins_2%` // Shows 2nd place's score in seeker wins

#### %hs_rank-name_\<stat-name>_\<place>%

This shows the current name at the given place in the given ranking of the stat

Example: `%hs_rank-name_seeker-wins_2%` // Shows 2nd place's name in seeker wins