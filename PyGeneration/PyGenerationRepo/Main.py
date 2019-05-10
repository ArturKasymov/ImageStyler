from PyGenerationRepo.SqueezeNet import SqueezeNet
import PIL


def main():
    squeezenet = SqueezeNet()
    content_img = PIL.Image.open("images/tubingen.jpg")
    style_img = PIL.Image.open("images/composition_vii.jpg")
    squeezenet.generate(content_img, style_img)


if __name__ == '__main__':
    main()
