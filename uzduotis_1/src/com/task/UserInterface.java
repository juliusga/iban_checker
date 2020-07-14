package com.task;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private final static String WELCOME_TEXT =
            "------------------------------------------------------------------\n" +
            "Application to check International Bank Account Number for errors.\n" +
            "------------------------------------------------------------------";
    private final static String OPTIONS =
            "Select an option (1-2):\n" +
            "[1] Interactive IBAN checker.\n" +
            "[2] Check IBAN's from file.";

    private final static String INTERACTIVE_CHECKER_INFO = "Interactive IBAN checker.\n" +
            "Please enter the IBAN number to check: ";

    private final static String FILE_CHECKER_INFO = "IBAN checker from file.\n" +
            "Please enter the full path to file, that contains IBAN's.\n" +
            "Example: C:\\Users\\Julius\\Documents\\Test files\\test file 1.txt\n" +
            "File location: ";

    private final static short NO_INPUT_RECEIVED = 100;
    private final static short COUNTRY_CODE_NOT_FOUND = 101;
    private final static short FORMAT_NOT_SATISFIED = 102;
    private final static short FORMAT_SATISFIED = 103;

    private Scanner inputScanner;
    private short currentOption;
    private Iban iban;
    private boolean isContinueSelected;
    private Scanner formatsFileScanner;

    private URL ibanFormatsLocation;

    public UserInterface()
    {
        ibanFormatsLocation = getClass().getResource("IBAN_formats");
        isContinueSelected = true;
        inputScanner = new Scanner(System.in);
        mainMethod();
    }

    private Scanner createFormatsFileScanner()
    {
        Scanner scanner = null;
        try {
            scanner = new Scanner(ibanFormatsLocation.openStream());
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] IBAN formats file not found. Exiting.");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scanner;
    }

    private void mainMethod()
    {
        System.out.println(WELCOME_TEXT);
        while (isContinueSelected) {
            System.out.println(OPTIONS);
            validateOption();
            executeCurrentOption();
            isContinueSelected = askToContinue();
        }
        inputScanner.close();
        System.out.println("Exiting...");
        System.exit(0);
    }

    private void validateOption()
    {
        while (true)
        {
            System.out.print("Enter the option number: ");
            if(inputScanner.hasNextShort())
            {
                currentOption = inputScanner.nextShort();
                if (currentOption < 1 || currentOption > 2)
                    System.out.println("[ERROR] Option " + currentOption + " doesn't exist.");
                else break;
            }
            else
            {
                System.out.println("[ERROR] Input is not an option number.");
                inputScanner.next();
            }
        }
        System.out.println("[OK] Starting option " + currentOption + ".");
    }

    public void executeCurrentOption()
    {
        if (currentOption == 1) interactiveChecker();
        else if (currentOption == 2) fileChecker();
        else throw new RuntimeException("Wrong option number.");
    }

    // First option
    private void interactiveChecker()
    {
        System.out.print(INTERACTIVE_CHECKER_INFO);
        String userInput = inputScanner.next();
        switch (ibanChecker(userInput))
        {
            case NO_INPUT_RECEIVED:
                System.out.println("[BAD] Short or no input.");
                break;
            case COUNTRY_CODE_NOT_FOUND:
                System.out.println("[BAD] Country code not found. IBAN is invalid.");
                break;
            case FORMAT_NOT_SATISFIED:
                System.out.println("[BAD] Format not satisfied. IBAN is invalid.");
                break;
            case FORMAT_SATISFIED:
                System.out.println("[GOOD] Format satisfied. IBAN is valid.");
                break;
        }
    }

    // Second option
    private void fileChecker()
    {
        System.out.print(FILE_CHECKER_INFO);
        inputScanner.nextLine();
        String location = inputScanner.nextLine();
        Scanner inputFileScanner;
        FileOutputStream fileOutStream;
        BufferedOutputStream buffer;
        byte b[];
        String currentIban;
        boolean isValid;
        try {
            inputFileScanner = new Scanner(new File(location));
            fileOutStream = new FileOutputStream(location + ".out");
            buffer = new BufferedOutputStream(fileOutStream);
            while (inputFileScanner.hasNextLine())
            {
                currentIban = inputFileScanner.nextLine();
                isValid = (ibanChecker(currentIban) == FORMAT_SATISFIED);
                b = (currentIban + ";" + isValid + "\n").getBytes();
                buffer.write(b);
            }
            buffer.flush();
            buffer.close();
            fileOutStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] " + e.getLocalizedMessage());
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[OK] Results successfully written to:\n" + location + ".out.");
    }

    // Returns format information line from IBAN_formats file.
    // If country code doesn't exist - returns empty string
    private String getFormatInfo(String countryCode)
    {
        formatsFileScanner = createFormatsFileScanner();
        String currentLine;
        while(formatsFileScanner.hasNextLine()){
            currentLine = formatsFileScanner.nextLine().trim();
            if(currentLine.contains(countryCode)){
                formatsFileScanner.close();
                return currentLine;
            }
        }
        formatsFileScanner.close();
        return "";
    }

    private boolean askToContinue()
    {
        String temp;
        while (true)
        {
            System.out.print("Continue? (y/n): ");
            temp = inputScanner.next();
            if(temp.equals("y")) return true;
            else if (temp.equals("n")) return false;
            else
            {
                System.out.println("Write only y (yes) or n (no).");
                continue;
            }
        }
    }

    // Checks if IBAN is valid
    // returns FORMAT_SATISFIED if valid
    // returns NO_INPUT_RECEIVED, COUNTRY_CODE_NOT_FOUND, FORMAT_NOT_SATISFIED if not
    private int ibanChecker(String ibanStr)
    {
        String formatInfo;
        if (ibanStr.length() < 15) return NO_INPUT_RECEIVED;
        else
        {
            iban = new Iban(ibanStr);
            formatInfo = getFormatInfo(iban.getCountryCode());
            if (formatInfo.isBlank()) return COUNTRY_CODE_NOT_FOUND;
            else {
                IbanFormat ibanFormat = new IbanFormat(formatInfo);
                if(ibanFormat.isFormatSatisfied(iban))
                    return FORMAT_SATISFIED;
                else return FORMAT_NOT_SATISFIED;
            }
        }
    }
}
