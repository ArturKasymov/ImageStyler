import PIL
import sys
import cv2
from Transformer import Net
import numpy


def main():
    id = sys.stdin.readline()
    log = open("ab.txt", "w+")
    log.write(id)
    log.close()
    '''_ = sys.stdin.readline()
    preserve_size = sys.stdin.readline()
    if preserve_size == "false":
        preserve_size = False
    else:
        preserve_size = True
    net = Net(int(id), preserve_size)
    log = open("aa.txt", "w+")
    # input content_img
    shape = sys.stdin.readline()
    height = int(shape.split()[0])
    width = int(shape.split()[1])
    log.write("image id - (" + id + ")")
    log.write("Content shape - (" + str(height) + ", " + str(width) + ")")
    log.close()
    content_img = PIL.Image.new('RGB', (width, height), "black")
    content_pixels = content_img.load()
    for i in range(height):
        for j in range(width):
            rgbint = int(sys.stdin.readline())
            content_pixels[j, i] = ((rgbint >> 16) & 255, (rgbint >> 8) & 255, rgbint & 255)
    content_img = cv2.cvtColor(numpy.array(content_img), cv2.COLOR_RGB2BGR)
    if preserve_size:
        if width < height:
            content_size = width
        else:
            content_size = height
    else:
        content_size = 224
    style_size = 224
    net.generate(content_img, content_size=content_size)
    gen_img = net.get_image()
    log.write("Result shape - (" + str(gen_img.shape[0]) + ", "+ str(gen_img.shape[1]) + ")")
    print(gen_img.shape)
    for i in range(gen_img.shape[0]):
        for j in range(gen_img.shape[1]):
            print("%d %d %d" % (gen_img[i, j, 0], gen_img[i, j, 1], gen_img[i, j, 2]))
    log.write("Finished")
    log.close()'''


if __name__ == '__main__':
    main()
