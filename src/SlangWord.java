import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
        File fileSlangWord = new File(fileDataSlangPackUpPath);
        File fileDefinition = new File(fileDataDefinitionPackUpPath);

        // Get Data
        if(!fileSlangWord.exists() || !fileDefinition.exists()) {
            getDataFromRootFile();
        } else {
            getDataBackUp();
        }

        menu();
    }

    // Get My Data
    private static void getDataBackUp() {
        listSlang = new HashMap<String, String>();
        listDefinition = new HashMap<String, Set<String>>();

        //slang_back_up.txt (true) or definition.txt(false)
        getDataFile(true);
        getDataFile(false);
    }

    // Function get Data from slang_back_up.txt (true) or definition.txt(false)
    private static void getDataFile(Boolean isSlangWord) {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(isSlangWord? fileDataSlangPackUpPath:fileDataDefinitionPackUpPath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line ="";
            while ((line = bufferedReader.readLine()) != null) {
                String[] value = line.split("`");
                if(isSlangWord) {
                    listSlang.put(value[0], value[1]);
                } else {
                    if(listDefinition.get(value[0]) == null) {
                        listDefinition.put(value[0], new HashSet<>());
                    }
                    String[] data = value[1].split("\\|");
                    for (String i: data) {
                        listDefinition.get(value[0]).add(i);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    // Main menu
    private static void menu() {
        int option = 0;
        do{
            menuOption();
            String ref = scanner.nextLine();
            try {
                option = Integer.parseInt(ref);
            } catch (Exception ex) {
                printLn("Wrong option");
                continue;
            }

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
                // History slang word search
                case 3:
                    historySearch();
                    break;
                // Add new Slang Word
                case 4:
                    addNewWord();
                    break;
                // Edit Slang Word
                case 5:
                    editSlangWord();
                    break;
                // Delete slang word
                case 6:
                    deleteData();
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
                // Funny game Definition random
                // Mode false: Definition
                case 10:
                    gameStart(false);
                    break;
                //Save Data
                case 11:
                    saveAllData();
                    break;
            }
        }while(option != 12);

    }

    // Delete Data - Map<SlangWord, def> and Map <Def, Set<SlangWord>>
    private static void deleteData() {
        print("Input: ");
        String slangInput = scanner.nextLine();
        String output = listSlang.get(slangInput);
        if(output == null) {
            printLn("Cant find " + slangInput);
            return;
        }
        //Map <Def, Set<SlangWord>>
        deleteDefinition(slangInput, output);
        //Map<SlangWord, def>
        listSlang.remove(slangInput);

        printLn("Successfully");
    }

    // Edit data
    private static void editSlangWord() {
        print("Input: ");
        String slangInput = scanner.nextLine();
        String output = listSlang.get(slangInput);

        if(output == null) {
            printLn("Cant find " + slangInput);
            return;
        }
        print("Definition: ");
        String def = scanner.nextLine();

        // Delete old data Map <Def, Set<SlangWord>>
        deleteDefinition(slangInput, output);
        // Save new data Map <Def, Set<SlangWord>>
        saveEditDefinition(slangInput, def);
        listSlang.put(slangInput, def);

        printLn("Successfully");
    }

    // Save new data Map <Def, Set<SlangWord>>
    private static void saveEditDefinition(String slangInput, String def) {
        String[] data = def.split("\\|");
        for(String i: data) {
            Set<String> set = listDefinition.get(i.toUpperCase());
            if(set == null) {
                listDefinition.put(i.toUpperCase(), new HashSet<>());
            }

            listDefinition.get(i.toUpperCase()).add(slangInput);
        }
    }

    // Delete data Map <Def, Set<SlangWord>>
    private static void deleteDefinition(String input, String output) {
        String[] data = output.split("\\|");

        for(String i: data) {
            Set<String> set = listDefinition.get(i.toUpperCase());
            if(set != null) {
                set.remove(input);
                if(set.size() == 0) listDefinition.remove(i.toUpperCase());
            }
        }


    }

    // Write file
    private static void saveAllData() {
        saveSlangWord();
        saveDefinitionWord();
    }

    // Write definition.txt
    private static void saveDefinitionWord() {
        File f = new File(fileDataDefinitionPackUpPath);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                printLn("Cant create history file");
                return;
            }
        }

        try {
            FileWriter fw = new FileWriter(f);
            Set<String> setKey = listDefinition.keySet();
            for (String key: setKey) {
                String line = "";
                Set<String> data = listDefinition.get(key);
                for(String i: data) {
                    line += i + "|";
                }
                if(line == "") continue;
                fw.write(key + "`" + line + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        printLn("Successfully");
    }

    // Write slang_back_up.txt
    private static void saveSlangWord() {
        File f = new File(fileDataSlangPackUpPath);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                printLn("Cant create history file");
                return;
            }
        }

        try {
            FileWriter fw = new FileWriter(f);
            Set<String> setKey = listSlang.keySet();
            for (String key: setKey) {
                fw.write(key + "`" + listSlang.get(key) + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Add new slang word
    private static void addNewWord() {
        print("Slang Word: ");
        String key = scanner.nextLine();
        print("Definition: ");
        String data = scanner.nextLine();
        listSlang.put(key,data);

        // Create new Def
        if(data.contains("|")) {
            String[] value = data.split("\\|");

            for(String i: value) {
                Set<String> set = listDefinition.get(i.toUpperCase());
                if(set == null) {
                    listDefinition.put(i.toUpperCase(), new HashSet<>());

                }
                listDefinition.get(i.toUpperCase(Locale.ROOT)).add(key);
            }
        }

        printLn("Successfully");
    }

    // History search slang word, Slangword - time
    private static void historySearch() {
        List<String> listHistory = new ArrayList<>();
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;

        try {
            fileInputStream = new FileInputStream(fileHistory);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line ="";
            while((line = bufferedReader.readLine()) != null) {
                if(line.equals("")) continue;
                listHistory.add(line);
            }
        } catch (FileNotFoundException e) {
            printLn("No history");
            return;
        } catch (IOException e) {
            return;
        }

        for(String key: listHistory) {
            printLn(key);
        }
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
        scanner.useDelimiter("\n");
        startTime = System.nanoTime();
        String output = listSlang.get(slangInput);
        scanner.useDelimiter("\n");
        endTime = System.nanoTime();

        printResultSlangWord(slangInput, output, startTime, endTime);

        saveHistorySlangWord(slangInput);
    }

    // Save History Slang Word
    private static void saveHistorySlangWord(String input) {
        java.util.Date date = new java.util.Date();
        String time = date.toString();

        File f = new File(fileHistory);
        if(!f.exists()) {
            try {
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("Slang Word          Time"+ "\n" );
                fw.close();
            } catch (IOException e) {
                printLn("Cant create history file");
                return;
            }
        }

        try {
            FileWriter fw = new FileWriter(f, true);
            fw.write(input + "      " + time + "\n" );
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        printLn("| 4. Add new Slang Word                                          |");
        printLn("| 5. Edit Slang Word                                             |");
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
    // End Print
}
