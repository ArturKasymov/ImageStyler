from SqueezeNet import SqueezeNet
import PIL
import sys


def main():
    squeezenet = SqueezeNet()
    if len(sys.argv)>1:
        content_img = PIL.Image.open("C:/Demko/Projects/ImageStyler/desktop-app/target/classes/PyGeneration/PyGenerationRepo/images/tubingen.jpg")
        style_img = PIL.Image.open("C:/Demko/Projects/ImageStyler/desktop-app/target/classes/PyGeneration/PyGenerationRepo/images/starry_night.jpg")
        squeezenet.generate(content_img, style_img, init_random=False)
        print(squeezenet.get_image().shape)
    else:
        # input content_img
        shape = sys.stdin.readline()
        height = int(shape.split()[0])
        width = int(shape.split()[1])
        '''
        content_img = PIL.Image.new( 'RGB', (width, height), "black")
        content_pixels = content_img.load()
        for i in range(height):
            for j in range(width):
                rgbint = int(sys.stdin.readline())
                content_pixels[j,i] = ((rgbint >> 16) & 255, (rgbint >> 8) & 255, rgbint & 255)
        content_size = width
        # input style_img
        shape = sys.stdin.readline()
        height = int(shape.split()[0])
        width = int(shape.split()[1])
        style_img = PIL.Image.new( 'RGB', (width, height), "black")
        style_pixels = style_img.load()
        for i in range(height):
            for j in range(width):
                rgbint = int(sys.stdin.readline())
                style_pixels[j,i] = ((rgbint >> 16) & 255, (rgbint >> 8) & 255, rgbint & 255)
        style_size = width
        squeezenet.generate(content_img, style_img, 224, 224)
        gen_img = squeezenet.get_image()'''
        print(height, width)
        '''for i in range(gen_img.shape[0]):
            for j in range(gen_img.shape[1]):
                print("%d %d %d" % (gen_img[i, j, 0], gen_img[i, j, 1], gen_img[i, j, 2]))'''


if __name__ == '__main__':
    main()
