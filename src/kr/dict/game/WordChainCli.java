package kr.dict.game;

import kr.dict.trie.DictionaryLoader;
import kr.dict.trie.Trie;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * 터미널에서 돌아가는 끝말잇기 실행 지점.
 * 사용법:
 *   java -cp out kr.dict.game.WordChainCli kr_korean.csv        (2인 로컬 대전)
 *   java -cp out kr.dict.game.WordChainCli kr_korean.csv ai     (컴퓨터와 대전)
 */
public class WordChainCli {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("사용법: java -cp out kr.dict.game.WordChainCli kr_korean.csv [ai]");
            return;
        }
        boolean vsAi = args.length >= 2 && args[1].equalsIgnoreCase("ai");

        Trie dictionary = new Trie();
        long t0 = System.currentTimeMillis();
        int count = DictionaryLoader.load(dictionary, args[0]);
        long t1 = System.currentTimeMillis();
        System.err.printf("%d개 단어 로딩 완료 (%.2f초)%n", count, (t1 - t0) / 1000.0);

        WordChainGame game = new WordChainGame(dictionary);
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        int humanTurn = 1; // vsAi 모드에서는 1=사람, 2=컴퓨터로 취급

        while (!game.isOver()) {
            boolean aiMove = vsAi && humanTurn == 2;

            System.out.print(prompt(game, aiMove));
            String word;
            if (aiMove) {
                List<String> candidates = game.nextCandidates(1);
                word = candidates.isEmpty() ? "" : candidates.get(0);
                System.out.println(word);
            } else {
                if (!sc.hasNextLine()) break;
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
                case GAME_OVER -> System.out.println("게임이 이미 끝났습니다.");
            }
        }

        int winner = (humanTurn == 1) ? 2 : 1; // 마지막에 낼 말이 없어서 막힌 사람이 진 것
        System.out.printf("게임 종료! 마지막 단어: %s / %d번째 플레이어 승리%n", game.lastWord(), winner);
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
