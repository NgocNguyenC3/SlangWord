import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SlangWord {
    private static String fileDataPath = "slang.txt";
    private static String fileDataSlangPackUpPath = "slang_back_up.txt";
    private static String fileDataDefinitionPackUpPath = "definition.txt";
    private static String fileHistory = "history.txt";
    private static long startTime = 0;
    private static long endTime = 0;
    private static HashMap<String, String> listSlang ;
    private static HashMap<String, Set<String>> listDefinition;
    private static String[] answerList = {"A", "B", "C", "D"};
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String args[]) {
        getDataFromRootFile();

        menu();
    }

    private static void menu() {
        int option = 0;
        do{
            menuOption();
            option = Integer.parseInt(scanner.nextLine());
            if(option < 0 || option > 12) {
                printLn("Wrong option!");
                continue;
            }

            switch (option) {
                // Search by slang word
                case 1:
                    eventSearchSlangWord();
                    break;
                // Search by definition
                case 2:
                    eventSearchDefinition();
                    break;
                //
                case 3:

                    break;
                //
                case 4:

                    break;
                //
                case 5:

                    break;
                //
                case 6:

                    break;
                // Reset Data
                case 7:
                    getDataFromRootFile();
                    break;
                // Random slang word
                case 8:
                    randomSlangWord();
                    break;
                // Funny game random slang Word
                // Mode true: Slang Word
                case 9:
                    gameStart(true);
                    break;
                //Funny game random Definition
                // Mode false: Definition
                case 10:
                    gameStart(false);
                    break;
                //
                case 11:

                    break;
            }
        }while(option != 12);

    }

    // Mode true: Slang Word, Mode false: Definition
    private static void gameStart(boolean mode) {
        int i = 1;
        do{
            randomGame(mode);
            printLn("Continue: 1: Yes, 0: No");
            i = Integer.parseInt(scanner.nextLine());
        }while(i !=0);
    }


    // SlangWord Game
    // Mode true: Slang Word, Mode false: Definition
    private static void randomGame(boolean modeGame) {
        Random generator = new Random();

        int index = generator.nextInt(4);
        List<String> randomList = new ArrayList<>();
        while(randomList.size() != 4) {
            String value = getRandomSlangWord();
            if(!randomList.contains(value))
                randomList.add(value);
        }

        if(modeGame)
        gameRandomMode(randomList.get(index), listSlang.get(randomList.get(0)),
                listSlang.get(randomList.get(1)), listSlang.get(randomList.get(2)),
                listSlang.get(randomList.get(3)), "Slang Word");
        else
        gameRandomMode(listSlang.get(randomList.get(index)), randomList.get(0),
                randomList.get(1), randomList.get(2),
                randomList.get(3), "Definition");
        String answer = scanner.nextLine();
        if(answer.toUpperCase().equals(answerList[index])) {
            printLn("Correct");
        } else {
            printLn("Incorrect");
            printLn("Right Answer: " + answerList[index]);
        }

    }

    // Print random Slang Word - definition
    private static void randomSlangWord() {
        String input = getRandomSlangWord();
        String output = listSlang.get(input);
        printResult(input, output);
    }

    // Get Random slang word
    private static String getRandomSlangWord() {
        Random generator = new Random();
        List<String> key = new ArrayList<String>(listSlang.keySet());
        return key.get(generator.nextInt(key.size()));
    }
    // Search by Definition
    private static void eventSearchDefinition() {
        print("Input: ");
        String definitionInput = scanner.nextLine();
        startTime = System.nanoTime();
        Set<String> output = listDefinition.get(definitionInput.toUpperCase());
        endTime = System.nanoTime();

        printResultDefinition(definitionInput, output, startTime, endTime);
    }

    // Search by Slang Word
    private static void eventSearchSlangWord() {
        print("Input: ");
        String slangInput = scanner.nextLine();
        startTime = System.nanoTime();
        String output = listSlang.get(slangInput);
        endTime = System.nanoTime();

        printResultSlangWord(slangInput, output, startTime, endTime);

    }

    // Show menu
    private static void menuOption() {
        printLn("");
        printLn("|----------------------------------------------------------------|");
        printLn("|                        Menu Slang Word!                        |");
        printLn("|----------------------------------------------------------------|");
        printLn("| 1. Search by Slang Word                                        |");
        printLn("| 2. Search by Definition                                        |");
        printLn("| 3. History search                                              |");
        printLn("| 4. Add new Search                                              |");
        printLn("| 5. Edit slang word                                             |");
        printLn("| 6. Delete slang word                                           |");
        printLn("| 7. Reset Data                                                  |");
        printLn("| 8. Random slang word                                           |");
        printLn("| 9. Funny game! Choose the correct definition for the slang word|");
        printLn("|10. Funny game! Choose the correct slang word for the definition|");
        printLn("|11. Save data                                                   |");
        printLn("|12. Exit                                                        |");
        printLn("|----------------------------------------------------------------|");
        print(" option = ");
    }

    // Show game
    private static void gameRandomMode(String slangWord, String A,
                                  String B, String C, String D , String gameMode) {
        printLn("");
        printLn("|-----------------------------------------------------------------");
        printLn("|    "+gameMode +": " + slangWord);
        printLn("| A. " + A + "\t" + "\t" + "B. " + B);
        printLn("| C. " + C + "\t" + "\t" + "D. " + D);
        printLn("|-----------------------------------------------------------------");
        print(" Your answer: ");
    }

    // Read data from slang.txt
    private static void getDataFromRootFile() {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        HashMap<String, String> slangList = new HashMap<String, String>();
        HashMap<String, Set<String>> definitionList = new HashMap<String, Set<String>>();

        try {
            fileInputStream = new FileInputStream(fileDataPath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line = bufferedReader.readLine();

            /*
            Every single line will divide into 2 HashMap( slang-definition and definition - List Slang)
            Data have character ' ' so we need remove this, should upcase data
            */
            while ((line = bufferedReader.readLine()) != null) {

                if(!line.contains("`")) {
                    continue;
                }

                String[] data = line.split("`");

                // remove ' ' slang
                if(data[0].lastIndexOf(" ") == data[0].length() - 1) {
                    data[0] = data[0].substring(0, data[0].length()- 2);
                }
                slangList.put(data[0], data[1]);

                // Definition
                String[] definitionData = data[1].split("\\|");
                Set<String> token = null;
                for(String value: definitionData) {
                    if(value.indexOf(" ") == 0) {
                        value = value.substring(1);
                    }
                    value = value.toUpperCase(Locale.ENGLISH);
                    token = definitionList.get(value);
                    if(token == null) {
                        definitionList.put(value, new HashSet<>());
                    }
                    definitionList.get(value).add(data[0]);

                }
            }
            listSlang = slangList;
            listDefinition = definitionList;

            fileInputStream.close();
            bufferedReader.close();
        }
        catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
    }

    // Print
    private static void printLn(String text) {
        System.out.println(text);
    }
    private static void print(String text) {
        System.out.print(text);
    }

    private static void printResultSlangWord(String slangInput, String output, long startTime, long endTime) {
        printLn("Slang word: " + slangInput);
        printLn("Definition: " + output);
        printLn("Total execution time: "+ (endTime- startTime) + " nanoseconds");
    }

    private static void printResult(String slangInput, String output) {
        printLn("Slang word: " + slangInput);
        printLn("Definition:" + output);
    }

    private static void printResultDefinition(String definitionInput, Set<String> output, long startTime, long endTime) {
        printLn("Definition : " + definitionInput);
        System.out.println("Slang Word:" + output);
        printLn("Total execution time: "+ (endTime- startTime) + " nanoseconds");
    }
    // End Utility
}
