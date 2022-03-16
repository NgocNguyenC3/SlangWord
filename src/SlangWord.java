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
                case 1:
                    eventSearchSlangWord();
                    break;
                case 2:
                    eventSearchDefinition();
                    break;
            }
        }while(option != 12);

    }

    private static void eventSearchDefinition() {
        print("Input: ");
        String definitionInput = scanner.nextLine();
        startTime = System.nanoTime();
        Set<String> output = listDefinition.get(definitionInput.toUpperCase());
        endTime = System.nanoTime();
        
        printResultDefinition(definitionInput, output, startTime, endTime);
    }

    private static void eventSearchSlangWord() {
        print("Input: ");
        String slangInput = scanner.nextLine();
        startTime = System.nanoTime();
        String output = listSlang.get(slangInput);
        endTime = System.nanoTime();

        printResultSlangWord(slangInput, output, startTime, endTime);

    }

    private static void menuOption() {
        printLn("");
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
        print(" option = ");
    }

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

    private static void printLn(String text) {
        System.out.println(text);
    }
    private static void print(String text) {
        System.out.print(text);
    }

    private static void printResultSlangWord(String slangInput, String output, long startTime, long endTime) {
        printLn("Slang word: " + slangInput);
        printLn("Definition:" + output);
        printLn("Total execution time: "+ (endTime- startTime) + " nanoseconds");
    }

    private static void printResultDefinition(String definitionInput, Set<String> output, long startTime, long endTime) {
        printLn("Definition : " + definitionInput);
        System.out.println("Slang Word:" + output);
        printLn("Total execution time: "+ (endTime- startTime) + " nanoseconds");
    }
}
