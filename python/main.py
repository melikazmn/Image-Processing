import numpy as np
import threading
from PIL import Image

class ImageProcessor(threading.Thread):
    def __init__(self, grayscale_image, start_x, end_x, output_image, lock):
        threading.Thread.__init__(self)
        self.grayscale_image = grayscale_image
        self.start_x = start_x
        self.end_x = end_x
        self.output_image = output_image
        self.lock = lock

        # Sobel kernels
        self.Gx_kernel = np.array([[-1, 0, 1],
                                   [-2, 0, 2],
                                   [-1, 0, 1]])

        self.Gy_kernel = np.array([[1, 2, 1],
                                   [0, 0, 0],
                                   [-1, -2, -1]])

    def run(self):
        height, width = self.grayscale_image.shape
        for x in range(self.start_x, self.end_x):
            for y in range(width):
                if x > 0 and x < height - 1 and y > 0 and y < width - 1:
                    Gx = np.sum(self.grayscale_image[x-1:x+2, y-1:y+2] * self.Gx_kernel)
                    Gy = np.sum(self.grayscale_image[x-1:x+2, y-1:y+2] * self.Gy_kernel)
                    magnitude = np.sqrt(Gx**2 + Gy**2)
                    self.lock.acquire()
                    try:
                        self.output_image[x, y] = np.clip(magnitude, 0, 255)
                    finally:
                        self.lock.release()

        # Calculate the mean of the magnitude image
        mean = np.mean(self.output_image[self.start_x:self.end_x])

        # Thresholding based on mean and updating the main output image with thread safety
        for x in range(self.start_x, self.end_x):
            for y in range(width):
                self.lock.acquire()
                try:
                    if self.output_image[x, y] > mean:
                        self.output_image[x, y] = 0  # Set to black
                    else:
                        self.output_image[x, y] = 255  # Set to white
                finally:
                    self.lock.release()

def rgb_to_grayscale(image):
    height, width, _ = image.shape
    gray_image = np.empty((height, width), dtype=np.uint8)

    for x in range(height):
        for y in range(width):
            red = image[x, y, 0]
            green = image[x, y, 1]
            blue = image[x, y, 2]
            gray = int(0.2989 * red + 0.5870 * green + 0.1140 * blue)
            gray_image[x, y] = gray

    return gray_image

def main():
    # Load the image
    image_path = "Screenshot 2024-07-07 171548.jpg"
    image = np.array(Image.open(image_path))

    if image is None:
        print(f"Error: Unable to load image '{image_path}'")
        return

    # Convert the image to grayscale
    grayscale_image = rgb_to_grayscale(image)

    # Parameters for threading
    num_threads = 9  # Number of threads to use
    threads = []
    height, width = grayscale_image.shape
    step_x = height // num_threads

    # Shared output image and lock for thread safety
    output_image = np.zeros((height, width), dtype=np.uint8)
    lock = threading.Lock()

    # Create threads
    for i in range(num_threads):
        start_x = i * step_x
        end_x = start_x + step_x if i < num_threads - 1 else height
        thread = ImageProcessor(grayscale_image, start_x, end_x, output_image, lock)
        threads.append(thread)

    # Start threads
    for thread in threads:
        thread.start()

    # Join threads
    for thread in threads:
        thread.join()

    # Display and save the output image
    output_image_rgb = np.stack([output_image]*3, axis=-1)
    Image.fromarray(output_image_rgb).show()
    Image.fromarray(output_image_rgb).save("output_image_thresholded.jpg")

if __name__ == "__main__":
    main()
