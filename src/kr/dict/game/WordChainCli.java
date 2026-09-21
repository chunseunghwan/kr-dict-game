package kr.dict.game;

import kr.dict.trie.DictionaryLoader;
import kr.dict.trie.Trie;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * 터미널에서 돌아가는 끝말잇기 실행 지점.
 * 실행하면 프로그램 안에서 바로 대결 모드를 고르고, 사전에 없는 단어를 3번 내면 그 사람이 진다.
 * 사용법:
 *   java -cp out kr.dict.game.WordChainCli kr_korean.csv
 */
public class WordChainCli {

    private static final int MAX_MISS = 3;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("사용법: java -cp out kr.dict.game.WordChainCli kr_korean.csv");
            return;
        }

        Trie dictionary = new Trie();
        long t0 = System.currentTimeMillis();
        int count = DictionaryLoader.load(dictionary, args[0]);
        long t1 = System.currentTimeMillis();
        System.err.printf("%d개 단어 로딩 완료 (%.2f초)%n", count, (t1 - t0) / 1000.0);

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        boolean vsAi = chooseMode(sc);

        WordChainGame game = new WordChainGame(dictionary);
        int humanTurn = 1; // vsAi 모드에서는 1=사람, 2=컴퓨터로 취급
        int[] missCount = new int[3]; // missCount[1], missCount[2] 사용 (오답 누적 횟수)

        while (true) {
            boolean aiMove = vsAi && humanTurn == 2;

            System.out.print(prompt(game, aiMove));
            String word;
            if (aiMove) {
                List<String> candidates = game.nextCandidates(1);
                word = candidates.isEmpty() ? "" : candidates.get(0);
                System.out.println(word);
            } else {
                if (!sc.hasNextLine()) return;
                word = sc.nextLine().trim();
            }

            if (word.equals("0")) {
                System.out.println("0을 입력하여 게임을 종료합니다.");
                return;
            }

            MoveResult result = game.submit(word);
            switch (result) {
                case OK -> {
                    System.out.printf("통과! (지금까지 사용한 단어 %d개)%n", game.usedCount());
                    humanTurn = (humanTurn == 1) ? 2 : 1;
                }
                case NOT_A_WORD -> System.out.println("사전에 없는 단어입니다. 다시 입력하세요.");
                case ALREADY_USED -> System.out.println("이미 사용된 단어입니다. 다시 입력하세요.");
                case WRONG_START -> System.out.println("이어지는 글자가 맞지 않습니다. 다시 입력하세요.");
                case TOO_SHORT -> System.out.println("한 글자짜리 단어는 사용할 수 없습니다. 다시 입력하세요.");
                case GAME_OVER -> System.out.println("게임이 이미 끝났습니다.");
            }

            if (result != MoveResult.OK) {
                missCount[humanTurn]++;
                int left = MAX_MISS - missCount[humanTurn];
                if (missCount[humanTurn] >= MAX_MISS) {
                    String loser = who(humanTurn, vsAi);
                    String winner = who(humanTurn == 1 ? 2 : 1, vsAi);
                    System.out.printf("%s 오답 %d회 누적으로 패배! %s 승리!%n", loser, MAX_MISS, winner);
                    return;
                } else {
                    System.out.printf("(오답 %d/%d, %d번 더 틀리면 패배)%n", missCount[humanTurn], MAX_MISS, left);
                }
            }

            if (game.isOver()) {
                int winner = (humanTurn == 1) ? 2 : 1; // 마지막에 낼 말이 없어서 막힌 사람이 진 것
                System.out.printf("게임 종료! 마지막 단어: %s / %s 승리%n", game.lastWord(), who(winner, vsAi));
                return;
            }
        }
    }

    /** 대결 모드를 고른다. 1=컴퓨터와 대결, 2=로컬 대결(2인). */
    private static boolean chooseMode(Scanner sc) {
        while (true) {
            System.out.println("1. 컴퓨터와 대결");
            System.out.println("2. 로컬 대결 (2인)");
            System.out.print("선택: ");
            if (!sc.hasNextLine()) return false;
            String line = sc.nextLine().trim();
            if (line.equals("1")) return true;
            if (line.equals("2")) return false;
            System.out.println("1 또는 2를 입력하세요.");
        }
    }

    private static String who(int turn, boolean vsAi) {
        if (vsAi) return turn == 1 ? "플레이어" : "컴퓨터";
        return turn + "번째 플레이어";
    }

    private static String prompt(WordChainGame game, boolean aiMove) {
        String who = aiMove ? "[컴퓨터] " : "[플레이어] ";
        if (game.lastWord() == null) {
            return who + "시작 단어를 입력하세요 (0 입력 시 종료): ";
        }
        int lastCp = game.lastWord().codePointBefore(game.lastWord().length());
        String startChar = new String(Character.toChars(lastCp));
        return who + "'" + startChar + "'" + josaRo(lastCp) + " 시작하는 단어: ";
    }

    /** 완성형 한글 음절의 받침 유무에 따라 "로"/"으로" 조사를 골라준다 (받침 없음 또는 ㄹ받침 -> 로). */
    private static String josaRo(int syllableCp) {
        if (syllableCp < 0xAC00 || syllableCp > 0xD7A3) return "로"; // 한글 완성형이 아니면 기본값
        int jong = (syllableCp - 0xAC00) % 28; // 0=받침 없음, 8=ㄹ받침
        return (jong == 0 || jong == 8) ? "로" : "으로";
    }
}
