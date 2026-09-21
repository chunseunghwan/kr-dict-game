# 끝말잇기 (kr-dict-game)

TRIE 자료구조 학습 및 소프트웨어 프로젝트 기초 과제로 만든 터미널 기반 끝말잇기 게임입니다. Java로 구현했으며, 로컬 CSV 사전 파일을 읽어 TRIE를 구성하고 이를 기반으로 게임 규칙(단어 존재 확인, 중복 확인, 이어 말하기 확인)을 처리합니다.

## 실행 방법

1. JDK가 설치되어 있어야 합니다 (프로젝트는 IntelliJ 기준으로 구성되어 있습니다).
2. 프로젝트 폴더로 이동합니다.
   ```
   cd C:\Users\user\Downloads\kr-dict-game_1
   ```
3. 소스를 컴파일합니다.
   ```
   javac -d out\production\kr-dict-game_1 (Get-ChildItem -Recurse -Filter *.java src | % FullName)
   ```
4. `run.bat`을 실행합니다.
   ```
   .\run.bat
   ```
   (콘솔 코드페이지를 UTF-8로 바꾼 뒤 게임을 실행하는 배치 파일입니다. 직접 `java -cp ...` 명령으로 실행할 경우, 그전에 `chcp 65001`을 먼저 입력해야 한글 입출력이 깨지지 않습니다.)

## 게임 방법

- 실행하면 대결 모드를 고릅니다.
  - `1` : 컴퓨터와 대결
  - `2` : 로컬 대결 (2인, 한 자리에서 번갈아 입력)
- 이전 단어의 마지막 글자로 시작하는 단어를 입력하면 됩니다.
- 사전에 없는 단어, 이미 사용한 단어, 이어지는 글자가 틀린 단어, 한 글자짜리 단어는 오답으로 처리됩니다.
- 오답이 **3회 누적**되면 그 사람이 패배하고 게임이 끝납니다.
- 아무 때나 `0`을 입력하면 즉시 게임을 종료합니다.
- 다음 사람이 이어갈 수 있는 단어가 더 이상 없으면 자동으로 게임이 종료됩니다.

## 프로젝트 구조

```
kr-dict-game_1/
├── run.bat                          실행용 배치 파일 (chcp + java 실행)
├── src/kr/dict/
│   ├── trie/
│   │   ├── Trie.java                TRIE 공개 API (insert/contains/posOf/autocomplete)
│   │   ├── TrieNode.java            TRIE 노드 (HashMap<Integer, TrieNode> 기반)
│   │   ├── DictionaryLoader.java    CSV 사전 파일을 읽어 TRIE에 적재
│   │   └── Main.java                TRIE 동작 확인용 간단한 테스트 실행 지점
│   ├── game/
│   │   ├── WordChainGame.java       게임 규칙(제출 판정, 다음 후보 탐색) 담당
│   │   ├── WordChainCli.java        터미널 실행 지점 (모드 선택, 입출력, 오답 카운트)
│   │   └── MoveResult.java          단어 제출 결과를 나타내는 enum
│   └── data/
│       └── kr_korean.csv            한국어 단어·품사 사전 (약 50만 행)
```

## 자료구조 및 알고리즘 개요

- 한글 완성형 음절(11,172자) 전체를 배열로 두지 않고, 실제 등장한 글자만 `HashMap<Integer, TrieNode>`로 관리해 메모리를 절약합니다.
- 문자열은 유니코드 코드포인트(`codePoints()`, `codePointAt`) 단위로 다루어 서로게이트 페어 문제를 피합니다.
- 단어 존재 확인(`contains`)과 다음 후보 자동완성(`autocomplete`)은 모두 단어 길이에 비례하는 시간에 동작합니다.
- 사전(`Trie`)과 게임 진행 상태(`WordChainGame`)를 분리해, 하나의 사전을 여러 판의 게임에서 재사용할 수 있도록 설계했습니다.

## 알려진 한계

- 한국어 두음법칙(예: "녀" → "여")은 반영되어 있지 않습니다.
- AI 상대는 항상 첫 번째 자동완성 후보를 선택하는 단순한 전략입니다.
- 실행할 때마다 CSV 사전 전체를 다시 파싱하므로, 반복 실행 시 로딩 시간이 소요됩니다.
