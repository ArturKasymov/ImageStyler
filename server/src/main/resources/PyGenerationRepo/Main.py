from SqueezeNet import SqueezeNet
import PIL
import sys


def main():
    id = sys.stdin.readline()
    strength = sys.stdin.readline()
    preserve_size = sys.stdin.readline()
    if preserve_size == "false":
        preserve_size = False
    else:
        preserve_size = True
    squeezenet = SqueezeNet(int(id), float(strength))
    if len(sys.argv)>1:
        content_img = PIL.Image.open("/home/demian/ImageStyler/server/target/classes/PyGenerationRepo/images/tubingen.jpg")
        style_img = PIL.Image.open("/home/demian/ImageStyler/server/target/classes/PyGenerationRepo/images/starry_night.jpg")
        squeezenet.generate(content_img, style_img, init_random=False)
        print(squeezenet.get_image().shape)
    else:
        log = open("a.txt", "w+")
        # input content_img
        shape = sys.stdin.readline()
        height = int(shape.split()[0])
        width = int(shape.split()[1])
        log.write("image id - (" + id + ")")
        log.write("Content shape - (" + str(height) + ", " + str(width) + ")")
        content_img = PIL.Image.new('RGB', (width, height), "black")
        content_pixels = content_img.load()
        for i in range(height):
            for j in range(width):
                rgbint = int(sys.stdin.readline())
                content_pixels[j, i] = ((rgbint >> 16) & 255, (rgbint >> 8) & 255, rgbint & 255)
        if preserve_size:
            if width < height:
                content_size = width
            else:
                content_size = height
        else:
            content_size = 224
        # input style_img
        shape = sys.stdin.readline()
        height = int(shape.split()[0])
        width = int(shape.split()[1])
        log.write("Style shape - (" + str(height) + ", " + str(width) + ")")
        style_img = PIL.Image.new('RGB', (width, height), "black")
        style_pixels = style_img.load()
        for i in range(height):
            for j in range(width):
                rgbint = int(sys.stdin.readline())
                style_pixels[j,i] = ((rgbint >> 16) & 255, (rgbint >> 8) & 255, rgbint & 255)
        style_size = 224
        squeezenet.generate(content_img, style_img, content_size=content_size, style_size=style_size)
        gen_img = squeezenet.get_image()
        log.write("Result shape - (" + str(gen_img.shape[0]) + ", "+ str(gen_img.shape[1]) + ")")
        print(gen_img.shape)
        for i in range(gen_img.shape[0]):
            for j in range(gen_img.shape[1]):
                print("%d %d %d" % (gen_img[i, j, 0], gen_img[i, j, 1], gen_img[i, j, 2]))
        log.write("Finished")
        log.close()


if __name__ == '__main__':
    main()
