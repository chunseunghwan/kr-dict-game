@echo off
chcp 65001 >nul
java -cp out\production\kr-dict-game_1 kr.dict.game.WordChainCli src\kr\dict\data\kr_korean.csv
pause
