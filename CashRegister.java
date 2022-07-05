// Homework 1: Cash Register
// Course: CIS357
// Due date: July 5 2022
// Name: Tanner Williams
// Github: https://github.com/Punkster81/cis357-hw1-Williams.git
// Instructor: Il-Hyung Cho
// Program description: This program reads in the contents of a file, a item list, including item id, item name, and price.
// it then lets a user enter the item they want to buy and how many. it then lets the user pay and gives back change.

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Arrays; //Import Array class to help manage arrays

/**
 * @author Tanner Williams
 *
 *         The Cash Register class contains almost the entire program. it stores
 *         all of the code to allow the choosing and purchasing of items
 *         and keeps track of what was purchased and how much was made in a
 *         certain day.
 */
public class CashRegister {
    /**
     * keeps track of currently being purchased items in the current transaction
     */
    static private CashRegister[] purchasedItems;
    /**
     * keeps track of the amount of a certain item thats being bought
     */
    int amount;
    /**
     * keeps track of how much the items being purchased are
     */
    double pricetotal;
    /**
     * holds name of purchased item
     */
    String name;
    /**
     * holds id of purchased items
     */
    int itemID;
    /**
     * allows class to keep track of the total profits made per use of program
     */
    static private double profitToday = 0;

    /**
     * constructor for the purchased item
     *
     * @param amount     the amount of the item being bought
     * @param pricetotal the total price of the amount of items being bought
     * @param name       name of the item
     * @param itemID     id of item
     */
    public CashRegister(int amount, double pricetotal, String name, int itemID) {
        this.amount = amount;
        this.pricetotal = pricetotal;
        this.name = name;
        this.itemID = itemID;
    }

    /**
     * inserts the chosen items into a 'cart', if already there adds on to the
     * amount and price already there
     *
     * @param sold takes a cash register obj as param
     */
    public static void intoCart(CashRegister sold) {
        if (purchasedItems[sold.itemID - 1] == null) {
            purchasedItems[sold.itemID - 1] = sold;
        } else {
            purchasedItems[sold.itemID - 1].amount += sold.amount;
            purchasedItems[sold.itemID - 1].pricetotal += sold.pricetotal;
        }
    }

    /**
     * sets the nice of the purchased item array
     *
     * @param size sets the size of the array to this number
     */
    public static void soldListSize(int size) {
        purchasedItems = new CashRegister[size];
    }

    /**
     * main method of file. runs the register program
     */
    public static void main(String[] args) {

        // gets the size of the file to set up arrays of proper size
        int fileSize = -1;
        try {
            File itemfile = new File("hw1_Input.txt");
            Scanner scanner2 = new Scanner(itemfile);
            while (scanner2.hasNextLine()) {
                scanner2.nextLine();
                fileSize += 1;
            }
            scanner2.close();

        } catch (FileNotFoundException e) {
            System.out.println("No file was found");
        }

        // sets up the items array and the purchased items array size

        Item.ItemListSize(fileSize + 1);
        CashRegister.soldListSize(fileSize + 1);

        // counter used to file the items array
        int arrayNumber = fileSize;

        // reads the file in
        try {
            Item[] itemlist = new Item[fileSize + 1];
            File itemfile = new File("hw1_Input.txt");
            Scanner scanner = new Scanner(itemfile);
            while (scanner.hasNextLine()) {

                String data = scanner.nextLine();

                // splits data by commas (id,name,price)
                String[] splicedData = data.split(",");
                // saves the data into an item obj
                Item item = new Item(Integer.parseInt(splicedData[0]), splicedData[1], Double.parseDouble(splicedData[2]));
                // puts item into array slot
                itemlist[fileSize - arrayNumber] = item;
                // increments array index
                arrayNumber--;
            }
            scanner.close();

            // files item class array
            Item.insertItems(itemlist);
            // error for file not found
        } catch (FileNotFoundException e) {
            System.out.println("No file was found");
        }

        // start of register progam display
        System.out.println("Welcome to Williams cash register system!");

        Scanner scanner = new Scanner(System.in);
        // running beginning of transaction until correct input is put in
        boolean transaction = true;
        while (transaction) {
            // clearing current cart
            Arrays.fill(CashRegister.purchasedItems, null);
            // asking about starting sale
            System.out.printf("%nBegin new sale? (Y/N): ");
            String inputString = scanner.next();

            // if yes starts a transaction
            if (inputString.equals("y") || inputString.equals("Y")) {
                System.out.printf("---------------------%n");

                boolean buying = true;

                while (buying) { // runs until a correct item code is entered

                    // asking for item code and checking to make sure its correct then printing name
                    // of item
                    System.out.printf("%20s", "Enter product code: ");
                    inputString = scanner.next();
                    if (isNumeric(inputString) && Integer.parseInt(inputString) > -1) {
                        if (Integer.parseInt(inputString) > 0 && Integer.parseInt(inputString) <= (fileSize + 1)) {

                            int itemID = Integer.parseInt(inputString);
                            System.out.printf("%18s: %s%n", "item name", Item.getItemName(Integer.parseInt(inputString)));

                            boolean quantity = true;
                            while (quantity) { // runs until correct quantity number is entered

                                // asks for quantity and then prints the price of how many of that object would
                                // cost
                                System.out.printf("%20s", "Enter quantity: ");
                                inputString = scanner.next();
                                if (isNumeric(inputString) && Integer.parseInt(inputString) > -1) {
                                    System.out.printf("%20s$ %.2f%n%n", "item total: ",
                                            Item.getItemPrice(itemID) * Integer.parseInt(inputString));
                                    quantity = false;

                                    // creating sold item then putting into sold item array
                                    CashRegister sold = new CashRegister(Integer.parseInt(inputString),
                                            Item.getItemPrice(itemID) * Integer.parseInt(inputString), Item.getItemName(itemID), itemID);
                                    CashRegister.intoCart(sold);

                                } else {
                                    // response if invalid quantity is entered, then asks again
                                    System.out.printf("Enter vaild quantity!!! %n%n");
                                }
                            }

                        } else {// respones if invalid product number is entered, then asks again
                            System.out.printf("Enter vaild product code!!! (1-10)%n%n");
                        }
                    } else if (isNumeric(inputString) && Integer.parseInt(inputString) == -1) { // if -1 is entered as product
                        // code then it enters the
                        // checkout
                        double subtotal = 0;
                        System.out.printf("---------------------%nItem list:%n");
                        // this method is to allow the sorting of the items by name
                        String[] dummyArray = new String[fileSize + 1];
                        int dummyInt = 0;
                        for (CashRegister item : CashRegister.purchasedItems) { // it fills a dummy array with the names of all the
                            // items that are being purchased
                            if (item != null) {
                                dummyArray[dummyInt] = item.name;

                            } else {
                                dummyArray[dummyInt] = "zzzzzz";
                            }

                            dummyInt++;
                        }

                        Arrays.sort(dummyArray); // sorts the array

                        for (String dummy : dummyArray) {
                            if (dummy != null) {
                                for (CashRegister item : CashRegister.purchasedItems) { // then compares the sorted to the unsorted and
                                    // prints in alphabetical order
                                    if (item != null && dummy == item.name) {
                                        System.out.printf("%5d %-14s$ %.2f%n", item.amount, item.name, item.pricetotal);
                                        subtotal += item.pricetotal;
                                    }
                                }
                            }
                        }

                        // this prints the subtotal and total formatted to all line up

                        double taxed = (subtotal * 1.06);
                        System.out.printf("%-20s$ %.2f%n", "Subtotal", subtotal);
                        System.out.printf("%s%.2f%n", "Total with tax (6%) $ ", taxed);
                        boolean ending = true;
                        while (ending) { // this repeats until a correct tender amount is entered
                            System.out.printf("%22s", "Tendered amout $ ");
                            String input = scanner.next();
                            if (isNumeric(input)) { // if a correct tender is entered
                                if (Double.parseDouble(input) >= taxed) {
                                    CashRegister.profitToday += taxed; // the total is added to the total profit
                                    System.out.printf("%22s%.2f%n%s", "Change $ ", (Double.parseDouble(input) - taxed),
                                            "---------------------"); // and the change is returned
                                    ending = false;
                                } else {
                                    System.out.printf("%nInput a cash amount higher than the total!!!");
                                }
                            } // error for incorrect price amount
                            else {
                                System.out.printf("%nInput a cash amount higher than the total!!!");
                            }
                        }
                        buying = false;

                    } else {
                        System.out.printf("Enter vaild product code!!! (1-10) %n%n"); // error for incorrect item id
                    }
                }

                // if the customer chooses to not start another transaction
            } else if (inputString.equals("n") || inputString.equals("N")) { // the system prints out the total profit for the
                // day and says goodbye
                transaction = false;
                System.out.printf("%n%s%.2f%n%n%s", "The total sale for today is $ ", CashRegister.profitToday,
                        "Thank you for using POST system. Goodbye");

            }

            else {
                System.out.println("Please input Y or N"); // if y or n arent entered it asks again

            }
        }
        scanner.close();
    }

    /**
     * this method checks to see if an input is a number or not
     *
     * @param input the inputed string to test if its a number or not
     */
    public static boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

/**
 * @author Tanner Williams
 *
 *         the item class allows the storing of all the item information from
 *         the file into one object, and all of the items into an array
 */
class Item {
    /**
     * holds item ID
     */
    int itemCode;
    /**
     * holds items name
     */
    String itemName;
    /**
     * holds item price
     */
    double unitPrice;
    /**
     * holds list of items to be accessed later
     */
    static private Item[] itemList;

    /**
     * constructor for the item object
     *
     * @param itemCode takes the ID of the item
     * @param itemName takes the name of the item
     * @param takes    the price of the item
     */
    public Item(int itemCode, String itemName, double unitPrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
    }

    /**
     * sets the size of the item list array
     *
     * @param size the size of the array
     */
    public static void ItemListSize(int size) {
        itemList = new Item[size];
    }

    /**
     * inserts an array of items into the class array
     *
     * @param takes an array of items to be transfered to the class
     */
    public static void insertItems(Item[] itemlist) {
        int i = 0;
        for (Item item : itemlist) {
            itemList[i] = item;
            i++;

        }
    }

    /**
     * gets the name of an item at a requested index
     *
     * @param place the array index
     * @return item name
     */
    public static String getItemName(int place) {
        return itemList[place - 1].itemName;
    }

    /**
     * gets the price of an item at a requested index
     *
     * @param place the array index
     * @return item price
     */
    public static double getItemPrice(int place) {
        return itemList[place - 1].unitPrice;
    }
}
