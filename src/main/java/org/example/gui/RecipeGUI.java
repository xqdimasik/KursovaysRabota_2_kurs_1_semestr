package gui;

import example.service.RecipeService;
import model.Category;
import model.Ingredient;
import model.Recipe;
import model.SpecialRecipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RecipeGUI extends JFrame {
    private final RecipeService service;
    private JList<String> recipeList;
    private DefaultListModel<String> listModel;
    private JTextArea recipeDetails;
    private JTextArea searchResults;

    public RecipeGUI() {
        service = new RecipeService();
        initializeGUI();
        updateRecipeList();
    }

    private void initializeGUI() {
        setTitle("Каталог рецептов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = createLeftPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder("Список рецептов"));

        listModel = new DefaultListModel<>();

        recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane listScroll = new JScrollPane(recipeList);
        listScroll.setPreferredSize(new Dimension(250, 0));

        JPanel listButtonPanel = createListButtonPanel();

        leftPanel.add(listScroll, BorderLayout.CENTER);
        leftPanel.add(listButtonPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createListButtonPanel() {
        JPanel listButtonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton refreshBtn = new JButton("Обновить");
        JButton viewBtn = new JButton("Просмотреть");
        JButton deleteBtn = new JButton("Удалить");

        refreshBtn.addActionListener(e -> {
            forceReloadRecipes();
            JOptionPane.showMessageDialog(this,
                    "Список рецептов перезагружен из файла!",
                    "Обновление",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        viewBtn.addActionListener(e -> showSelectedRecipe());
        deleteBtn.addActionListener(e -> deleteSelectedRecipe());

        listButtonPanel.add(refreshBtn);
        listButtonPanel.add(viewBtn);
        listButtonPanel.add(deleteBtn);

        return listButtonPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new TitledBorder("Детали рецепта"));

        recipeDetails = new JTextArea("Выберите рецепт из списка...");
        recipeDetails.setEditable(false);
        recipeDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        recipeDetails.setLineWrap(true);
        recipeDetails.setWrapStyleWord(true);

        JScrollPane detailsScroll = new JScrollPane(recipeDetails);
        centerPanel.add(detailsScroll, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(new TitledBorder("Управление"));

        JPanel addPanel = createAddPanel();
        JPanel searchPanel = createSearchPanel();
        JPanel interfacePanel = createInterfacePanel();

        rightPanel.add(addPanel, BorderLayout.NORTH);
        rightPanel.add(searchPanel, BorderLayout.CENTER);
        rightPanel.add(interfacePanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel createAddPanel() {
        JPanel addPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        addPanel.setBorder(new TitledBorder("Добавить рецепт"));

        JButton addSimpleBtn = new JButton("Обычный рецепт");
        JButton addSpecialBtn = new JButton("Праздничный рецепт");

        addSimpleBtn.addActionListener(e -> addRecipeDialog(false));
        addSpecialBtn.addActionListener(e -> addRecipeDialog(true));

        addPanel.add(addSimpleBtn);
        addPanel.add(addSpecialBtn);

        return addPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(new TitledBorder("Поиск"));

        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        JButton searchBtn = new JButton("Найти по названию");
        JButton searchIngBtn = new JButton("Найти по ингредиенту");

        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchBtn, BorderLayout.EAST);

        searchResults = new JTextArea("Результаты поиска будут отображены здесь...");
        searchResults.setEditable(false);
        searchResults.setFont(new Font("Monospaced", Font.PLAIN, 11));
        searchResults.setRows(10);

        JScrollPane resultsScroll = new JScrollPane(searchResults);

        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(searchIngBtn, BorderLayout.CENTER);
        searchPanel.add(resultsScroll, BorderLayout.SOUTH);

        final JTextField finalSearchField = searchField;
        searchBtn.addActionListener(e -> {
            String query = finalSearchField.getText().trim();
            if (!query.isEmpty()) {
                searchByTitle(query);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Введите текст для поиска!",
                        "Ошибка",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        searchIngBtn.addActionListener(e -> {
            String query = finalSearchField.getText().trim();
            if (!query.isEmpty()) {
                searchByIngredient(query);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Введите текст для поиска!",
                        "Ошибка",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        return searchPanel;
    }

    private JPanel createInterfacePanel() {
        JPanel interfacePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        interfacePanel.setBorder(new TitledBorder("Демонстрация интерфейсов"));

        JButton printShortBtn = new JButton("Краткие описания");
        JButton printAllBtn = new JButton("Полные форматы");

        printShortBtn.addActionListener(e -> showAllShortDescriptions());
        printAllBtn.addActionListener(e -> showAllPrintableFormats());

        interfacePanel.add(printShortBtn);
        interfacePanel.add(printAllBtn);

        return interfacePanel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem reloadItem = new JMenuItem("Перезагрузить из файла");
        reloadItem.addActionListener(e -> forceReloadRecipes());

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(reloadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Помощь");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void forceReloadRecipes() {
        RecipeService newService = new RecipeService();
        java.util.List<Recipe> reloadedRecipes = newService.getAll();

        listModel.clear();
        for (int i = 0; i < reloadedRecipes.size(); i++) {
            Recipe r = reloadedRecipes.get(i);
            String prefix = (r instanceof SpecialRecipe) ? "Праздничный: " : "Обычный: ";
            listModel.addElement((i + 1) + ". " + prefix + r.getTitle() +
                    " (" + r.getCategory().getDisplayName() + ")");
        }

        recipeDetails.setText("Список обновлен из файла. Выберите рецепт...");
        searchResults.setText("Результаты поиска будут отображены здесь...");
    }

    private void updateRecipeList() {
        listModel.clear();
        java.util.List<Recipe> recipes = service.getAll();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            String prefix = (r instanceof SpecialRecipe) ? "Праздничный: " : "Обычный: ";
            listModel.addElement((i + 1) + ". " + prefix + r.getTitle() +
                    " (" + r.getCategory().getDisplayName() + ")");
        }

        if (recipes.isEmpty()) {
            recipeDetails.setText("Список рецептов пуст. Добавьте рецепт!");
        } else {
            recipeDetails.setText("Выберите рецепт из списка...");
        }
    }

    private void showSelectedRecipe() {
        int index = recipeList.getSelectedIndex();
        if (index >= 0) {
            java.util.List<Recipe> recipes = service.getAll();
            if (index < recipes.size()) {
                Recipe r = recipes.get(index);
                recipeDetails.setText(r.toString());
                showRecipeInfo(r);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите рецепт из списка!",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showRecipeInfo(Recipe r) {
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder info = new StringBuilder();
        info.append("ИНФОРМАЦИЯ О РЕЦЕПТЕ\n");
        info.append("========================================\n\n");
        info.append("Название: ").append(r.getTitle()).append("\n");
        info.append("Категория: ").append(r.getCategory().getDisplayName()).append("\n");

        if (r instanceof SpecialRecipe) {
            SpecialRecipe sr = (SpecialRecipe) r;
            info.append("Праздник: ").append(sr.getHoliday()).append("\n");
        }

        info.append("\nКоличество ингредиентов: ").append(r.getIngredients().size()).append("\n");
        info.append("\nКраткое описание:\n");
        info.append(r.getShortDescription()).append("\n");

        infoArea.setText(info.toString());

        JOptionPane.showMessageDialog(this, infoArea,
                "Информация о рецепте: " + r.getTitle(),
                JOptionPane.INFORMATION_MESSAGE);

        JOptionPane.showMessageDialog(this,
                r.getPrintableFormat(),
                "Формат для печати: " + r.getTitle(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedRecipe() {
        int index = recipeList.getSelectedIndex();
        if (index >= 0) {
            java.util.List<Recipe> recipes = service.getAll();
            if (index < recipes.size()) {
                Recipe r = recipes.get(index);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить рецепт:\n" + r.getTitle() + "?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    service.removeRecipe(index);
                    updateRecipeList();
                    recipeDetails.setText("Рецепт '" + r.getTitle() + "' удален.");
                    JOptionPane.showMessageDialog(this,
                            "Рецепт '" + r.getTitle() + "' успешно удален!",
                            "Удаление",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите рецепт для удаления!",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addRecipeDialog(boolean isSpecial) {
        JDialog dialog = new JDialog(this, isSpecial ? "Добавить праздничный рецепт" : "Добавить рецепт", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Категория:"), gbc);
        gbc.gridx = 1;
        JComboBox<Category> categoryCombo = new JComboBox<>(Category.values());
        panel.add(categoryCombo, gbc);

        JTextField holidayField;
        if (isSpecial) {
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Праздник:"), gbc);
            gbc.gridx = 1;
            holidayField = new JTextField(20);
            panel.add(holidayField, gbc);
        } else {
            holidayField = null;
        }

        int instructionRow = isSpecial ? 3 : 2;
        gbc.gridx = 0; gbc.gridy = instructionRow;
        panel.add(new JLabel("Инструкция:"), gbc);
        gbc.gridx = 1;
        JTextArea instructionArea = new JTextArea(5, 20);
        instructionArea.setLineWrap(true);
        instructionArea.setWrapStyleWord(true);
        JScrollPane instructionScroll = new JScrollPane(instructionArea);
        panel.add(instructionScroll, gbc);

        gbc.gridx = 0; gbc.gridy = instructionRow + 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");

        saveBtn.addActionListener(e -> saveRecipeDialog(dialog, isSpecial, titleField, categoryCombo,
                holidayField, instructionArea));

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void saveRecipeDialog(JDialog dialog, boolean isSpecial, JTextField titleField,
                                  JComboBox<Category> categoryCombo, JTextField holidayField,
                                  JTextArea instructionArea) {
        String title = titleField.getText().trim();
        String instruction = instructionArea.getText().trim();

        if (title.isEmpty() || instruction.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Заполните все обязательные поля!");
            return;
        }

        Recipe recipe;
        if (isSpecial) {
            String holiday = holidayField.getText().trim();
            if (holiday.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Укажите праздник!");
                return;
            }
            recipe = new SpecialRecipe(title,
                    (Category) categoryCombo.getSelectedItem(),
                    instruction, holiday);
        } else {
            recipe = new Recipe(title,
                    (Category) categoryCombo.getSelectedItem(),
                    instruction);
        }

        addIngredientsDialog(recipe);
        service.addRecipe(recipe);
        updateRecipeList();
        dialog.dispose();

        JOptionPane.showMessageDialog(this,
                "Рецепт '" + title + "' успешно добавлен!",
                "Успех",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addIngredientsDialog(Recipe recipe) {
        while (true) {
            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Добавить ингредиент?"));

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Добавление ингредиентов",
                    JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.YES_OPTION) {
                if (recipe.getIngredients().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Рецепт должен содержать хотя бы один ингредиент!");
                    continue;
                }
                break;
            }

            JTextField nameField = new JTextField(15);
            JTextField amountField = new JTextField(10);

            JPanel ingPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            ingPanel.add(new JLabel("Название:"));
            ingPanel.add(nameField);
            ingPanel.add(new JLabel("Количество:"));
            ingPanel.add(amountField);

            int ingResult = JOptionPane.showConfirmDialog(this, ingPanel,
                    "Новый ингредиент",
                    JOptionPane.OK_CANCEL_OPTION);

            if (ingResult == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String amount = amountField.getText().trim();
                if (!name.isEmpty()) {
                    recipe.addIngredient(new Ingredient(name, amount));
                }
            }
        }
    }

    private void searchByTitle(String query) {
        java.util.List<Recipe> results = service.searchByTitle(query);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Рецепты с названием '" + query + "' не найдены.",
                    "Результат поиска",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        displaySearchResults(results, "Результаты поиска по названию: '" + query + "'");
    }

    private void searchByIngredient(String query) {
        java.util.List<Recipe> results = service.searchByIngredient(query);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Рецепты с ингредиентом '" + query + "' не найдены.",
                    "Результат поиска",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        displaySearchResults(results, "Результаты поиска по ингредиенту: '" + query + "'");
    }

    private void displaySearchResults(java.util.List<Recipe> results, String title) {
        if (results.isEmpty()) {
            searchResults.setText("Рецепты не найдены.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n\n");
            for (Recipe r : results) {
                sb.append("• ").append(r.getTitle())
                        .append(" (").append(r.getCategory().getDisplayName()).append(")\n");
                sb.append("  Инструкция: ");
                String shortInst = r.getInstruction().length() > 50 ?
                        r.getInstruction().substring(0, 47) + "..." : r.getInstruction();
                sb.append(shortInst).append("\n\n");
            }
            searchResults.setText(sb.toString());
        }
    }

    private void showAllShortDescriptions() {
        java.util.List<Recipe> recipes = service.getAll();
        if (recipes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Список рецептов пуст!",
                    "Краткие описания",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("КРАТКИЕ ОПИСАНИЯ ВСЕХ РЕЦЕПТОВ:\n");
        sb.append("==================================================\n\n");

        for (Recipe recipe : recipes) {
            sb.append("• ").append(recipe.getShortDescription()).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Краткие описания (через интерфейс Printable)",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAllPrintableFormats() {
        java.util.List<Recipe> recipes = service.getAll();
        if (recipes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Список рецептов пуст!",
                    "Полные форматы",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Recipe recipe : recipes) {
            sb.append(recipe.getPrintableFormat())
                    .append("\n")
                    .append("--------------------------------------------------")
                    .append("\n\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Полные форматы для печати",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String message = "Каталог рецептов\n\n" +
                "Версия 2.0 (с GUI)\n" +
                "Использованы интерфейсы:\n" +
                "• Printable - для форматирования вывода\n" +
                "• Storable - для сохранения в JSON\n" +
                "• Searchable - для поиска рецептов\n\n" +
                "Реализованы:\n" +
                "• Наследование (SpecialRecipe)\n" +
                "• Полиморфизм\n" +
                "• Графический интерфейс (Swing)";

        JOptionPane.showMessageDialog(this, message,
                "О программе", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Ошибка при установке Look and Feel: " + e.getMessage());
            }

            RecipeGUI gui = new RecipeGUI();
            gui.setVisible(true);
        });
    }
}