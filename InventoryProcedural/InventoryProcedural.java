import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InventoryProcedural {
    private static final String FILE_PATH = "data/inventory.json";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Item");
            System.out.println("2. View Items");
            System.out.println("3. Update Item");
            System.out.println("4. Delete Item");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidMenuChoice(scanner);
            if (choice == -1) {
                System.out.println("Invalid input. Returning to menu.");
                continue;
            }

            switch (choice) {
                case 1 -> addItem(scanner);
                case 2 -> viewItems();
                case 3 -> updateItem(scanner);
                case 4 -> deleteItem(scanner);
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static int getValidMenuChoice(Scanner scanner) {
        int choice = -1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
        return choice;
    }

    private static List<Map<String, String>> readJsonFile() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        String content = Files.readString(Paths.get(FILE_PATH));
        return parseJson(content);
    }

    private static void writeJsonFile(List<Map<String, String>> items) throws IOException {
        String json = toJson(items);
        Files.writeString(Paths.get(FILE_PATH), json);
    }

    private static void addItem(Scanner scanner) throws IOException {
        List<Map<String, String>> items = readJsonFile();

        // Check for duplicate ID
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }
        if (isDuplicateId(items, id)) {
            System.out.println("ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Invalid Name. Returning to menu.");
            return;
        }

        System.out.print("Enter Condition: ");
        String condition = scanner.nextLine();
        if (condition.isEmpty()) {
            System.out.println("Invalid Condition. Returning to menu.");
            return;
        }

        Map<String, String> newItem = new HashMap<>();
        newItem.put("id", id);
        newItem.put("name", name);
        newItem.put("condition", condition);

        items.add(newItem);
        writeJsonFile(items);

        System.out.println("Item added successfully!");
    }

    private static void viewItems() throws IOException {
        List<Map<String, String>> items = readJsonFile();
        if (items.isEmpty()) {
            System.out.println("No items found.");
        } else {
            for (Map<String, String> item : items) {
                System.out.println("ID: " + item.get("id") +
                        ", Name: " + item.get("name") +
                        ", Condition: " + item.get("condition"));
            }
        }
    }

    private static void updateItem(Scanner scanner) throws IOException {
        List<Map<String, String>> items = readJsonFile();

        System.out.print("Enter ID of the item to update: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }

        // Check if the ID exists
        Map<String, String> itemToUpdate = findItemById(items, id);
        if (itemToUpdate == null) {
            System.out.println("Item not found.");
            return;
        }

        // Check for duplicate ID during update
        System.out.print("Enter new ID: ");
        String newId = scanner.nextLine();
        if (!newId.isEmpty() && !newId.equals(id)) {
            if (isDuplicateId(items, newId)) {
                System.out.println("ID already exists. Please enter a unique ID.");
                return;
            }
            itemToUpdate.put("id", newId);
        }

        System.out.print("Enter new Name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Invalid Name. Returning to menu.");
            return;
        }
        itemToUpdate.put("name", name);

        System.out.print("Enter new Condition: ");
        String condition = scanner.nextLine();
        if (condition.isEmpty()) {
            System.out.println("Invalid Condition. Returning to menu.");
            return;
        }
        itemToUpdate.put("condition", condition);

        writeJsonFile(items);
        System.out.println("Item updated successfully!");
    }

    private static void deleteItem(Scanner scanner) throws IOException {
        List<Map<String, String>> items = readJsonFile();

        System.out.print("Enter ID of the item to delete: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }

        boolean removed = items.removeIf(item -> item.get("id").equals(id));
        if (removed) {
            writeJsonFile(items);
            System.out.println("Item deleted successfully!");
        } else {
            System.out.println("Item not found.");
        }
    }

    private static List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> items = new ArrayList<>();
        if (json.isEmpty()) {
            return items;
        }

        String[] entries = json.substring(1, json.length() - 1).split("\\},\\{");
        for (String entry : entries) {
            Map<String, String> item = new HashMap<>();
            entry = entry.replaceAll("[\\[\\]\\{\\}]", ""); // Remove brackets and braces
            String[] fields = entry.split(",");
            for (String field : fields) {
                String[] keyValue = field.split(":");
                String key = keyValue[0].replaceAll("\"", "").trim();
                String value = keyValue[1].replaceAll("\"", "").trim();
                item.put(key, value);
            }
            items.add(item);
        }
        return items;
    }

    private static String toJson(List<Map<String, String>> items) {
        StringBuilder json = new StringBuilder("[");
        for (Map<String, String> item : items) {
            json.append("{");
            // Ensure the keys are written in the correct order: id, name, condition
            json.append("\"id\":\"" + item.get("id") + "\",");
            json.append("\"name\":\"" + item.get("name") + "\",");
            json.append("\"condition\":\"" + item.get("condition") + "\"");
            json.append("},");
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1); // Remove trailing comma
        }
        json.append("]");
        return json.toString();
    }

    private static boolean isDuplicateId(List<Map<String, String>> items, String id) {
        for (Map<String, String> item : items) {
            if (item.get("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> findItemById(List<Map<String, String>> items, String id) {
        for (Map<String, String> item : items) {
            if (item.get("id").equals(id)) {
                return item;
            }
        }
        return null;
    }
}