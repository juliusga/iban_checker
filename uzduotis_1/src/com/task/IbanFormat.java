package com.task;

public class IbanFormat {
    private String countryCode;
    private String format;
    private int lenght;

    public int getLenght() {
        return lenght;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getFormat() {
        return format;
    }

    public IbanFormat(String formatStr)
    {
        format = "KK";
        countryCode = formatStr.substring(0, 2);
        int i;
        for(i = 3; formatStr.charAt(i) != ' '; i++);
        lenght = Integer.parseInt(formatStr.substring(3, i));
        String bbanPart = formatStr.substring(i++);
        String charsLenght = "";
        for(int j = 0; j < bbanPart.length(); j++)
        {
            while (Character.isDigit(bbanPart.charAt(j))){
                charsLenght += bbanPart.charAt(j);
                j++;
            }
            if (!charsLenght.isBlank())
                appendFormat(Integer.parseInt(charsLenght), bbanPart.charAt(j++));
            charsLenght = "";
        }
    }

    private void appendFormat(int lenght, char character)
    {
        character = Character.toUpperCase(character);
        for (int i = 0; i < lenght; i++)
        {
            format += character;
        }
    }

    public boolean isFormatSatisfied(Iban ibanToCheck)
    {
        if (ibanToCheck.getLenght() != getLenght())
            return false;
        char currentChar;
        for(int i = 0; i < ibanToCheck.getBbanPart().length(); i++)
        {
            currentChar = ibanToCheck.getBbanPart().charAt(i);
            switch (getFormat().charAt(i))
            {
                case 'A':
                    if(!(Character.isUpperCase(currentChar))) return false;
                    break;
                case 'N':
                    if(!Character.isDigit(currentChar)) return false;
                    break;
                case 'C':
                    if(!(Character.isLetterOrDigit(currentChar))) return false;
                    break;
            }
        }
        return true;
    }
}
