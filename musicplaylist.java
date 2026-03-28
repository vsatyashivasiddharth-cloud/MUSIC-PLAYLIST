package p1;

import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// ================= SONG CLASS =================
class Song {
    String Sname, Singer;
    double Rating;
    Song next;

    public Song(String Sname, String Singer, double Rating) {
        this.Sname = Sname;
        this.Singer = Singer;
        this.Rating = Rating;
        this.next = null;
    }

    public String toString() {
        return Sname + "\t" + Singer + "\t" + Rating;
    }
}

// ================= PLAYLIST CLASS =================
class PlayList {
    Song head = null, cnode, newSong, prev;

    Song[] topSongs = new Song[5];

    Stack<String> historyStack = new Stack<>();

    Queue<String> playNextQueue = new LinkedList<>();

    String filePath = "C:/DSA PROJECT SEM 2 File/playlist.txt";

    boolean isEmpty() {
        return head == null;
    }

    void loadFromFile() {
        try {
            File file = new File(filePath);

            if (!file.exists()) return;

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String parts[] = line.split(",");

                String name = parts[0];
                String singer = parts[1];
                double rating = Double.parseDouble(parts[2]);

                newSong = new Song(name, singer, rating);

                if (head == null) head = newSong;
                else {
                    cnode = head;
                    while (cnode.next != null) cnode = cnode.next;
                    cnode.next = newSong;
                }
            }

            fileScanner.close();
            updateTopSongs();

        } catch (Exception e) {
            System.out.println("Error loading file");
        }
    }

    void saveToFile() {
        try {
            FileWriter fw = new FileWriter(filePath, false);
            BufferedWriter bw = new BufferedWriter(fw);

            cnode = head;

            while (cnode != null) {
                bw.write(cnode.Sname + "," + cnode.Singer + "," + cnode.Rating);
                bw.newLine();
                cnode = cnode.next;
            }

            bw.flush();
            bw.close();

        } catch (Exception e) {
            System.out.println("Error saving file: " + e);
        }
    }

    void addSongs() {
        String choice;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Enter Song Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Singer Name: ");
            String singer = sc.nextLine();

            System.out.print("Enter Rating: ");
            double rate = sc.nextDouble();
            sc.nextLine();

            newSong = new Song(name, singer, rate);

            if (isEmpty()) head = newSong;
            else {
                cnode = head;
                while (cnode.next != null) cnode = cnode.next;
                cnode.next = newSong;
            }

            System.out.print("Add another song? (y/n): ");
            choice = sc.nextLine();

        } while (choice.equalsIgnoreCase("y"));

        updateTopSongs();
        saveToFile();
    }

    void updateTopSongs() {
        for (int i = 0; i < 5; i++) topSongs[i] = null;

        cnode = head;

        while (cnode != null) {
            for (int i = 0; i < 5; i++) {
                if (topSongs[i] == null || cnode.Rating > topSongs[i].Rating) {
                    for (int j = 4; j > i; j--) {
                        topSongs[j] = topSongs[j - 1];
                    }
                    topSongs[i] = cnode;
                    break;
                }
            }
            cnode = cnode.next;
        }
    }

    void displayTopSongs() {
        System.out.println("\n--- TOP 5 ---");
        for (int i = 0; i < 5; i++) {
            if (topSongs[i] != null) {
                System.out.println((i + 1) + ". " + topSongs[i].Sname);
            }
        }
    }

    void addToPlayNext(String name) {
        playNextQueue.add(name);
    }

    void displayHistory() {
        for (int i = historyStack.size() - 1; i >= 0; i--) {
            System.out.println(historyStack.get(i));
        }
    }

    void searchSong(String name) {
        cnode = head;
        while (cnode != null) {
            if (cnode.Sname.equals(name)) {
                System.out.println("Found: " + cnode);
                return;
            }
            cnode = cnode.next;
        }
        System.out.println("Not found");
    }

    void deleteSong(String name) {
        cnode = head;
        prev = null;

        while (cnode != null) {
            if (cnode.Sname.equalsIgnoreCase(name)) {
                if (prev == null) head = cnode.next;
                else prev.next = cnode.next;

                updateTopSongs();
                saveToFile();
                return;
            }
            prev = cnode;
            cnode = cnode.next;
        }
    }

    void playSong() {
        try {
            if (!playNextQueue.isEmpty()) {
                String qSong = playNextQueue.poll();
                historyStack.push(qSong);
                return;
            }

            File folder = new File("songs");
            File[] files = folder.listFiles();

            if (files == null) return;

            for (File file : files) {
                if (file.getName().endsWith(".wav")) {
                    historyStack.push(file.getName());

                    AudioInputStream audio = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();

                    clip.open(audio);
                    clip.start();

                    Thread.sleep(3000);
                    clip.close();
                }
            }

        } catch (Exception e) {
            System.out.println("Playback error");
        }
    }

    void display() {
        cnode = head;
        while (cnode != null) {
            System.out.println(cnode);
            cnode = cnode.next;
        }
    }
}

// ================= GUI CLASS =================
public class musicplaylist {

    PlayList pl = new PlayList();
    JTextArea displayArea;

    public musicplaylist() {

        JFrame frame = new JFrame("Music Playlist");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        displayArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(displayArea);

        JTextField name = new JTextField(10);
        JTextField singer = new JTextField(10);
        JTextField rating = new JTextField(5);

        JButton add = new JButton("Add");
        JButton play = new JButton("Play");
        JButton show = new JButton("Show");
        JButton top = new JButton("Top 5");
        JButton search = new JButton("Search");
        JButton delete = new JButton("Delete");
        JButton queue = new JButton("Queue");
        JButton history = new JButton("History");

        JPanel panel = new JPanel();

        panel.add(new JLabel("Name"));
        panel.add(name);

        panel.add(new JLabel("Singer"));
        panel.add(singer);

        panel.add(new JLabel("Rating"));
        panel.add(rating);

        panel.add(add);
        panel.add(play);
        panel.add(show);
        panel.add(top);
        panel.add(search);
        panel.add(delete);
        panel.add(queue);
        panel.add(history);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        pl.loadFromFile();

        // BUTTONS

        add.addActionListener(e -> {
            try {
                Song s = new Song(name.getText(), singer.getText(),
                        Double.parseDouble(rating.getText()));

                if (pl.head == null) pl.head = s;
                else {
                    Song t = pl.head;
                    while (t.next != null) t = t.next;
                    t.next = s;
                }

                pl.updateTopSongs();
                pl.saveToFile();

                displayArea.append("Added\n");

            } catch (Exception ex) {
                displayArea.append("Error\n");
            }
        });

        show.addActionListener(e -> {
            displayArea.setText("");
            Song t = pl.head;
            while (t != null) {
                displayArea.append(t + "\n");
                t = t.next;
            }
        });

        play.addActionListener(e -> {
            pl.playSong();
            displayArea.append("Playing...\n");
        });

        top.addActionListener(e -> {
            displayArea.setText("Top Songs\n");
            for (int i = 0; i < 5; i++) {
                if (pl.topSongs[i] != null)
                    displayArea.append(pl.topSongs[i].Sname + "\n");
            }
        });

        search.addActionListener(e -> {
            Song t = pl.head;
            while (t != null) {
                if (t.Sname.equals(name.getText())) {
                    displayArea.append("Found\n");
                    return;
                }
                t = t.next;
            }
            displayArea.append("Not Found\n");
        });

        delete.addActionListener(e -> {
            pl.deleteSong(name.getText());
            displayArea.append("Deleted\n");
        });

        queue.addActionListener(e -> {
            pl.addToPlayNext(name.getText());
            displayArea.append("Queued\n");
        });

        history.addActionListener(e -> {
            displayArea.setText("History\n");
            for (int i = pl.historyStack.size() - 1; i >= 0; i--) {
                displayArea.append(pl.historyStack.get(i) + "\n");
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new musicplaylist();
    }
}