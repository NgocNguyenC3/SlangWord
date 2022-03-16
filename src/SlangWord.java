import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SlangWord {
    private static String fileDataPath = "slang.txt";
    private static String fileDataSlangPackUpPath = "slang_back_up.txt";
    private static String fileDataDefinitionPackUpPath = "definition.txt";
    private static String fileHistory = "history.txt";

    private static HashMap<String, String> listSlang ;
    private static HashMap<String, Set<String>> listDefinition;
    public static void main(String args[]) {
        getDataFromRootFile();

        menu();
    }

    private static void menu() {
        menuOption();
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
}
