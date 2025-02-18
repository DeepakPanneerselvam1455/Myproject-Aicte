import cv2
import os
def text_to_ascii(text):
    return [ord(char) for char in text]
def ascii_to_text(ascii_values):
    return ''.join(chr(value) for value in ascii_values)
def encode_message(image_path, secret_message, password, output_path="encryptedImage.jpg"):
    try:
        img = cv2.imread(image_path)
        if img is None:
            print("❌ Error: Image not found!")
            return
        secret_message += "###" 
        ascii_values = text_to_ascii(secret_message)
        rows, cols, _ = img.shape
        index = 0
        for i in range(rows):
            for j in range(cols):
                for k in range(3):  # RGB channels
                    if index < len(ascii_values):
                        img[i, j, k] = ascii_values[index]  # Store ASCII value
                        index += 1
        cv2.imwrite(output_path, img)
        print(f"✅ Message encoded successfully and saved as: {output_path}")
        os.system(f"start {output_path}")
    except Exception as e:
        print(f"❌ Error: {e}")
def decode_message(image_path, password):
    try:
        img = cv2.imread(image_path)
        if img is None:
            print("❌ Error: Image not found!")
            return
        input_password = input("Enter passcode for Decryption: ")
        if input_password != password:
            print("❌ YOU ARE NOT AUTHORIZED!")
            return
        message_ascii = []
        rows, cols, _ = img.shape
        for i in range(rows):
            for j in range(cols):
                for k in range(3):
                    char = chr(img[i, j, k])
                    if char == '#' and ''.join(message_ascii[-2:]) == "##":
                        message_ascii = message_ascii[:-2]  # Remove delimiter
                        print("🔓 Decryption message:", ascii_to_text(message_ascii))
                        return
                    message_ascii.append(char)
        print("❌ No hidden message found!")
    except Exception as e:
        print(f"❌ Error: {e}")
image_path = r"IMG-20220709-WA0003.jpg"
password = input("Set a passcode: ")
secret_message = input("Enter secret message: ")
encode_message(image_path, secret_message, password)
decode_message("encryptedImage.jpg", password)
