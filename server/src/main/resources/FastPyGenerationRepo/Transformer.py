import torch
import torch.nn as nn
import cv2
from utils import Utils
import PIL


class Net:

    def __init__(self, id, preserve_size):
        self.net = TransformerNet()
        if id == 3:
            self.net.load_state_dict(torch.load("src/main/resources/Transform_weights/udnie.pth", map_location='cpu'))
        elif id == 4:
            self.net.load_state_dict(torch.load("src/main/resources/Transform_weights/starry.pth", map_location='cpu'))
        self.net = self.net.to("cpu")
        self.generated_image = None

    def generate(self, content_img, content_size=224):
        with torch.no_grad():
            content_tensor = Utils.preprocess(content_img)
            generated_tensor = self.net(content_tensor)
            self.generated_image = Utils.deprocess(generated_tensor)

    def get_image(self):
        return self.generated_image


class TransformerNet(nn.Module):

    def __init__(self):
        super(TransformerNet, self).__init__()
        self.ConvBlock = nn.Sequential(
            ConvLayer(3, 32, 9, 1),
            nn.ReLU(),
            ConvLayer(32, 64, 3, 2),
            nn.ReLU(),
            ConvLayer(64, 128, 3, 2),
            nn.ReLU()
        )
        self.ResidualBlock = nn.Sequential(
            ResidualLayer(128, 3),
            ResidualLayer(128, 3),
            ResidualLayer(128, 3),
            ResidualLayer(128, 3),
            ResidualLayer(128, 3)
        )
        self.DeconvBlock = nn.Sequential(
            DeconvLayer(128, 64, 3, 2, 1),
            nn.ReLU(),
            DeconvLayer(64, 32, 3, 2, 1),
            nn.ReLU(),
            ConvLayer(32, 3, 9, 1, norm_type="None")
        )

    def forward(self, x):
        x = self.ConvBlock(x)
        x = self.ResidualBlock(x)
        out = self.DeconvBlock(x)
        return out


class ConvLayer(nn.Module):

    def __init__(self, in_channels, out_channels, kernel_size, stride, norm_type="instance"):
        super(ConvLayer, self).__init__()

        padding_size = kernel_size // 2
        self.reflection_pad = nn.ReflectionPad2d(padding_size)

        self.conv_layer = nn.Conv2d(in_channels, out_channels, kernel_size, stride)

        self.norm_type = norm_type
        if norm_type == "instance":
            self.norm_layer = nn.InstanceNorm2d(out_channels, affine=True)

    def forward(self, x):
        x = self.reflection_pad(x)
        x = self.conv_layer(x)
        if self.norm_type == "instance":
            out = self.norm_layer(x)
        else:
            out = x
        return out


class ResidualLayer(nn.Module):

    def __init__(self, channels=128, kernel_size=3):
        super(ResidualLayer, self).__init__()
        self.conv1 = ConvLayer(channels, channels, kernel_size, stride=1)
        self.relu = nn.ReLU()
        self.conv2 = ConvLayer(channels, channels, kernel_size, stride=1)

    def forward(self, x):
        id = x
        out = self.relu(self.conv1(x))
        out = self.conv2(out)
        return out + id


class DeconvLayer(nn.Module):

    def __init__(self, in_channels, out_channels, kernel_size, stride, output_padding):
        super(DeconvLayer, self).__init__()

        padding_size = kernel_size // 2
        self.conv_transpose = nn.ConvTranspose2d(in_channels, out_channels, kernel_size, stride, padding_size, output_padding)
        self.norm_layer = nn.InstanceNorm2d(out_channels, affine=True)

    def forward(self, x):
        x = self.conv_transpose(x)
        out = self.norm_layer(x)
        return out