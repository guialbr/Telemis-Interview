# Telemis Interview Java R&D Fullstack engineer
This project consist of the last round interview preparation for Telemis R&D last round interview. It consit of 
- TMRHE (TeleMis Reception High End) software demo [text](demo_TMRHE.md) 

- A Java web application : 

## TMRHE Demo 
Role based interview \ 15 minutes presentation

I should focus on  the mains Pacs features and functionalities + impact values within the hospitals point of view as well as understand the architecture (class diagrams ?) and technologies useed.

I am the Telemis application engineer presenting Telemis's PACS solution. I should keep a high level presentation but understand in a deeper in case of questions.
 
 The interviewer will analyse how deep I can answer and present the software. I should focus on presenting main functionalities as well as understand them in a system based paradigm.



## Web based - Ancient African Bowling Game

This project implements the logic and scoring system of a game inspired by ancient African archaeological discoveries, closely resembling modern bowling. The application will be built with:
- A Java backend, leveraging the Spring Framework and Maven for dependency management and build.

Cahier des charges / the program is designed to:

- Calculate the score for each player based on the specific game rules described below.
- Expose a Java method for submitting each throw:

How many players ?  To be determined or checked at compilation range between 2 to X



Test driven implementation - To check solution and scores

# Games rules and scoring System
### Game setup 
For each players, the goal is to knock down as many pins as possible per frame.

- Each player plays 5 frames
- Each frame consists of 3 throws
- After each frame, all 15 pins are reset.
So 15 throws for each players in the game

### Scoring rules:

1. **Regular** Frame: If not all pins are knocked down after 3 throws, the score is the sum of pins knocked down (max 14 points).

2. **Spare**: All 15 pins are knocked down by the second or third throw.
Score: 15 points + pins from the next **2** throws
Max possible: 45 points (if next two throws are strikes)

3. **Strike** : All 15 pins are knocked down in the first throw.
Score: 15 points + pins from the next **3** throws
Max possible: 60 points (if next two throws are strikes)

4. Bonus Throws: If the player make a strike or spare at the last (5th) frame, extra throws are granted to allow for proper score calculation.

The final frame can contain up to 4 throws depending on bonus eligibility.

Maximum Score : The perfect game score is 300 points.

Commit actuel : 5h


# Architecture of the solution




### Telemis Short presentation and notes
Moto / main values :  Extending Human Life