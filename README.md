# Multi-threaded Image Processing

This project demonstrates multi-threaded image processing using both Python and Java. The goal is to implement various image filters and techniques to process images efficiently by utilizing threading to improve performance.

## Project Structure
The project is divided into two main components:

- **Java Image Processing**:
  - Implements multiple filters, including Sobel, Gaussian, and Sepia.
  - Utilizes Java's threading capabilities to enhance performance in an interactive GUI application.

- **Python Image Processing**:
  - Focuses on implementing Sobel edge detection and thresholding.
  - Employs Python’s threading to handle image processing in chunks.

## Features

- Multi-threaded Implementation: Both Java and Python implementations use multi-threading to process images faster by dividing the workload across multiple threads.
- Multiple Filters: The Java component includes various filters such as:
- Sobel Filter: Detects edges in images.
- Gaussian Filter: Applies Gaussian blur to images.
- Sepia Filter: Applies a warm tone effect to images.
- Sobel Edge Detection: The Python component focuses on the Sobel operator for edge detection, showcasing its application in image processing.

## results 
- Initial image:
  - ![Screenshot 2024-07-07 171548](https://github.com/user-attachments/assets/b9266c6d-879c-447f-8b7d-a3f6b8aae2be)
- Sobel Edge Detection:
  - ![output_image_thresholded](https://github.com/user-attachments/assets/2432e5b7-df77-4948-bf27-ae40a52523e3)
- Gaussian Filter
  - ![output](https://github.com/user-attachments/assets/a43545f5-d9d4-43b7-9008-10e5702df191)
- Sepia Filter
   - ![output](https://github.com/user-attachments/assets/6acc80cf-76c8-469a-b842-3ad77cb468cf)
