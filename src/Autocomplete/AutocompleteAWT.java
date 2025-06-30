package Autocomplete;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AutocompleteAWT {
    // Trie Node class
    class TrieNode {
        public boolean isEndOfWord;
        public java.util.Map<Character, TrieNode> children;

        public TrieNode() {
            isEndOfWord = false;
            children = new java.util.HashMap<>();
        }
    }

    // Trie class
    class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode node = root;
            for (char c : word.toLowerCase().toCharArray()) { // Convert to lowercase before inserting
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
            }
            node.isEndOfWord = true;
        }

        public List<String> searchByPrefix(String prefix) {
            List<String> results = new ArrayList<>();
            TrieNode node = root;
            for (char c : prefix.toLowerCase().toCharArray()) { // Convert to lowercase for search
                if (!node.children.containsKey(c)) {
                    return results;
                }
                node = node.children.get(c);
            }
            collectWords(node, new StringBuilder(prefix), results);
            return results;
        }

        private void collectWords(TrieNode node, StringBuilder prefix, List<String> results) {
            if (node.isEndOfWord) {
                results.add(prefix.toString());
            }
            for (java.util.Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                prefix.append(entry.getKey());
                collectWords(entry.getValue(), prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    private Trie genreTrie = new Trie();
    private Trie languageTrie = new Trie();
    private Trie authorTrie = new Trie();
    private Trie currentTrie;

    public AutocompleteAWT() {
        // Sample data
        String[] genres = {"Science Fiction", "Fantasy", "Mystery", "Romance", "Horror", "Thriller", "Historical Fiction", "Adventure", "Dystopian", "Young Adult(YA)", "Biography", "Autobiography", "Self-Help", "History", "Memoir", "Travel", "True Crime", "Science", "Philosophy", "CookBooks", "Finance", "Paranormal Romance", "Urban Fantasy", "Psychological Thriller", "Epic Fantasy", "Coming-of-Age", "Chick Lit", "Satire", "Steampunk", "Political Fiction", "Religion"};
        String[] languages = {"English", "Spanish", "French", "German", "Chinese", "Arabic", "Hindi", "Portuguese", "Japanese", "Russian", "Italian", "Korean", "Turkish", "Dutch", "Swedish", "Danish", "Norwegian", "Finnish", "Greek", "Polish", "Bengali", "Tamil", "Telgu", "Malyayalam", "Kannada", "Marathi", "Gujrati", "Punjabi", "Assamese", "Urdu"};
        String[] authors = {"J.K. Rowling", "George Orwell", "J.R.R. Tolkien", "Jane Austen", "Mark Twain", "William Shakespeare", "Ernest Hemingway", "Charles Dickens", "F. Scott Fitzgerald", "Virginia Woolf", "Agatha Christie", "Gabriel García Márquez", "Isabel Allende", "Federico García Lorca", "Carlos Ruiz Zafón", "Mario Vargas Llosa", "Jorge Luis Borges", "Laura Esquivel", "Cao Xueqin", "Yu Hua", "Qian Zhongshu", "Eileen Chang", "Zhang Ailing", "Victor Hugo", "Gustave Flaubert", "Jean-Paul Sartre", "Naguib Mahfouz", "Alaa Al Aswany", "Taha Hussein", "Premchand ", "Harivansh Rai Bachchan", "Jaishankar Prasad", "Alexander Pushkin", "Ivan Turgenev", "Yasunari Kawabata", "Haruki Murakami", "Rabindranath Tagore", "Kalki Krishnamurthy", "Thakazhi Sivasankara Pillai", "O. V. Vijayan", "P.L. Deshpande", "Bal Gangadhar Tilak", "Vinda Karandikar", "Babasaheb Purandare", "Sane Guruji", "Kusumagraj (V.V. Shirwadkar)", "Acharya Atre"};

        // Insert data into respective Tries
        for (String genre : genres) {
            genreTrie.insert(genre);
        }
        for (String language : languages) {
            languageTrie.insert(language);
        }
        for (String author : authors) {
            authorTrie.insert(author);
        }

        // Create JFrame
        JFrame frame = new JFrame("Autocomplete System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Background image
        JLabel backgroundLabel = new JLabel(scaleIcon("C:\\Users\\accou\\Downloads\\background.jpg", 600, 400));
        backgroundLabel.setBounds(0, 0, 600, 400);
        backgroundLabel.setLayout(null); // Allow adding components to this label

        // Navigation bar
        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 3));
        navBar.setBackground(new Color(117, 78, 26)); // Navbar background color #6F5A4B(BROWN)
        navBar.setBounds(0, 0, frame.getWidth(), 50);

        String[] navItems = {"Genre", "Language", "Author"};
        JLabel[] navLabels = new JLabel[3];
        for (int i = 0; i < navItems.length; i++) {
            JLabel label = new JLabel(navItems[i], SwingConstants.CENTER);
            label.setForeground(new Color(248, 225, 183)); // Navbar text color #E9E9EA
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navLabels[i] = label;
            navBar.add(label);
        }

        // Input field with icons
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(255, 242, 194)); // Set input panel background color to #FFF2C2
        inputPanel.setBorder(BorderFactory.createLineBorder(new Color(117, 78, 26))); // Subtle border(BROWN)
        inputPanel.setVisible(false); // Initially hidden
        inputPanel.setBounds(70, 70, 300, 40);

        JLabel searchIcon = new JLabel(scaleIcon("C:\\Users\\accou\\Downloads\\search.png", 20, 20));
        searchIcon.setHorizontalAlignment(SwingConstants.CENTER);
        searchIcon.setPreferredSize(new Dimension(30, 30));

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputField.setForeground(new Color(111, 90, 75)); // Text color in input field #6F5A4B
        inputField.setBackground(new Color(255, 242, 194)); // Input field background #E9E9EA

        JLabel clearIcon = new JLabel(scaleIcon("C:\\Users\\accou\\Downloads\\cancel.png", 20, 20));
        clearIcon.setHorizontalAlignment(SwingConstants.CENTER);
        clearIcon.setPreferredSize(new Dimension(30, 30));
        clearIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });

        inputPanel.add(searchIcon, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(clearIcon, BorderLayout.EAST);

        // Suggestions list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> suggestionsList = new JList<>(listModel);
        suggestionsList.setFont(new Font("Arial", Font.PLAIN, 14));
        suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionsList.setBackground(new Color(255, 242, 194)); // Set suggestions list background color to #FFF2C2
        suggestionsList.setForeground(new Color(111, 90, 75)); // Optional: Set text color to match input field


        JScrollPane scrollPane = new JScrollPane(suggestionsList);
        scrollPane.setBounds(50, 120, 300, 100); // Position below input panel
        scrollPane.setVisible(false); // Initially hidden
        scrollPane.setBackground(new Color(255, 242, 194)); // Set scroll pane background color to #FFF2C2
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(117, 78, 26))); // Subtle border

        // Add navigation bar to the background label
        backgroundLabel.add(navBar);
        backgroundLabel.add(inputPanel);
        backgroundLabel.add(scrollPane);

        // Dynamically adjust navBar width and background image on frame resize
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Resize background image
                backgroundLabel.setSize(frame.getWidth(), frame.getHeight());
                backgroundLabel.setIcon(scaleIcon("C:\\Users\\accou\\Downloads\\background.jpg", frame.getWidth(), frame.getHeight()));

                // Resize the navigation bar width when the frame is resized
                navBar.setBounds(0, 0, frame.getWidth(), 50);
            }
        });

        // Add action to labels
        for (JLabel label : navLabels) {
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Get the label's position
                    Point location = label.getLocationOnScreen();
                    SwingUtilities.convertPointFromScreen(location, frame);

                    // Determine the current Trie based on the clicked label
                    if (label.getText().equals("Genre")) {
                        currentTrie = genreTrie;
                    } else if (label.getText().equals("Language")) {
                        currentTrie = languageTrie;
                    } else {
                        currentTrie = authorTrie;
                    }

                    // Center the input panel below the label
                    int labelWidth = label.getWidth();
                    int panelWidth = inputPanel.getWidth();
                    int xOffset = (labelWidth - panelWidth) / 2; // Calculate centering offset

                    inputPanel.setBounds(location.x + xOffset, location.y + 50, 300, 40);
                    inputPanel.setVisible(true);

                    // Position and visibility of the suggestion scroll pane
                    scrollPane.setBounds(location.x + xOffset, location.y + 100, 300, 100);
                    scrollPane.setVisible(false);
                    inputField.requestFocus();
                }
            });
        }

        // Listen for text changes
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            private void updateSuggestions() {
                String text = inputField.getText().toLowerCase();
                listModel.clear();

                if (!text.isEmpty() && currentTrie != null) {
                    List<String> suggestions = currentTrie.searchByPrefix(text);
                    if (suggestions.isEmpty()) {
                        listModel.addElement("No suggestions available");
                    } else {
                        for (String suggestion : suggestions) {
                            listModel.addElement(suggestion);
                        }
                    }
                    scrollPane.setVisible(true);
                } else {
                    scrollPane.setVisible(false);
                }
            }
        });

        // Add the background label to the frame
        frame.setContentPane(backgroundLabel);
        frame.setVisible(true);
    }

    // Method to scale icons
    private ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutocompleteAWT::new);
    }
}
