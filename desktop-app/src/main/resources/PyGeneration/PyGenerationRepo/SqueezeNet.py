import torch
import torchvision
import matplotlib.pyplot as plt
import numpy as np

from utils import Utils


class SqueezeNet:

    def __init__(self):
        self.net = torchvision.models.squeezenet1_1(pretrained=True).features
        self.net.type(Utils.getDtype())
        for param in self.net.parameters():
            param.requires_grad = False
        self.init_hyper_params()

    def generate(self, content_img, style_img, content_size=192, style_size=512, init_random=False):
        self.CONTENT_SIZE = content_size
        self.STYLE_SIZE = style_size

        content_img = Utils.preprocess(content_img, size=self.CONTENT_SIZE)
        style_img = Utils.preprocess(style_img, size=self.STYLE_SIZE)

        features = Utils.extract_features(content_img, self.net)
        content_target = features[self.CONTENT_LAYER].clone()

        features = Utils.extract_features(style_img, self.net)
        style_activations = []
        for index in self.STYLE_LAYERS:
            style_activations.append(Utils.gramMatrix(features[index].clone()))

        if init_random:
            img = torch.Tensor(content_img.size()).uniform_(0, 1).type(Utils.getDtype())
        else:
            img = content_img.clone().type(Utils.getDtype())

        img.requires_grad_()

        optimizer = torch.optim.Adam([img], lr=self.LEARNING_RATE)
        # test with pyplot
        f, axarr = plt.subplots(1, 2)
        axarr[0].axis('off')
        axarr[1].axis('off')
        axarr[0].set_title('Content Source Img.')
        axarr[1].set_title('Style Source Img.')
        axarr[0].imshow(Utils.deprocess(content_img.cpu()))
        axarr[1].imshow(Utils.deprocess(style_img.cpu()))
        plt.show()
        plt.figure()

        for t in range(self.ITERATIONS):
            if t < (self.ITERATIONS / 2):
                img.data.clamp_(-1.5, 1.5)
            optimizer.zero_grad()

            forward_activations = Utils.extract_features(img, self.net)

            content_loss = Utils.contentLoss(self.CONTENT_WEIGHT, forward_activations[self.CONTENT_LAYER], content_target)
            style_loss = Utils.styleLoss(forward_activations, self.STYLE_LAYERS, style_activations, self.STYLE_WEIGHTS)
            tv_loss = Utils.tvLoss(img, self.TV_WEIGHT)
            loss = content_loss + style_loss + tv_loss

            loss.backward()

            if t == self.DECAY_LR_AT:
                optimizer = torch.optim.Adam([img], lr=self.DECAY_LR)
            optimizer.step()

            if t%50==0 and self.verbose:
                self.show_image(img, t)
        self.show_image(img, t)
        self.generated_image = np.array(Utils.deprocess(img.data.cpu()))


    def init_hyper_params(self):
        self.CONTENT_LAYER = 3
        self.STYLE_LAYERS = [1, 4, 6, 7]
        self.LEARNING_RATE = 3.0
        self.DECAY_LR = 0.1
        self.DECAY_LR_AT = 180
        self.ITERATIONS = 400
        self.CONTENT_WEIGHT = 6e-2
        self.STYLE_WEIGHTS = [300000, 1000, 15, 3]
        self.TV_WEIGHT = 2.5e-2
        self.verbose = True

    def show_image(self, img, iteration):
        print('Iteration {}'.format(iteration))
        plt.axis('off')
        plt.imshow(Utils.deprocess(img.data.cpu()))
        plt.show()

    def get_image(self):
        return self.generated_image