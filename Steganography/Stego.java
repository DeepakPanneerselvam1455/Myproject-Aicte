import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageSteganographyGUI {
    private JFrame frame;
    private JTextField messageField;
    private JPasswordField passcodeField;
    private JLabel imageLabel, statusLabel;
    private File selectedFile;
    private BufferedImage img;

    public ImageSteganographyGUI() {
        frame = new JFrame("🔐 Image Steganography Tool");
        frame.setSize(600, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Set Background Color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(40, 44, 52)); // Dark Mode Theme

        // Top Panel (Image Preview)
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(30, 35, 40));
        imageLabel = new JLabel("📷 No Image Selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));
        imageLabel.setForeground(Color.WHITE);
        imagePanel.add(imageLabel);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(40, 44, 52));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton selectImageButton = createStyledButton("🖼 Select Image");
        selectImageButton.addActionListener(this::selectImage);

        messageField = createStyledTextField();
        passcodeField = new JPasswordField(20);
        passcodeField.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton encryptButton = createStyledButton("🔒 Encrypt Message");
        JButton decryptButton = createStyledButton("🔓 Decrypt Message");

        encryptButton.addActionListener(this::encryptMessage);
        decryptButton.addActionListener(this::decryptMessage);

        inputPanel.add(selectImageButton);
        inputPanel.add(createStyledLabel("✍ Enter Secret Message:"));
        inputPanel.add(messageField);
        inputPanel.add(createStyledLabel("🔑 Enter Passcode:"));
        inputPanel.add(passcodeField);
        inputPanel.add(encryptButton);
        inputPanel.add(decryptButton);

        // Status Label
        statusLabel = new JLabel("⚡ Status: Waiting for action...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.CYAN);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Layout
        mainPanel.add(imagePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void selectImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                img = ImageIO.read(selectedFile);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
                statusLabel.setText("✅ Selected: " + selectedFile.getName());
                statusLabel.setForeground(Color.GREEN);
            } catch (IOException ex) {
                statusLabel.setText("❌ Error loading image!");
                statusLabel.setForeground(Color.RED);
            }
        }
    }

    private void encryptMessage(ActionEvent e) {
        if (selectedFile == null || img == null) {
            statusLabel.setText("⚠️ Please select an image first.");
            statusLabel.setForeground(Color.ORANGE);
            return;
        }

        String message = messageField.getText().trim();
        String passcode = new String(passcodeField.getPassword()).trim();

        if (message.isEmpty() || passcode.isEmpty()) {
            statusLabel.setText("⚠️ Enter both message & passcode.");
            statusLabel.setForeground(Color.ORANGE);
            return;
        }

        int width = img.getWidth();
        int height = img.getHeight();
        int x = 0, y = 0, channel = 0;

        for (char ch : message.toCharArray()) {
            int rgb = img.getRGB(x, y);
            int[] colorChannels = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };
            colorChannels[channel] = ch;

            int newRgb = (colorChannels[0] << 16) | (colorChannels[1] << 8) | colorChannels[2];
            img.setRGB(x, y, newRgb);

            y = (y + 1) % height;
            x = (x + 1) % width;
            channel = (channel + 1) % 3;
        }

        try {
            File outputFile = new File("encryptedImage.jpg");
            ImageIO.write(img, "jpg", outputFile);
            statusLabel.setText("✅ Encrypted & saved as encryptedImage.jpg");
            statusLabel.setForeground(Color.GREEN);
            JOptionPane.showMessageDialog(frame, "Message Successfully Encrypted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            statusLabel.setText("❌ Error saving image.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void decryptMessage(ActionEvent e) {
        if (selectedFile == null || img == null) {
            statusLabel.setText("⚠️ Please select an encrypted image first.");
            statusLabel.setForeground(Color.ORANGE);
            return;
        }

        String enteredPasscode = new String(passcodeField.getPassword()).trim();
        if (enteredPasscode.isEmpty()) {
            statusLabel.setText("⚠️ Enter passcode for decryption.");
            statusLabel.setForeground(Color.ORANGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        int width = img.getWidth();
        int height = img.getHeight();
        int x = 0, y = 0, channel = 0;

        for (int i = 0; i < 100; i++) {
            int rgb = img.getRGB(x, y);
            int[] colorChannels = { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };

            if (colorChannels[channel] < 32 || colorChannels[channel] > 126) break;

            message.append((char) colorChannels[channel]);

            y = (y + 1) % height;
            x = (x + 1) % width;
            channel = (channel + 1) % 3;
        }

        JOptionPane.showMessageDialog(frame, "🔓 Decrypted Message: " + message.toString(), "Decryption Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(60, 170, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.BOLD, 14));
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageSteganographyGUI::new);
    }
}
