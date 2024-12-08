import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InventoryOOP {
    private static final String FILE_PATH = "data/inventory.json";
    private List<Item> items;

    public static void main(String[] args) throws IOException {
        InventoryOOP inventory = new InventoryOOP();
        inventory.loadItems();

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
                case 1 -> inventory.addItem(scanner);
                case 2 -> inventory.viewItems();
                case 3 -> inventory.updateItem(scanner);
                case 4 -> inventory.deleteItem(scanner);
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

    // Constructor to initialize the inventory items list
    public InventoryOOP() {
        this.items = new ArrayList<>();
    }

    // Load items from file
    private void loadItems() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        String content = Files.readString(Paths.get(FILE_PATH));
        this.items = parseJson(content);
    }

    // Save items to file
    private void saveItems() throws IOException {
        String json = toJson(items);
        Files.writeString(Paths.get(FILE_PATH), json);
    }

    // Add item to inventory
    private void addItem(Scanner scanner) throws IOException {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }
        if (isDuplicateId(id)) {
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

        Item newItem = new Item(id, name, condition);
        items.add(newItem);
        saveItems();
        System.out.println("Item added successfully!");
    }

    // View all items in inventory
    private void viewItems() {
        if (items.isEmpty()) {
            System.out.println("No items found.");
        } else {
            for (Item item : items) {
                System.out.println(item);
            }
        }
    }

    // Update an item in the inventory
    private void updateItem(Scanner scanner) throws IOException {
        System.out.print("Enter ID of the item to update: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }

        Item itemToUpdate = findItemById(id);
        if (itemToUpdate == null) {
            System.out.println("Item not found.");
            return;
        }

        // Check for duplicate ID during update
        System.out.print("Enter new ID: ");
        String newId = scanner.nextLine();
        if (!newId.isEmpty() && !newId.equals(id)) {
            if (isDuplicateId(newId)) {
                System.out.println("ID already exists. Please enter a unique ID.");
                return;
            }
            itemToUpdate.setId(newId);
        }

        System.out.print("Enter new Name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Invalid Name. Returning to menu.");
            return;
        }
        itemToUpdate.setName(name);

        System.out.print("Enter new Condition: ");
        String condition = scanner.nextLine();
        if (condition.isEmpty()) {
            System.out.println("Invalid Condition. Returning to menu.");
            return;
        }
        itemToUpdate.setCondition(condition);

        saveItems();
        System.out.println("Item updated successfully!");
    }

    // Delete an item from inventory
    private void deleteItem(Scanner scanner) throws IOException {
        System.out.print("Enter ID of the item to delete: ");
        String id = scanner.nextLine();
        if (id.isEmpty()) {
            System.out.println("Invalid ID. Returning to menu.");
            return;
        }

        boolean removed = items.removeIf(item -> item.getId().equals(id));
        if (removed) {
            saveItems();
            System.out.println("Item deleted successfully!");
        } else {
            System.out.println("Item not found.");
        }
    }

    // Parse JSON string into List of Item objects
    private List<Item> parseJson(String json) {
        List<Item> items = new ArrayList<>();
        if (json.isEmpty()) {
            return items;
        }

        String[] entries = json.substring(1, json.length() - 1).split("\\},\\{");
        for (String entry : entries) {
            String[] fields = entry.replaceAll("[\\[\\]\\{\\}]", "").split(",");
            String id = fields[0].split(":")[1].replaceAll("\"", "").trim();
            String name = fields[1].split(":")[1].replaceAll("\"", "").trim();
            String condition = fields[2].split(":")[1].replaceAll("\"", "").trim();
            items.add(new Item(id, name, condition));
        }
        return items;
    }

    // Convert List of Item objects to JSON string
    private String toJson(List<Item> items) {
        StringBuilder json = new StringBuilder("[");
        for (Item item : items) {
            json.append("{");
            json.append("\"id\":\"" + item.getId() + "\",");
            json.append("\"name\":\"" + item.getName() + "\",");
            json.append("\"condition\":\"" + item.getCondition() + "\"");
            json.append("},");
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1); // Remove trailing comma
        }
        json.append("]");
        return json.toString();
    }

    // Check if an item with the given ID already exists
    private boolean isDuplicateId(String id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Find an item by its ID
    private Item findItemById(String id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}

// Item class representing an individual inventory item
class Item {
    private String id;
    private String name;
    private String condition;

    public Item(String id, String name, String condition) {
        this.id = id;
        this.name = name;
        this.condition = condition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Condition: " + condition;
    }
}